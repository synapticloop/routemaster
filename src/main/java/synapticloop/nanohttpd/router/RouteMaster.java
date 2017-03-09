package synapticloop.nanohttpd.router;

import static synapticloop.nanohttpd.utils.SimpleLogger.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.servant.UninitialisedServant;
import synapticloop.nanohttpd.utils.AsciiArt;
import synapticloop.nanohttpd.utils.FileHelper;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import synapticloop.nanohttpd.utils.ModifiableSession;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class RouteMaster {
	private static final String ROUTEMASTER_PROPERTIES = "routemaster.properties";
	private static final String ROUTEMASTER_EXAMPLE_PROPERTIES = "routemaster.example.properties";

	private static final String PROPERTY_PREFIX_REST = "rest.";
	private static final String PROPERTY_PREFIX_ROUTE = "route.";
	private static final String PROPERTY_PREFIX_HANDLER = "handler.";

	private static Router router = null;

	private static Set<String> indexFiles = new HashSet<String>();
	private static Map<Integer, String> errorPageCache = new ConcurrentHashMap<Integer, String>();
	private static Map<String, Routable> routerCache = new ConcurrentHashMap<String, Routable>();
	private static Map<String, Handler> handlerCache = new ConcurrentHashMap<String, Handler>();

	private static boolean initialised = false;

	private RouteMaster() {}

	public static void initialise() {
		Properties properties = null;
		boolean allOk = true;

		try {
			properties = FileHelper.confirmPropertiesFileDefault(ROUTEMASTER_PROPERTIES, ROUTEMASTER_EXAMPLE_PROPERTIES);
		} catch (IOException ioex) {
			logNoRoutemasterProperties();
			allOk = false;
		}

		if(null == properties) {
			logNoRoutemasterProperties();
			allOk = false;
		}

		if(allOk) {
			parseOptionsAndRoutes(properties);
			initialised = true;
		} else {
			StringTokenizer stringTokenizer = new StringTokenizer("/*", "/", false);
			router = new Router("/*", stringTokenizer, UninitialisedServant.class.getCanonicalName());
		}
	}

	private static void parseOptionsAndRoutes(Properties properties) {
		// now parse the properties
		parseOptions(properties);
		Enumeration<Object> keys = properties.keys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String routerClass = (String)properties.get(key);
			if(key.startsWith(PROPERTY_PREFIX_ROUTE)) {
				// time to bind a route
				String subKey = key.substring(PROPERTY_PREFIX_ROUTE.length());
				StringTokenizer stringTokenizer = new StringTokenizer(subKey, "/", false);
				if(null == router) {
					router = new Router(subKey, stringTokenizer, routerClass);
				} else {
					router.addRoute(subKey, stringTokenizer, routerClass);
				}
			} else if(key.startsWith(PROPERTY_PREFIX_REST)) {
				// time to bind a rest route
				String subKey = key.substring(PROPERTY_PREFIX_REST.length());
				// now we need to get the parameters
				String[] splits = subKey.split("/");
				StringBuilder stringBuilder = new StringBuilder();

				List<String> params = new ArrayList<String>();
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

			} else if(key.startsWith(PROPERTY_PREFIX_HANDLER)) {
				// we are going to add in a plugin
				String subKey = key.substring(PROPERTY_PREFIX_HANDLER.length());
				String pluginProperty = properties.getProperty(key);

				try {
					Object pluginClass = Class.forName(pluginProperty).newInstance();
					if(pluginClass instanceof Handler) {
						handlerCache.put(subKey, (Handler)pluginClass);
						logInfo("Handler '" + pluginClass + "', registered for '*." + subKey + "'.");
					} else {
						logFatal("Plugin class '" + pluginProperty + "' is not of instance Plugin.");
					}
				} catch (ClassNotFoundException cnfex) {
					logFatal("Could not find the class for '" + pluginProperty + "'.", cnfex);
				} catch (InstantiationException iex) {
					logFatal("Could not instantiate the class for '" + pluginProperty + "'.", iex);
				} catch (IllegalAccessException iaex) {
					logFatal("Illegal acces for class '" + pluginProperty + "'.", iaex);
				}

			} else {
				logWarn("Unknown property prefix for key '" + key + "'.");
			}
		}

		if(null != router) {
			logTable(router.getRouters(), "registered routes", "route", "routable class");
			router.getRouters();
		}

		if(indexFiles.isEmpty()) {
			// default welcomeFiles
			indexFiles.add("index.html");
			indexFiles.add("index.htm");
		}

		logTable(new ArrayList<String>(indexFiles), "index files");

		logTable(errorPageCache, "error pages", "status", "page");

		logTable(handlerCache, "Handlers", "extension", "handler class");

		MimeTypeMapper.logMimeTypes();

		logInfo(RouteMaster.class.getSimpleName() + " initialised.");
	}

	private static void logNoRoutemasterProperties() {
		logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring...");
		logFatal("(Consequently this is going to be a pretty boring experience!");
		logFatal("but we did write out an example file for you - '" + ROUTEMASTER_EXAMPLE_PROPERTIES + "')");

		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + ROUTEMASTER_EXAMPLE_PROPERTIES);

		FileHelper.writeFile(new File(ROUTEMASTER_EXAMPLE_PROPERTIES), inputStream, true);

	}

	/**
	 * Parse the options file
	 *
	 * @param properties The properties object
	 * @param key the option key we are looking at
	 */
	private static void parseOption(Properties properties, String key) {
		if("option.indexfiles".equals(key)) {
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
				errorPageCache.put(parseInt, properties.getProperty(key));
			} catch(NumberFormatException nfex) {
				logFatal("Could not parse error key '" + subKey + "'.", nfex);
			}
		} else if("option.log".equals(key)) {
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

	/**
	 * Serve the correctly routed file
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The session
	 *
	 * @return The response
	 */
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
			if(routerCache.containsKey(uri)) {
				Response serve = routerCache.get(uri).serve(rootDir, httpSession);
				if(serve == null) {
					return(get404Response(rootDir, httpSession));
				} else {
					return(serve);
				}
			} else {
				StringTokenizer stringTokenizer = new StringTokenizer(uri, "/", false);
				Routable routable = router.route(httpSession, stringTokenizer);
				if(null != routable) {
					routerCache.put(uri, routable);
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
		String uri = errorPageCache.get(requestStatus);
		if(errorPageCache.containsKey(requestStatus)) {
			ModifiableSession modifiedSession = new ModifiableSession(httpSession);
			modifiedSession.setUri(uri);
			// if not valid - we have already tried this - and we are going to get a
			// stack overflow, so just drop through
			if(modifiedSession.isValidRequest()) {
				Response response = route(rootDir, modifiedSession);
				response.setStatus(status);
				if(null != response) {
					return(response);
				}
			}
		}

		return(HttpUtils.notFoundResponse(AsciiArt.ROUTEMASTER + 
				"          " + 
				message + 
				";\n\n       additionally, an over-ride " + 
				status.toString() + 
				" error page was not defined\n\n           in the configuration file, key 'option.error.404'."));
	}

	public static Response get404Response(File rootDir, IHTTPSession httpSession) { return(getErrorResponse(rootDir, httpSession, Response.Status.NOT_FOUND, "not found")); }
	public static Response get500Response(File rootDir, IHTTPSession httpSession) { return(getErrorResponse(rootDir, httpSession, Response.Status.INTERNAL_ERROR, "internal server error")); }

	/**
	 * Get the root Router
	 *
	 * @return The Router assigned to the root of the site
	 */
	public static Router getRouter() { return(router); }

	/**
	 * Get the cache of all of the Routables which contains a Map of the Routables
	 * per path - which saves on going through the Router and determining the
	 * Routable on every access
	 *
	 * @return The Routable cache
	 */
	public static Map<String, Routable> getRouterCache() { return (routerCache); }

	/**
	 * Get the index/welcome files that are registered.
	 *
	 * @return The index files
	 */
	public static Set<String> getIndexFiles() { return indexFiles; }

	/**
	 * Get the handler cache
	 * 
	 * @return the handler cache
	 */
	public static Map<String, Handler> getHandlerCache() { return (handlerCache); }
}
