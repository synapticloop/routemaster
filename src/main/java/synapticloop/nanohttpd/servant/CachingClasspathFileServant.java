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
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public class CachingClasspathFileServant extends ClasspathFileServant {
	private static Map<String, byte[]> contentCache = new ConcurrentHashMap<String, byte[]>();
	private static Map<String, String> mimeTypeCache = new ConcurrentHashMap<String, String>();

	public CachingClasspathFileServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		if(httpSession.getMethod() == Method.GET) {
			String uri = httpSession.getUri();
			if(!contentCache.containsKey(uri)) {
				String tempUri = HttpUtils.cleanUri(uri);

				if(tempUri.endsWith("/")) {
					tempUri = getIndexFileName(uri);
				}

				String mimeType = HttpUtils.getMimeType(uri);
				mimeTypeCache.put(uri, mimeType);

				byte[] bytes;
				try {
					bytes = getBytes(tempUri);
				} catch (IOException ioex) {
					return(HttpUtils.internalServerErrorResponse("Could not read '" + tempUri + "'."));
				}
				contentCache.put(uri, bytes);
			}

			// now serve up the response
			return(getResponse(httpSession.getHeaders(), uri, mimeTypeCache.get(uri), contentCache.get(uri)));

		} else {
			return(HttpUtils.methodNotAllowedResponse());
		}
	}
}
