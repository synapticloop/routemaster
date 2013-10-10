package synapticloop.nanohttpd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import synapticloop.nanohttpd.logger.SimpleLogger;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class RouteMaster {
	private static ConcurrentHashMap<String, Routable> ROUTER_CACHE = new ConcurrentHashMap<String, Routable>();
	private static Router router = null;
	static {
		// find the route.properties file
		Properties properties = new Properties();
		try {
			InputStream inputStream = RouteMaster.class.getResourceAsStream("/routemaster.properties");
			if(null != inputStream) {
				properties.load(inputStream);
				Enumeration<Object> keys = properties.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();

					StringTokenizer stringTokenizer = new StringTokenizer(key, "/", false);
					if(null == router) {
						router = new Router(key, stringTokenizer, (String)properties.get(key));
					} else {
						router.addRoute(key, stringTokenizer, (String)properties.get(key));
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
	}

	public static Response route(File rootDir, IHTTPSession httpSession) {
		if(null != router) {
			// try and find the route
			String uri = httpSession.getUri();
			// do we have a cached version of this?
			if(ROUTER_CACHE.containsKey(uri)) {
				return(ROUTER_CACHE.get(uri).serve(rootDir, httpSession));
			} else {
				StringTokenizer stringTokenizer = new StringTokenizer("/");
				Routable routable = router.route(httpSession, stringTokenizer);
				if(null != routable) {
					ROUTER_CACHE.put(uri, routable);
					return(routable.serve(rootDir, httpSession));
				} else {
					// return 404 perhaps
					return(null);
				}
			}
		} else {
			// @TODO this should actually be a 404 perhaps...
			return(null);
		}
	}
}
