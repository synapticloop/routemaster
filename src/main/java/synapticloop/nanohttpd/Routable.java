package synapticloop.nanohttpd;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Routable {
	public Response serve(IHTTPSession httpSession);
}
