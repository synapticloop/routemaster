package synapticloop.nanohttpd.router;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class RouteMaster {
	private static ConcurrentHashMap<String, Routable> ROUTER_CACHE = new ConcurrentHashMap<String, Routable>();
	private static Router router = null;
	private static HashSet<String> indexFiles = new HashSet<String>();

	static {
		// find the route.properties file
		Properties properties = new Properties();
		try {
			InputStream inputStream = RouteMaster.class.getResourceAsStream("/routemaster.properties");
			if(null != inputStream) {
				properties.load(inputStream);

				parseOptions(properties);
				Enumeration<Object> keys = properties.keys();

				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					String routerClass = (String)properties.get(key);
					if(key.startsWith("route.")) {
						// time to bind a route
						String subKey = key.substring("route.".length());
						StringTokenizer stringTokenizer = new StringTokenizer(subKey, "/", false);
						if(null == router) {
							router = new Router(subKey, stringTokenizer, routerClass, indexFiles);
						} else {
							router.addRoute(subKey, stringTokenizer, routerClass, indexFiles);
						}
					} else if(key.startsWith("rest.")) {
						// time to bind a rest route
						String subKey = key.substring("rest.".length());
						// now we need to get the parameters
						String[] splits = subKey.split("/");
						StringBuilder stringBuilder = new StringBuilder();

						ArrayList<String> params = new ArrayList<String>();
						if(subKey.startsWith("/")) { stringBuilder.append("/"); }

						for (int i = 0; i < splits.length; i++) {
							String split = splits[i];
							if(split.length() == 0) {
								continue;
							}
							if(split.startsWith("%") && split.endsWith("%")) {
								// have a parameter
								params.add(split.substring(1, split.length() -1));
							} else {
								stringBuilder.append(split);
								// keep adding a slash for those that are missing - but not 
								// if it the last
								if(i != splits.length -1) { stringBuilder.append("/"); }
							}
						}

						// now clean up the route
						String temp = stringBuilder.toString();
						if(subKey.endsWith("/") && !temp.endsWith("/")) { stringBuilder.append("/"); }
						// need to make sure that the rest router always picks up wildcards
						if(!subKey.endsWith("*")) { stringBuilder.append("*"); }

						subKey = stringBuilder.toString();
						StringTokenizer stringTokenizer = new StringTokenizer(subKey, "/", false);
						if(null == router) {
							router = new Router(subKey, stringTokenizer, routerClass, params);
						} else {
							router.addRestRoute(subKey, stringTokenizer, routerClass, params);
						}

					} else {
						SimpleLogger.logWarn("Unknown property prefix for key '" + key + "'.");
					}
				}
			} else {
				SimpleLogger.logFatal("Could not load the 'routemaster.properties' file, ignoring...");
			}
		} catch (IOException ioex) {
			SimpleLogger.logFatal("Could not load the 'routemaster.properties' file, ignoring...", ioex);
		}

		if(null != router) {
			router.printRoutes();
		}

		if(indexFiles.size() == 0) {
			// default welcomeFiles
			indexFiles.add("index.html");
			indexFiles.add("index.htm");
		}

		for (String welcomeFile : indexFiles) {
			SimpleLogger.logInfo("Index file: => " + welcomeFile);
		}
	}

	/**
	 * Parse the options file
	 * 
	 * @param properties The properties object
	 * @param key the option key we are looking at
	 */
	private static void parseOption(Properties properties, String key) {
		if(key.equals("option.indexfiles")) {
			String property = properties.getProperty(key);
			String[] splits = property.split(",");
			for (int i = 0; i < splits.length; i++) {
				String split = splits[i].trim();
				indexFiles.add(split);
			}
		}
	}

	private static void parseOptions(Properties properties) {
		Enumeration<Object> keys = properties.keys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if(key.startsWith("option.")) {
				parseOption(properties, key);
				properties.remove(key);
			}
		}
	}

	public static Response route(File rootDir, IHTTPSession httpSession) {
		if(null != router) {
			// try and find the route
			String uri = httpSession.getUri();
			// do we have a cached version of this?
			if(ROUTER_CACHE.containsKey(uri)) {
				return(ROUTER_CACHE.get(uri).serve(rootDir, httpSession));
			} else {
				StringTokenizer stringTokenizer = new StringTokenizer(uri, "/", false);
				Routable routable = router.route(httpSession, stringTokenizer);
				if(null != routable) {
					ROUTER_CACHE.put(uri, routable);
					return(routable.serve(rootDir, httpSession));
				} else {
					// return 404 perhaps
					return(HttpUtils.notFoundResponseHtml("Not Found"));
				}
			}
		} else {
			// @TODO this should actually be a 404 perhaps...
			return(HttpUtils.notFoundResponseHtml("Not Found"));
		}
	}

	public static Router getRouter() { return router; }
	public static ConcurrentHashMap<String, Routable> getRouterCache() { return ROUTER_CACHE; }
	public static HashSet<String> getIndexFiles() { return indexFiles; }
}
