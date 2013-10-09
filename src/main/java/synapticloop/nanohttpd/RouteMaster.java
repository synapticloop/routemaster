package synapticloop.nanohttpd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

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
				logRouteMasterError();
			}
		} catch (IOException ioex) {
			logRouteMasterError();
		}

		if(null != router) {
			router.printRoutes();
		}
	}

	private static void logRouteMasterError() {
		System.out.println("Could not load the 'routemaster.properties' file, ignoring...");
	}

	public static Response route(IHTTPSession httpSession) {
		if(null != router) {
			// try and find the route
			String uri = httpSession.getUri();
			// do we have a cached version of this?
			if(ROUTER_CACHE.containsKey(uri)) {
				return(ROUTER_CACHE.get(uri).serve(httpSession));
			} else {
				StringTokenizer stringTokenizer = new StringTokenizer("/");
				Routable iRouter = router.route(httpSession, stringTokenizer);
				ROUTER_CACHE.put(uri, iRouter);
				return(iRouter.serve(httpSession));
			}
		} else {
			// @TODO this should actually be a 404 perhaps...
			return(null);
		}
	}
}
