package synapticloop.nanohttpd.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class Router {
	private HashMap<String, Router> routerMap = new HashMap<String, Router>();
	private Routable wildcardRoute = null;
	private Routable defaultRoute = null;
	private String route = null;

	public Router(String route, StringTokenizer stringTokenizer, String routerClass) {
		addRoute(route, stringTokenizer, routerClass);
	}

	public Router(String route, StringTokenizer stringTokenizer, String routerClass, ArrayList<String> params) {
		addRestRoute(route, stringTokenizer, routerClass, params);
	}

	public void addRoute(String route, StringTokenizer stringTokenizer, String routerClass) {

		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(token.equals("*")) {
				try {
					this.route = route.substring(0, route.length() -1);
					wildcardRoute = (Routable) Routable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class).newInstance(this.route);
					
					// this should also bind to the default route (as the * should also 
					// map to an empty string) - this will be over-ridden later if another
					// binding is found
					if(null == defaultRoute) {
						defaultRoute = (Routable) Routable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class).newInstance(this.route);
					}
				} catch (Exception ex) {
					SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
				}
			} else {
				if(routerMap.containsKey(token)) {
					routerMap.get(token).addRoute(route, stringTokenizer, routerClass);
				} else {
					routerMap.put(token, new Router(route, stringTokenizer, routerClass));
				}
			}
		} else {
			try {
				this.route = route;
				defaultRoute = (Routable) Routable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class).newInstance(this.route);
			} catch (Exception ex) {
				SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
			}
		}
	}

	public void addRestRoute(String route, StringTokenizer stringTokenizer, String routerClass, ArrayList<String> params) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(token.equals("*")) {
				try {
					this.route = route.substring(0, route.length() -1);
					wildcardRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, ArrayList.class).newInstance(this.route, params);
					
					// this should also bind to the default route (as the * should also 
					// map to an empty string) - this will be over-ridden later if another
					// binding is found
					if(null == defaultRoute) {
						defaultRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, ArrayList.class).newInstance(this.route, params);
					}
				} catch (Exception ex) {
					SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
				}
			} else {
				if(routerMap.containsKey(token)) {
					routerMap.get(token).addRestRoute(route, stringTokenizer, routerClass, params);
				} else {
					routerMap.put(token, new Router(route, stringTokenizer, routerClass, params));
				}
			}
		} else {
			try {
				this.route = route;
				defaultRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, ArrayList.class).newInstance(this.route, params);
			} catch (Exception ex) {
				SimpleLogger.logFatal("Could not instantiate the default route for '" + route + "'.", ex);
			}
		}
	}

	/**
	 * Get the 'Routable' for the incoming URI
	 * 
	 * @param httpSession The user's session
	 * @param stringTokenizer the stringTokenizer for the route paths
	 * @return the Routable that responds to this uri
	 */
	public Routable route(IHTTPSession httpSession, StringTokenizer stringTokenizer) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if(routerMap.containsKey(token)) {
				return(routerMap.get(token).route(httpSession, stringTokenizer));
			} else {

				// do we have a wildcard route at this point?
				if(null != wildcardRoute) {
					return(wildcardRoute);
				} else {
					return(null);
				}
			}
		} else {
			// do we have a default route?
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
			if(defaultRoute instanceof RestRoutable) {
				SimpleLogger.logInfo("/ REST: " + route + " => " + defaultRoute.getClass().getCanonicalName());
			} else {
				SimpleLogger.logInfo("/ Route: " + route + " => " + defaultRoute.getClass().getCanonicalName());
			}
		}

		if(null != wildcardRoute) {
			if(wildcardRoute instanceof RestRoutable) {
				SimpleLogger.logInfo("* REST: " + route + " => " + wildcardRoute.getClass().getCanonicalName());
			} else {
				SimpleLogger.logInfo("* Route: " + route + " => " + wildcardRoute.getClass().getCanonicalName());
			}
		}

		// go through and print all of the other routes
		Iterator<String> keySet = routerMap.keySet().iterator();
		while (keySet.hasNext()) {
			String next = (String) keySet.next();
			routerMap.get(next).printRoutes();
		}
	}

	public HashMap<String, Router> getRouterMap() { return routerMap; }
	public Routable getWildcardRoute() { return wildcardRoute; }
	public Routable getDefaultRoute() { return defaultRoute; }
	public String getRoute() { return route; }
}
