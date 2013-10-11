package synapticloop.nanohttpd.servant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.router.Router;
import synapticloop.nanohttpd.utils.HttpUtils;

public class RouteMasterRestServant extends RestRoutable {

	public RouteMasterRestServant(String routeContext, ArrayList<String> params) {
		super(routeContext, params);
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		String method = restParams.get("method");
		StringBuilder content = new StringBuilder();

		if(method.equals("routes")) {
			Router router = RouteMaster.getRouter();
			printRouter(content, router);
			return(HttpUtils.okResponseHtml(content.toString()));
		} else if (method.equals("cache")) {
			printCache(content);
			return(HttpUtils.okResponseHtml(content.toString()));
		}

		return(HttpUtils.okResponseHtml(this.getClass().getName() + " [ " + method + " ] request: says OK, with method '" + method + "'"));
	}

	private void printCache(StringBuilder content) {
		ConcurrentHashMap<String,Routable> routerCache = RouteMaster.getRouterCache();
		for (Iterator<String> iterator = routerCache.keySet().iterator(); iterator.hasNext();) {
			String uri = (String)iterator.next();
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

		HashMap<String,Router> routerMap = router.getRouterMap();
		Collection<Router> values = routerMap.values();
		for (Iterator<Router> iterator = values.iterator(); iterator.hasNext();) {
			Router subRouter = (Router) iterator.next();
			printRouter(content, subRouter);
		}
	}

	private void printRoutable(StringBuilder content, Router router, Routable routable, boolean isWildcard) {
		content.append("<p>");
		if(routable instanceof RestRoutable) {
			content.append("REST:");
		} else {
			content.append("Route:");
		}
		content.append(" <strong>");
		content.append(router.getRoute());
		if(isWildcard) {
			content.append("*");
		}
		content.append("</strong> =&gt; ");

		content.append(routable.getClass().getCanonicalName());
		content.append("</p>");

		content.append("<p>");
	}
}
