package synapticloop.nanohttpd.router;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import synapticloop.nanohttpd.logger.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class Router {
	private HashMap<String, Router> ROUTER = new HashMap<String, Router>();
	private Routable wildcardRoute = null;
	private Routable defaultRoute = null;
	private String route = null;

	public Router(String route, StringTokenizer stringTokenizer, String routerClass) {
		addRoute(route, stringTokenizer, routerClass);
	}

	public void addRoute(String route, StringTokenizer stringTokenizer, String routerClass) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(token.equals("*")) {
				try {
					wildcardRoute = (Routable) Routable.class.getClassLoader().loadClass(routerClass).newInstance();
					this.route = route.substring(0, route.length() -1);
				} catch (Exception ex) {
					SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
				}
			} else {
				if(ROUTER.containsKey(token)) {
					ROUTER.get(token).addRoute(route, stringTokenizer, routerClass);
				} else {
					ROUTER.put(token, new Router(route, stringTokenizer, routerClass));
				}
			}
		} else {
			try {
				defaultRoute = (Routable) Routable.class.getClassLoader().loadClass(routerClass).newInstance();
				this.route = route;
			} catch (Exception ex) {
				SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
			}
		}
	}

	public Routable route(IHTTPSession httpSession, StringTokenizer stringTokenizer) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(ROUTER.containsKey(token)) {
				return(ROUTER.get(token).route(httpSession, stringTokenizer));
			} else {
				// do we have a wildcard route at this point?
				if(null != wildcardRoute) {
					return(wildcardRoute);
				} else {
					return(null);
				}
			}
		} else {
			// do we have a default rout?
			if(null != defaultRoute) {
				return(defaultRoute);
			} else {
				// return 404 perhaps
				return(null);
			}
		}
	}

	public void printRoutes() {
		if(null != defaultRoute) {
			SimpleLogger.logInfo("/ Route: " + route + " => " + defaultRoute.getClass().getCanonicalName());
		}
		if(null != wildcardRoute) {
			SimpleLogger.logInfo("* Route: " + route + " => " + wildcardRoute.getClass().getCanonicalName());
		}

		// go through and print all of the other routes
		Iterator<String> keySet = ROUTER.keySet().iterator();
		while (keySet.hasNext()) {
			String next = (String) keySet.next();
			ROUTER.get(next).printRoutes();
		}
	}
}
