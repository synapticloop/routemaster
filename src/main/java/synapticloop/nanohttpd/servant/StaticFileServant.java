package synapticloop.nanohttpd.servant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class StaticFileServant extends Routable {
	public StaticFileServant(String routeContext) {
		super(routeContext);
	}


	public Response serve(File rootDir, IHTTPSession httpSession) {
		String uri = HttpUtils.cleanUri(httpSession.getUri());
		File file = new File(rootDir.getAbsolutePath() + uri);
		if(!file.exists()) {
			// 404
			File indexFile = getIndexFile(rootDir, uri);
			if(null != indexFile) {
				file = indexFile;
			} else {
				return(RouteMaster.get404Response(rootDir, httpSession));
			}
		}

		if(!file.canRead()) {
			return(HttpUtils.forbiddenResponse());
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

		if(MimeTypeMapper.getMimeTypes().containsKey(extension)) {
			mimeType = MimeTypeMapper.getMimeTypes().get(extension);
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

					res = HttpUtils.partialContentResponse(mimeType, fis);
					res.addHeader("Content-Length", "" + dataLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = HttpUtils.notModifiedResponse(mimeType, "");
				else {
					res = HttpUtils.okResponse(mimeType, new FileInputStream(file));
					res.addHeader("Accept-Ranges", "bytes");
					res.addHeader("Content-Length", "" + fileLen);
					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			
			res = HttpUtils.forbiddenResponse();
			res.addHeader("Accept-Ranges", "bytes");
		}
		return res;
	}

}
