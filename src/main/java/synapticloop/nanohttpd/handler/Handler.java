package synapticloop.nanohttpd.handler;

import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class Handler {
	public abstract boolean canServeUri(String uri);
	public abstract Response serveFile(File rootDir, String uri, Map<String, String> headers, IHTTPSession session);

	public String getName() {
		return(this.getClass().getCanonicalName());
	}
}
