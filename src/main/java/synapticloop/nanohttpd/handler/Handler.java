package synapticloop.nanohttpd.handler;

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
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.nanohttpd.utils.MimeTypeMapper;

/**
 * A handler registers itself against a specific extension and will respond to
 * all requests that end with the extension.  In the case where a directory is
 * requested, then it will go through the 'options.indexfiles' configuration 
 * property.  You __MUST__ register your handler as one of the indexfiles, else 
 * it won't be picked up.  I.e. for the templar handler, you will need to put in
 * an 'index.html.templar' as one of the values for the indexfiles.
 *
 */
public abstract class Handler {
	/**
	 * Return whether this handler can serve the requested URI
	 * 
	 * @param uri the URI to check
	 * 
	 * @return whether this handler can serve the requested URI
	 */
	public abstract boolean canServeUri(String uri);
	
	/**
	 * If the handler can serve this URI, then it will be requested to serve up 
	 * the content
	 * 
	 * @param rootDir The root directory where the routemaster was started from
	 * @param uri The URI that is requested to be served
	 * @param headers The headers that are passed through
	 * @param session The session object
	 * 
	 * @return The response that will be sent back to the clien
	 */
	public abstract Response serveFile(File rootDir, String uri, Map<String, String> headers, IHTTPSession session);

	/**
	 * Get the name of the handler
	 *  
	 * @return the name of the handler (by default the canonical name of this class)
	 */
	public String getName() {
		return(this.getClass().getCanonicalName());
	}

	/**
	 * Get the mimetype for the handler file.  This will return the mimetype from 
	 * the file that is requested, by using the fileName.mimeType.handlerExtension.
	 * 
	 * For example - index.html.templar will look up the mime type mappings for 
	 * 'html', and return the mimetype (if one exists).  Else it will return null
	 * 
	 * @param uri the URI to lookup
	 * 
	 * @return the mime type if found in the lookup table, else null
	 */
	protected String getMimeType(String uri) {
		String[] split = uri.split("\\.");
		int length = split.length;

		if(length > 2) {
			// the last one is .templar
			// the second last one is the mime type we need to lookup
			return(MimeTypeMapper.getMimeTypes().get(split[length -2]));
		}
		return(null);
	}

}
