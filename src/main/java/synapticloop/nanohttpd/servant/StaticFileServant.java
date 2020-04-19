package synapticloop.nanohttpd.servant;

/*
 * Copyright (c) 2013-2020 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class StaticFileServant extends Routable {
	private static final Logger LOGGER = Logger.getLogger(StaticFileServant.class.getSimpleName());

	public StaticFileServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		boolean indexFileRequested = false;
		Map<String, Handler> handlerCache = RouteMaster.getHandlerCache();

		String uri = HttpUtils.cleanUri(httpSession.getUri());
		File file = new File(rootDir.getAbsolutePath() + uri);
		if(!file.exists()) {
			// 404
			File indexFile = getIndexFile(rootDir, uri);
			if(null != indexFile) {
				file = indexFile;
				indexFileRequested = true;
			} else {
				// maybe this can be handled by a handler... (it may be a virtual file)
				int lastIndexOf = uri.lastIndexOf(".");
				String extension = uri.substring(lastIndexOf + 1);
				if(handlerCache.containsKey(extension)) {
					Handler handler = handlerCache.get(extension);
					String fileName = file.getName();
					if(handler.canServeUri(fileName)) {
						if(indexFileRequested) {
							return(handler.serveFile(rootDir, uri + fileName, httpSession.getHeaders(), httpSession));
						} else {
							return(handler.serveFile(rootDir, uri, httpSession.getHeaders(), httpSession));
						}
					}
				}

				// at this point we cannot handle the response
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
				indexFileRequested = true;
			}
		}

		String absolutePath = file.getAbsolutePath();
		// at this point we have a file and we now need to check whether we need a handler

		int lastIndexOf = absolutePath.lastIndexOf(".");
		String extension = absolutePath.substring(lastIndexOf + 1);

		if(handlerCache.containsKey(extension)) {
			Handler handler = handlerCache.get(extension);
			String fileName = file.getName();
			if(handler.canServeUri(fileName)) {
				if(indexFileRequested) {
					return(handler.serveFile(rootDir, uri + fileName, httpSession.getHeaders(), httpSession));
				} else {
					return(handler.serveFile(rootDir, uri, httpSession.getHeaders(), httpSession));
				}
			}
		}

		// at this point - we haven't been handled by a handler - need to serve the file
		if(file.exists() && file.canRead()) {
			if(lastIndexOf != -1) {
				// have a file here
				return(serveFile(file, httpSession.getHeaders(), absolutePath.substring(lastIndexOf + 1)));
			} else {
				// default is to do text/plain
			}
		}

		return(null);
	}

	private static File getIndexFile(File rootDir, String uri) {
		// time to check the indexFiles
		Set<String> indexFiles = RouteMaster.getIndexFiles();
		for (String indexFile : indexFiles) {
			File file = new File(rootDir.getAbsolutePath() + uri + "/" + indexFile);
			if(file.exists()) {
				return(file);
			}
		}

		return(null);
	}

	private static Response serveFile(File file, Map<String, String> header, String extension) {
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
			long fileLen = file.length();
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = HttpUtils.rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + Long.toString(fileLen));
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

					long skip = fis.skip(startFrom);
					if(skip != startFrom) {
						LOGGER.severe("Tried to skip: " + startFrom + " bytes, but actualy skipped: " + skip + " bytes");
					}

					res = HttpUtils.partialContentResponse(mimeType, fis, dataLen);
					res.addHeader("Content-Length", "" + Long.toString(dataLen));
					res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match"))) {
					res = HttpUtils.notModifiedResponse(mimeType, "");
				} else {
					res = HttpUtils.okResponse(mimeType, new FileInputStream(file), file.length());
					res.addHeader("Accept-Ranges", "bytes");
					res.addHeader("Content-Length", "" + Long.toString(fileLen));
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
