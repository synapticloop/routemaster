package synapticloop.nanohttpd.router;

/*
 * Copyright (c) 2013-2020 synapticloop.
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

import static synapticloop.nanohttpd.utils.SimpleLogger.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

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

/**
 * This class acts as the registration point for all routes, handlers, modules 
 * and configuration to run the sever.
 * 
 * @author synapticloop
 *
 */
public class RouteMaster {
	private static final String ROUTEMASTER_PROPERTIES = "routemaster.properties";
	private static final String ROUTEMASTER_JSON = "routemaster.json";
	private static final String ROUTEMASTER_EXAMPLE_PROPERTIES = "routemaster.example.properties";
	private static final String ROUTEMASTER_EXAMPLE_JSON = "routemaster.example.json";

	private static final String PROPERTY_PREFIX_REST = "rest.";
	private static final String PROPERTY_PREFIX_ROUTE = "route.";
	private static final String PROPERTY_PREFIX_HANDLER = "handler.";

	private static Router router = null;

	private static Set<String> indexFiles = new HashSet<String>();
	private static Map<Integer, String> errorPageCache = new ConcurrentHashMap<Integer, String>();
	private static Map<String, Routable> routerCache = new ConcurrentHashMap<String, Routable>();
	private static Map<String, Handler> handlerCache = new ConcurrentHashMap<String, Handler>();
	private static Set<String> modules = new HashSet<String>();

	private static boolean initialised = false;
	private static File rootDir;

	private RouteMaster() {}

	/**
	 * Initialise the RouteMaster by attempting to look for the routemaster.properties
	 * in the classpath and on the file system.
	 *   
	 * @param rootDir the root directory from which content should be sourced
	 */
	public static void initialise(File rootDir) {
		RouteMaster.rootDir = rootDir;

		Properties properties = null;
		boolean allOk = true;

		try {
			properties = FileHelper.confirmPropertiesFileDefault(ROUTEMASTER_PROPERTIES, ROUTEMASTER_EXAMPLE_PROPERTIES);
		} catch (IOException ioex) {
			try {
				logNoRoutemasterProperties();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			allOk = false;
		}

		if(null == properties) {
			try {
				logNoRoutemasterProperties();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			allOk = false;
		}

		if(allOk) {
			// at this point we want to load any modules that we find, and at them to the 
			// properties file
			loadModules(properties);

			parseOptionsAndRoutes(properties);
			initialised = true;
		} else {
			StringTokenizer stringTokenizer = new StringTokenizer("/*", "/", false);
			router = new Router("/*", stringTokenizer, UninitialisedServant.class.getCanonicalName());
		}
	}

	/**
	 * Dynamically load any modules that exist within the 'modules' directory
	 * 
	 * @param properties the properties from the default routemaster.properties
	 */
	private static void loadModules(Properties properties) {
		// look in the modules directory
		File modulesDirectory = new File(rootDir.getAbsolutePath() + "/modules/");
		if(modulesDirectory.exists() && modulesDirectory.isDirectory() && modulesDirectory.canRead()) {
			String[] moduleList = modulesDirectory.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return(name.endsWith(".jar"));
				}
			});

			int length = moduleList.length;

			if(length == 0) {
				logInfo("No modules found, continuing...");
			} else {
				logInfo("Scanning '" + length + "' jar files for modules");
				URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
				Method method = null;
				try {
					method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				} catch (Exception ex) {
					logFatal("Could not load any modules, exception message was: " + ex.getMessage());
					return;
				}

				method.setAccessible(true);
				for (String module : moduleList) {
					logInfo("Found potential module in file '" + module + "'.");

					File file = new File(rootDir + "/modules/" + module);

					// try and find the <module>-<version>.jar.properties file which will
					// over-ride the routemaster.properties entry in the jar file

					boolean loadedOverrideProperties = false;

					String moduleName = getModuleName(module);
					File overridePropertiesFile = new File(rootDir + "/modules/" + moduleName + ".properties");
					if(overridePropertiesFile.exists() && overridePropertiesFile.canRead()) {
						logInfo("[ " + module + " ] Found an over-ride  .properties file '" + moduleName + ".properties'.");
						try {
							Properties mergeProperties = new Properties();
							mergeProperties.load(new FileReader(overridePropertiesFile));
							Iterator<Object> iterator = mergeProperties.keySet().iterator();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if(properties.containsKey(key)) {
									logWarn("[ " + module + " ] Routemaster already has a property with key '" + key + "', over-writing...");
								}

								String value = mergeProperties.getProperty(key);
								properties.setProperty(key, value);

								logInfo("[ " + module + " ] Adding property key '" + key + "', value '" + value + "'");
							}
							loadedOverrideProperties = true;
						} catch (IOException ex) {
							logFatal("Could not load modules message was: " + ex.getMessage());
						}
					} 

					try {
							JarFile jarFile = new JarFile(file);
							ZipEntry zipEntry = jarFile.getEntry(moduleName + ".properties");
							if(null != zipEntry) {
								URL url = file.toURI().toURL();
								method.invoke(classLoader, url);

								if(!loadedOverrideProperties) { 
									// assuming that the above works - read the properties from the 
									// jar file
									readProperties(module, properties, jarFile, zipEntry);
								}

								if(!modules.contains(module)) {
									modules.add(module);
								}

							} else {
								logWarn("[ " + module + " ] Could not find '/" + moduleName + ".properties' in file '" + module + "'.");
							}
	
							jarFile.close();
						} catch (Exception ex) {
							logFatal("Could not load modules message was: " + ex.getMessage());
						}
					}
			}
		}
	}

	private static void readProperties(String module, Properties properties, JarFile jarFile, ZipEntry zipEntry) throws IOException {
		InputStream input = jarFile.getInputStream(zipEntry);
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		while ((line = reader.readLine()) != null) {
			String trimmed = line.trim();

			if(trimmed.length() != 0) {
				if(trimmed.startsWith("#")) {
					continue;
				}

				String[] split = line.split("=", 2);
				if(split.length == 2) {
					String key = split[0].trim();
					String value = split[1].trim();
					if(properties.containsKey(key)) {
						logWarn("[ " + module + " ] Routemaster already has a property with key '" + key + "', over-writing...");
					}

					properties.setProperty(key, value);
					logInfo("[ " + module + " ] Adding property key '" + key + "', value '" + value + "'");
				}
			}
		}
		reader.close();
	}

	private static String getModuleName(String module) {
		Pattern r = Pattern.compile("(.*)-\\d+\\.\\d+.*\\.jar");
		Matcher m = r.matcher(module);
		if(m.matches()) {
			return(m.group(1));
		}

		int lastIndexOf = module.lastIndexOf(".");
		return(module.substring(0, lastIndexOf));
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
				if(!temp.endsWith("/")) { stringBuilder.append("/"); }
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
		}

		logTable(new ArrayList<String>(modules), "loaded modules");

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

	private static void logNoRoutemasterProperties() throws IOException {
//		logFatal("Could not load the '" + ROUTEMASTER_JSON + "' file, ignoring...");
//		logFatal("(Consequently this is going to be a pretty boring experience!");
//		logFatal("but we did write out an example file for you - '" + ROUTEMASTER_EXAMPLE_JSON + "')");
//		logFatal("NOTE: the '" + ROUTEMASTER_EXAMPLE_JSON + "' takes precedence)");
//		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + ROUTEMASTER_EXAMPLE_JSON);
//
//		FileHelper.writeFile(new File(ROUTEMASTER_EXAMPLE_JSON), inputStream, true);
//		inputStream.close();

		logFatal("Could not load the '" + ROUTEMASTER_PROPERTIES + "' file, ignoring...");
		logFatal("(Consequently this is going to be a pretty boring experience!");
		logFatal("but we did write out an example file for you - '" + ROUTEMASTER_EXAMPLE_PROPERTIES + "')");

		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + ROUTEMASTER_EXAMPLE_PROPERTIES);
		FileHelper.writeFile(new File(ROUTEMASTER_EXAMPLE_PROPERTIES), inputStream, true);
		inputStream.close();

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
	
	/**
	 * Get the set of modules that have been registered with the routemaster
	 * 
	 * @return the set of modules that have been registered
	 */
	public static Set<String> getModules() { return(modules); }
}
