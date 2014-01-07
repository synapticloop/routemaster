package synapticloop.nanohttpd.router;

import java.io.File;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class Routable {
	// the route that this routable is bound to
	protected String routeContext = null;

	public Routable(String routeContext) {
		this.routeContext = routeContext;
	}

	public abstract Response serve(File rootDir, IHTTPSession httpSession);
}
