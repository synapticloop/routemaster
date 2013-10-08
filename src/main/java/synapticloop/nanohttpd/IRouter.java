package synapticloop.nanohttpd;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface IRouter {
	public Response serve(IHTTPSession httpSession);
}
