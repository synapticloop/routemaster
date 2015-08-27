package synapticloop.nanohttpd.router;

import static synapticloop.nanohttpd.utils.SimpleLogger.*;

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

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.servant.UninitialisedServant;
import synapticloop.nanohttpd.utils.AsciiArt;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import synapticloop.nanohttpd.utils.ModifiableSession;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class RouteMaster {
	private static final String ROUTEMASTER_PROPERTIES = "routemaster.properties";

	private static final String PROPERTY_PREFIX_REST = "rest.";
	private static final String PROPERTY_PREFIX_ROUTE = "route.";
	private static final String PROPERTY_PREFIX_HANDLER = "handler.";

	private static Router router = null;

	private static HashSet<String> indexFiles = new HashSet<String>();
	private static ConcurrentHashMap<Integer, String> ERROR_PAGE_CACHE = new ConcurrentHashMap<Integer, String>();
	private static ConcurrentHashMap<String, Routable> ROUTER_CACHE = new ConcurrentHashMap<String, Routable>();
	private static ConcurrentHashMap<String, Handler> HANDLER_CACHE = new ConcurrentHashMap<String, Handler>();

	private static boolean initialised = false;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void initialise() {
		boolean allOk = true;
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

					} else if(key.startsWith(PROPERTY_PREFIX_HANDLER)) {
						// we are going to add in a plugin
						String subKey = key.substring(PROPERTY_PREFIX_HANDLER.length());
						String pluginProperty = properties.getProperty(key);

						try {
							Object pluginClass = Class.forName(pluginProperty).newInstance();
							if(pluginClass instanceof Handler) {
								HANDLER_CACHE.put(subKey, (Handler)pluginClass);
								logInfo("Handler '" + pluginClass + "', registered for '*." + subKey + "'.");
							} else {
								logFatal("Plugin class '" + pluginProperty + "' is not of instance Plugin.");
							}
						} catch (ClassNotFoundException cnfex) {
							logFatal("Could not find the class for '" + pluginProperty + "'.", cnfex);
						} catch (InstantiationException iex) {
							logFatal("Could not find the class for '" + pluginProperty + "'.", iex);
						} catch (IllegalAccessException iaex) {
							logFatal("Could not find the class for '" + pluginProperty + "'.", iaex);
						}
						
					} else {
						logWarn("Unknown property prefix for key '" + key + "'.");
					}
				}
			} else {
				logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring...");
				logFatal("(Consequently this is going to be a pretty boring experience!)");
				allOk = false;
			}
		} catch (IOException ioex) {
			logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring...");
			logFatal("(Consequently this is going to be a pretty boring experience!)", ioex);
			allOk = false;
		}

		if(null != router) {
			logTable(router.getRouters(), "registered routes", "route", "routable class");
			router.getRouters();
		}

		if(indexFiles.size() == 0) {
			// default welcomeFiles
			indexFiles.add("index.html");
			indexFiles.add("index.htm");
		}

		logTable(new ArrayList(indexFiles), "index files");

		logTable(ERROR_PAGE_CACHE, "error pages", "status", "page");

		logTable(HANDLER_CACHE, "Handlers", "extension", "handler class");

		new MimeTypeMapper();

		logInfo(RouteMaster.class.getSimpleName() + " initialised.");
		initialised = true;

		if(!allOk) {
			StringTokenizer stringTokenizer = new StringTokenizer("/*", "/", false);
			router = new Router("/*", stringTokenizer, UninitialisedServant.class.getCanonicalName());
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
		} else if(key.startsWith("option.error.")) {
			String subKey = key.substring("option.error.".length());
			try {
				int parseInt = Integer.parseInt(subKey);
				ERROR_PAGE_CACHE.put(parseInt, properties.getProperty(key));
			} catch(NumberFormatException nfex) {
				logFatal("Could not parse error key '" + subKey + "'.", nfex);
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

		// at this point we want to check for plugins
		String absolutePath = rootDir.getAbsolutePath();
		int lastIndexOf = absolutePath.lastIndexOf(".");
		if(lastIndexOf != -1) {
			// Figure out if we have a handler
			System.out.println(absolutePath.substring(lastIndexOf));
			
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
		return(HttpUtils.notFoundResponse(AsciiArt.ROUTEMASTER + "          " + message + ";\n\n       additionally, an over-ride " + status.toString() + " error page was not defined\n\n           in the configuration file, key 'option.error.404'."));
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
	public static ConcurrentHashMap<String, Routable> getRouterCache() { return (ROUTER_CACHE); }

	/**
	 * Get the index/welcome files that are registered.
	 *
	 * @return The index files
	 */
	public static HashSet<String> getIndexFiles() { return indexFiles; }

	/**
	 * Get the handler cache
	 * 
	 * @return the handler cache
	 */
	public static ConcurrentHashMap<String, Handler> getHandlerCache() { return (HANDLER_CACHE); }
}
