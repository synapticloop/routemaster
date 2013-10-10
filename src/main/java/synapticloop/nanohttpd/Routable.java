package synapticloop.nanohttpd;

import java.io.File;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Routable {
	public Response serve(File rootDir, IHTTPSession httpSession);
}
