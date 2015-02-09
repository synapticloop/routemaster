package synapticloop.nanohttpd.handler;

import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class Handler {
	public abstract boolean canServeUri(String uri, File rootDir);
	public abstract Response serveFile(String uri, Map<String, String> headers, IHTTPSession session, File file, String mimeType);

	public String getName() {
		return(this.getClass().getCanonicalName());
	}
}
