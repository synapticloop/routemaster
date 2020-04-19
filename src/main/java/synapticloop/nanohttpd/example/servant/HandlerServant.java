package synapticloop.nanohttpd.example.servant;

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
import java.util.Iterator;
import java.util.Map;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HandlerServant extends Routable {

	public HandlerServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		StringBuilder content = new StringBuilder();
		Map<String, Handler> handlerCache = RouteMaster.getHandlerCache();

		for (Iterator<String> iterator = handlerCache.keySet().iterator(); iterator.hasNext();) {
			String extension = iterator.next();
			Handler plugin = handlerCache.get(extension);

			content.append("<p> extension: <strong>.");
			content.append(extension);
			content.append("</strong> =&gt; ");
			content.append(plugin.getName());
			content.append("</p>");
		}

		return(HttpUtils.okResponse(content.toString()));
	}

}
