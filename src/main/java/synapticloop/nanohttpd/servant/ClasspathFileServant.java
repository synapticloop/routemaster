package synapticloop.nanohttpd.servant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public class ClasspathFileServant extends StaticFileServant {
	private static final Logger LOGGER = Logger.getLogger(ClasspathFileServant.class.getSimpleName());

	public ClasspathFileServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		if(httpSession.getMethod() == Method.GET) {
			return(serveResource(rootDir, httpSession));
		} else {
			return(HttpUtils.methodNotAllowedResponse());
		}
	}

	protected Response serveResource(File rootDir, IHTTPSession httpSession) {
		Map<String, String> headers = httpSession.getHeaders();
		String uri = HttpUtils.cleanUri(httpSession.getUri());
		if(uri.endsWith("/")) {
			uri = getIndexFileName(uri);
		}

		if(null == uri) {
			return(HttpUtils.notFoundResponse());
		}

		int lastIndexOf = uri.lastIndexOf(".");
		String extension = uri.substring(lastIndexOf + 1);

		String mimeType = NanoHTTPD.MIME_HTML;
		// try and get the handler

		Map<String, Handler> handlerCache = RouteMaster.getHandlerCache();
		if(handlerCache.containsKey(extension)) {
			Handler handler = handlerCache.get(extension);
			if(handler.canServeUri(uri)) {
				return(handler.serveFile(rootDir, uri, headers, httpSession));
			}
		}

		if(MimeTypeMapper.getMimeTypes().containsKey(extension)) {
			mimeType = MimeTypeMapper.getMimeTypes().get(extension);
		}

		byte[] bytes = null;

		InputStream resourceAsStream = this.getClass().getResourceAsStream(uri);
		if(null == resourceAsStream) {
			return(RouteMaster.get404Response(rootDir, httpSession));
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			int read = resourceAsStream.read();
			while (read != -1) {
				byteArrayOutputStream.write(read);
				read = resourceAsStream.read();
			}
		} catch (IOException ioex) {
			return(RouteMaster.get500Response(rootDir, httpSession));
		}
		bytes = byteArrayOutputStream.toByteArray();

		return getResponse(headers, uri, mimeType, bytes);
	}

	protected Response getResponse(Map<String, String> headers, String uri, String mimeType, byte[] bytes) {
		// etag first
		String etag = Integer.toHexString((uri).hashCode());
		// Support (simple) skipping:
		long startFrom = 0;
		long endAt = -1;
		String range = headers.get("range");
		if (range != null && range.startsWith("bytes=")) {
			range = range.substring("bytes=".length());
			int minus = range.indexOf('-');
			try {
				if (minus > 0) {
					startFrom = Long.parseLong(range.substring(0, minus));
					endAt = Long.parseLong(range.substring(minus + 1));
				}
			} catch (NumberFormatException ignored) {
				// do nothing
			}
		}

		// Change return code and add Content-Range header when skipping is requested
		return(addHeaders(headers, mimeType, bytes, etag, startFrom, endAt, range));
	}

	protected Response addHeaders(Map<String, String> headers, String mimeType, byte[] bytes, String etag, long startFrom, long endAt, String range) {
		int bytesLength = bytes.length;
		Response res;
		if (range != null && startFrom >= 0) {
			if (startFrom >= bytesLength) {
				res = HttpUtils.rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, "");
				res.addHeader("Content-Range", "bytes 0-0/" + bytesLength);
				res.addHeader("ETag", etag);
			} else {
				if (endAt < 0) {
					endAt = (long)bytesLength - 1;
				}
				long newLen = endAt - startFrom + 1;
				if (newLen < 0) {
					newLen = 0;
				}

				final long dataLen = newLen;
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes) {
					@Override
					public int available(){
						return (int) dataLen;
					}
				};

				long skip = byteArrayInputStream.skip(startFrom);
				if(skip != startFrom) {
					LOGGER.severe("Tried to skip: " + startFrom + " bytes, but actualy skipped: " + skip + " bytes");
				}

				res = HttpUtils.partialContentResponse(mimeType, byteArrayInputStream, (long)dataLen);
				res.addHeader("Content-Length", "" + Long.toString(dataLen));
				res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + bytesLength);
				res.addHeader("ETag", etag);
			}
		} else {
			if (etag.equals(headers.get("if-none-match")))
				res = HttpUtils.notModifiedResponse(mimeType, "");
			else {
				res = HttpUtils.okResponse(mimeType, new ByteArrayInputStream(bytes), (long)bytes.length);
				res.addHeader("Content-Length", "" + Integer.toString(bytesLength));
				res.addHeader("ETag", etag);
			}
		}
		return res;
	}

	protected String getIndexFileName(String uri) {
		// time to check the indexFiles
		Set<String> indexFiles = RouteMaster.getIndexFiles();
		for (String indexFile : indexFiles) {
			String possibleIndexFile = uri + indexFile;
			if(null != this.getClass().getResourceAsStream(possibleIndexFile)) {
				return(possibleIndexFile);
			}
		}
		return(null);
	}

	protected byte[] getBytes(String uri) throws IOException {

		InputStream resourceAsStream = this.getClass().getResourceAsStream(uri);
		if(null == resourceAsStream) {
			throw new IOException("Could not read '" + uri + "'.");
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			int read = resourceAsStream.read();
			while (read != -1) {
				byteArrayOutputStream.write(read);
				read = resourceAsStream.read();
			}
		} catch (IOException ioex) {
			throw new IOException("Could not read '" + uri + "'.", ioex);
		} finally {
			byteArrayOutputStream.close();
			resourceAsStream.close();
		}

		return(byteArrayOutputStream.toByteArray());

	}

}