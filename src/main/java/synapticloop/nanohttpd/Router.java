package synapticloop.nanohttpd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import synapticloop.nanohttpd.logger.Logger;

public class Router {
	private HashMap<String, Router> ROUTER = new HashMap<String, Router>();
	private IRouter wildcardRoute = null;
	private IRouter defaultRoute = null;
	private String route = null;

	public Router(String route, StringTokenizer stringTokenizer, String routerClass) {
		addRoute(route, stringTokenizer, routerClass);
	}

	public void addRoute(String route, StringTokenizer stringTokenizer, String routerClass) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(token.equals("*")) {
				try {
					wildcardRoute = (IRouter) IRouter.class.getClassLoader().loadClass(routerClass).newInstance();
					this.route = route.substring(0, route.length() -1);
				} catch (Exception ex) {
					Logger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
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
				defaultRoute = (IRouter) IRouter.class.getClassLoader().loadClass(routerClass).newInstance();
				this.route = route;
			} catch (Exception ex) {
				Logger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
			}
		}
	}

	public void printRoutes() {
		if(null != defaultRoute) {
			Logger.logInfo("/ Route: " + route + " => " + defaultRoute.getClass().getCanonicalName());
		}
		if(null != wildcardRoute) {
			Logger.logInfo("* Route: " + route + " => " + wildcardRoute.getClass().getCanonicalName());
		}

		// go through and print all of the other routes
		Iterator<String> keySet = ROUTER.keySet().iterator();
		while (keySet.hasNext()) {
			String next = (String) keySet.next();
			ROUTER.get(next).printRoutes();
		}
	}
}
