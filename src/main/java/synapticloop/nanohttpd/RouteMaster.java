package synapticloop.nanohttpd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class RouteMaster {
	//	private static HashMap<String, Router> ROUTER = new HashMap<String, Router>();
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
		return(null);
	}
}
