package synapticloop.nanohttpd.router;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.ModifiableSession;
import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class RouteMaster {
	private static final String ROUTEMASTER_PROPERTIES = "routemaster.properties";

	private static Router router = null;

	private static HashSet<String> indexFiles = new HashSet<String>();
	private static ConcurrentHashMap<Integer, String> ERROR_PAGE_CACHE = new ConcurrentHashMap<Integer, String>();
	private static ConcurrentHashMap<String, Routable> ROUTER_CACHE = new ConcurrentHashMap<String, Routable>();
	private static boolean initialised = false;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void initialise() {
		// find the route.properties file
		Properties properties = new Properties();
		try {
			InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + ROUTEMASTER_PROPERTIES);

			// maybe it is in the current working directory
			if(null == inputStream) {
				File routemasterFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + ROUTEMASTER_PROPERTIES);
				if(routemasterFile.exists() && routemasterFile.canRead()) {
					inputStream = new BufferedInputStream(new FileInputStream(routemasterFile));
				}
			}

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
							router = new Router(subKey, stringTokenizer, routerClass);
						} else {
							router.addRoute(subKey, stringTokenizer, routerClass);
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
				SimpleLogger.logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring... (although this is going to be a pretty boring experience!)");
			}
		} catch (IOException ioex) {
			SimpleLogger.logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring... (although this is going to be a pretty boring experience!)", ioex);
		}

		if(null != router) {
			SimpleLogger.logTable(router.getRouters(), "registered routes", "route", "routable class");
			router.getRouters();
		}

		if(indexFiles.size() == 0) {
			// default welcomeFiles
			indexFiles.add("index.html");
			indexFiles.add("index.htm");
		}

		SimpleLogger.logTable(new ArrayList(indexFiles), "index files");

		SimpleLogger.logTable(ERROR_PAGE_CACHE, "error pages", "status", "page");

		SimpleLogger.logInfo(RouteMaster.class.getSimpleName() + " initialised.");
		initialised = true;
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
		} else if(key.startsWith("option.error.")) {
			String subKey = key.substring("option.error.".length());
			try {
				int parseInt = Integer.parseInt(subKey);
				ERROR_PAGE_CACHE.put(parseInt, properties.getProperty(key));
			} catch(NumberFormatException nfex) {
				SimpleLogger.logFatal("Could not parse error key '" + subKey + "'.", nfex);
			}
		} else if(key.equals("option.log")) {
//			logRequests = properties.getProperty("option.log").equalsIgnoreCase("true");
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
		if(!initialised) {
			HttpUtils.notFoundResponse();
		}

		Response routeInternalResponse = routeInternal(rootDir, httpSession);
		if(null != routeInternalResponse) {
			return(routeInternalResponse);
		} 
		return(get500Response(rootDir, httpSession));
	}

	private static Response routeInternal(File rootDir, IHTTPSession httpSession) {
		if(null != router) {
			// try and find the route
			String uri = httpSession.getUri();
			// do we have a cached version of this?
			if(ROUTER_CACHE.containsKey(uri)) {
				Response serve = ROUTER_CACHE.get(uri).serve(rootDir, httpSession);
				if(serve == null) {
					return(get404Response(rootDir, httpSession));
				} else {
					return(serve);
				}
			} else {
				StringTokenizer stringTokenizer = new StringTokenizer(uri, "/", false);
				Routable routable = router.route(httpSession, stringTokenizer);
				if(null != routable) {
					ROUTER_CACHE.put(uri, routable);
					Response serve = routable.serve(rootDir, httpSession);
					if(null != serve) {
						return(serve);
					} else {
						return(get404Response(rootDir, httpSession));
					}
				} else {
					// have a null route-able return 404 perhaps
					return(get404Response(rootDir, httpSession));
				}
			}
		} else {
			return(get404Response(rootDir, httpSession));
		}
	}

	private static Response getErrorResponse(File rootDir, IHTTPSession httpSession, Status status, String message) {
		int requestStatus = status.getRequestStatus();
		String uri = ERROR_PAGE_CACHE.get(requestStatus);
		if(ERROR_PAGE_CACHE.containsKey(requestStatus)) {
			ModifiableSession modifiedSession = new ModifiableSession(httpSession);
			modifiedSession.setUri(uri);
			Response response = route(rootDir, modifiedSession);
			response.setStatus(status);
			if(null != response) {
				return(response);
			}
		}
		return(HttpUtils.notFoundResponse(message + "; additionally, an over-ride " + status.toString() + " error page was not found."));
	}

	private static Response get404Response(File rootDir, IHTTPSession httpSession) {
		return(getErrorResponse(rootDir, httpSession, Response.Status.NOT_FOUND, "not found"));
	}

	private static Response get500Response(File rootDir, IHTTPSession httpSession) {
		return(getErrorResponse(rootDir, httpSession, Response.Status.INTERNAL_ERROR, "internal server error"));
	}

	public static Router getRouter() { return router; }
	public static ConcurrentHashMap<String, Routable> getRouterCache() { return ROUTER_CACHE; }
	public static HashSet<String> getIndexFiles() { return indexFiles; }
}
