package synapticloop.nanohttpd.router;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class Router {
	private static final String COULD_NOT_INSTANTIATE_THE_DEFAULT_ROUTE_FOR = "Could not instantiate the default route for '";
	private HashMap<String, Router> routerMap = new HashMap<String, Router>();
	private Routable wildcardRoute = null;
	private Routable defaultRoute = null;
	private String route = null;

	public Router(String route, StringTokenizer stringTokenizer, String routerClass) {
		addRoute(route, stringTokenizer, routerClass);
	}

	public Router(String route, StringTokenizer stringTokenizer, String routerClass, List<String> params) {
		addRestRoute(route, stringTokenizer, routerClass, params);
	}

	public void addRoute(String route, StringTokenizer stringTokenizer, String routerClass) {

		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if("*".equals(token)) {
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
					SimpleLogger.logFatal(COULD_NOT_INSTANTIATE_THE_DEFAULT_ROUTE_FOR + route + "'.", ex);
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
			} catch (Throwable th) {
				// here we catch throwable - as we are using reflection...
				SimpleLogger.logFatal(COULD_NOT_INSTANTIATE_THE_DEFAULT_ROUTE_FOR + route + "'.", th);
			}
		}
	}

	public void addRestRoute(String route, StringTokenizer stringTokenizer, String routerClass, List<String> params) {
		if(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if("*".equals(token)) {
				try {
					this.route = route.substring(0, route.length() -1);
					wildcardRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, List.class).newInstance(this.route, params);

					// this should also bind to the default route (as the * should also 
					// map to an empty string) - this will be over-ridden later if another
					// binding is found
					if(null == defaultRoute) {
						defaultRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, List.class).newInstance(this.route, params);
					}
				} catch (Throwable th) {
					SimpleLogger.logFatal(COULD_NOT_INSTANTIATE_THE_DEFAULT_ROUTE_FOR + route + "'.", th);
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
				defaultRoute = (Routable) RestRoutable.class.getClassLoader().loadClass(routerClass).getConstructor(String.class, List.class).newInstance(this.route, params);
			} catch (Exception ex) {
				SimpleLogger.logFatal(COULD_NOT_INSTANTIATE_THE_DEFAULT_ROUTE_FOR + route + "'.", ex);
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

	public Map<String, Routable> getRouters() {
		LinkedHashMap<String, Routable> retVal = new LinkedHashMap<String, Routable>();

		if(null != defaultRoute) {
			if(defaultRoute instanceof RestRoutable) {
				retVal.put(" rest: " + route, defaultRoute);
			} else {
				retVal.put("route: " + route, defaultRoute);
			}
		}

		if(null != wildcardRoute) {
			if(wildcardRoute instanceof RestRoutable) {
				retVal.put(" rest: " + route + "*", wildcardRoute);
			} else {
				retVal.put("route: " + route + "*", wildcardRoute);
			}
		}

		// go through and print all of the other routes
		Iterator<String> keySet = routerMap.keySet().iterator();
		while (keySet.hasNext()) {
			String next = keySet.next();
			retVal.putAll(routerMap.get(next).getRouters());
		}
		return(retVal);
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
			String next = keySet.next();
			routerMap.get(next).printRoutes();
		}
	}

	public Map<String, Router> getRouterMap() { return routerMap; }
	public Routable getWildcardRoute() { return wildcardRoute; }
	public Routable getDefaultRoute() { return defaultRoute; }
	public String getRoute() { return route; }
}
