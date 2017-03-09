package synapticloop.nanohttpd.example.servant;

/*
 * Copyright (c) 2013-2017 synapticloop.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.router.Router;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class RouteMasterRestServant extends RestRoutable {

	public RouteMasterRestServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		String method = restParams.get("method");
		StringBuilder content = new StringBuilder();

		if(method != null) {
			if("routes".equals(method)) {
				Router router = RouteMaster.getRouter();
				printRouter(content, router);
				return(HttpUtils.okResponse(content.toString()));
			} else if ("cache".equals(method)) {
				printCache(content);
				return(HttpUtils.okResponse(content.toString()));
			}
		}

		return(HttpUtils.okResponse(this.getClass().getName() + " [ " + method + " ] request: says OK, with method '" + method + "'"));
	}

	private void printCache(StringBuilder content) {
		Map<String,Routable> routerCache = RouteMaster.getRouterCache();
		for (Iterator<String> iterator = routerCache.keySet().iterator(); iterator.hasNext();) {
			String uri = iterator.next();
			Routable routable = routerCache.get(uri);
			content.append("<p> Cached: <strong>");
			content.append(uri);
			content.append("</strong> =&gt; ");
			content.append(routable.getClass().getCanonicalName());
			content.append("</p>");
		}
	}

	private void printRouter(StringBuilder content, Router router) {
		// now get all of the other routes

		Routable defaultRoute = router.getDefaultRoute();
		if(null != defaultRoute) {
			printRoutable(content, router, defaultRoute, false);
		}


		Routable wildcardRoute = router.getWildcardRoute();
		if(null != wildcardRoute) {
			printRoutable(content, router, wildcardRoute, true);
		}

		Map<String,Router> routerMap = router.getRouterMap();
		Collection<Router> values = routerMap.values();
		for (Iterator<Router> iterator = values.iterator(); iterator.hasNext();) {
			Router subRouter = iterator.next();
			printRouter(content, subRouter);
		}
	}

	private void printRoutable(StringBuilder content, Router router, Routable routable, boolean isWildcard) {
		StringBuilder stringBuilder = new StringBuilder();
		// <p>{type}: <strong>{route}</strong> =&gt; {class}</p>
		stringBuilder.append("<p>");
		if(routable instanceof RestRoutable) {
			stringBuilder.append("REST");
		} else {
			stringBuilder.append("Route");
		}
		stringBuilder.append(": <strong>");

		stringBuilder.append(router.getRoute() + "*");
		if(isWildcard) {
			stringBuilder.append("*");
		}
		stringBuilder.append("</strong> =&gt; ");
		stringBuilder.append(routable.getClass().getCanonicalName());
		stringBuilder.append("</p>");
		content.append(stringBuilder.toString());
	}
}
