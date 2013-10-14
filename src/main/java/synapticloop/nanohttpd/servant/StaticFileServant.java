package synapticloop.nanohttpd.servant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class StaticFileServant extends Routable {

	private static final String MIMETYPES_PROPERTIES = "mimetypes.properties";

	public StaticFileServant(String routeContext) {
		super(routeContext);
	}

	private static HashMap<String, String> MIME_TYPES = new HashMap<String, String>();
	static {
		Properties properties = new Properties();
		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + MIMETYPES_PROPERTIES);

		// maybe it is in the current working directory
		if(null == inputStream) {
			File mimetypesFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + MIMETYPES_PROPERTIES);
			if(mimetypesFile.exists() && mimetypesFile.canRead()) {
				try {
					inputStream = new BufferedInputStream(new FileInputStream(mimetypesFile));
				} catch (FileNotFoundException fnfex) {
				}
			}
		} else {
			try {
				properties.load(inputStream);
				Enumeration<Object> keys = properties.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					MIME_TYPES.put(key, properties.getProperty(key));
				}
			} catch (IOException ioex) {
				SimpleLogger.logFatal("Could not load the '" + MIMETYPES_PROPERTIES + "' file, ignoring.", ioex);
			}
		}

		Iterator<String> iterator = MIME_TYPES.keySet().iterator();
		SimpleLogger.logInfo("Registered mime types:");
		SimpleLogger.logInfo("======================");
		if(!iterator.hasNext()) {
			SimpleLogger.logInfo("None!!! - perhaps you need to have a " + MIMETYPES_PROPERTIES + " file in the current directory?");
		}

		while (iterator.hasNext()) {
			String mimeType = (String) iterator.next();
			SimpleLogger.logInfo(mimeType + " => " + MIME_TYPES.get(mimeType));
		}
	}

	public Response serve(File rootDir, IHTTPSession httpSession) {
		String uri = HttpUtils.cleanUri(httpSession.getUri());
		File file = new File(rootDir.getAbsolutePath() + uri);
		if(!file.exists()) {
			// 404
			File indexFile = getIndexFile(rootDir, uri);
			if(null != indexFile) {
				file = indexFile;
			}
		}

		if(!file.canRead()) {
			// return 403 - or should we just ignore
		}

		if(file.isDirectory()) {
			// do we want to serve directory files???
			File indexFile = getIndexFile(rootDir, uri);
			if(null != indexFile) {
				file = indexFile;
			}
		} else {
			// is a file - 
		}

		String absolutePath = file.getAbsolutePath();
		if(file.exists() && file.canRead()) {
			int lastIndexOf = absolutePath.lastIndexOf(".");
			if(lastIndexOf != -1) {
				// have a file here
				return(serveFile(file, httpSession.getHeaders(), absolutePath.substring(lastIndexOf + 1)));
			} else {
				// default is to do text/plain
			}
		}

		return(null);
	}

	private File getIndexFile(File rootDir, String uri) {
		// time to check the indexFiles
		HashSet<String> indexFiles = RouteMaster.getIndexFiles();
		for (String indexFile : indexFiles) {
			File file = new File(rootDir.getAbsolutePath() + uri + "/" + indexFile);
			if(file.exists()) {
				return(file);
			}
		}

		return(null);
	}

	private Response serveFile(File file, Map<String, String> header, String extension) {
		String mimeType = NanoHTTPD.MIME_HTML;
		Response res = null;

		if(MIME_TYPES.containsKey(extension)) {
			mimeType = MIME_TYPES.get(extension);
		}

		try {

			// etag first
			String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());
			// Support (simple) skipping:
			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}

			// Change return code and add Content-Range header when skipping is requested
			long fileLen = file.length();
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = HttpUtils.rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
					res.addHeader("ETag", etag);
				} else {
					if (endAt < 0) {
						endAt = fileLen - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					final long dataLen = newLen;
					FileInputStream fis = new FileInputStream(file) {
						@Override
						public int available() throws IOException {
							return (int) dataLen;
						}
					};
					fis.skip(startFrom);

					res = createResponse(Response.Status.PARTIAL_CONTENT, mimeType, fis);
					res.addHeader("Content-Length", "" + dataLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = createResponse(Response.Status.NOT_MODIFIED, mimeType, "");
				else {
					res = createResponse(Response.Status.OK, mimeType, new FileInputStream(file));
					res.addHeader("Content-Length", "" + fileLen);
					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			res = createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
		}

		return res;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType, InputStream message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private Response createResponse(Response.Status status, String mimeType, String message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}
}
