package synapticloop.nanohttpd.example.servant;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.router.Router;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.TemplarHelper;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import synapticloop.templar.utils.TemplarContext;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class RouteMasterRestServant extends RestRoutable {
	private static final String ROUTER_SNIPPET_TEMPLAR = "src/main/html/templar/router-snippet.templar";
	private static final Logger LOGGER = Logger.getLogger(RouteMasterRestServant.class.getName());

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
		TemplarContext templarContext = new TemplarContext();

		if(routable instanceof RestRoutable) {
			templarContext.add("type", "REST");
		} else {
			templarContext.add("type", "Route");
		}
		if(isWildcard) {
			templarContext.add("route", router.getRoute() + "*");
		} else {
			templarContext.add("route", router.getRoute());
		}
		templarContext.add("class", routable.getClass().getCanonicalName());
		try {
			Parser parser = TemplarHelper.getParser(ROUTER_SNIPPET_TEMPLAR);
			content.append(parser.render(templarContext));
		} catch (ParseException pex) {
			LOGGER.log(Level.SEVERE, "Could not parse '" + ROUTER_SNIPPET_TEMPLAR + "'.", pex);
		} catch (RenderException rex) {
			LOGGER.log(Level.SEVERE, "Could not parse '" + ROUTER_SNIPPET_TEMPLAR + "'.", rex);
		}
	}
}
