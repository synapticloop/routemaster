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

import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class ModulesRouteServant extends Routable {

	public ModulesRouteServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		StringBuilder content = new StringBuilder();
		Iterator<String> iterator = RouteMaster.getModules().iterator();
		while (iterator.hasNext()) {
			String module = (String) iterator.next();
			content.append("<p>");
			content.append(module);
			content.append("</p>");
		}
		return(HttpUtils.okResponse(content.toString()));
	}
}
