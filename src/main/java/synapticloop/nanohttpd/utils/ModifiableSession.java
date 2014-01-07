package synapticloop.nanohttpd.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class ModifiableSession implements IHTTPSession {
	private IHTTPSession httpSession = null;
	private String uri = null;

	public ModifiableSession(IHTTPSession originalSession) { this.httpSession = originalSession; }
	public void execute() throws IOException { httpSession.execute(); }
	public Map<String, String> getParms() { return(httpSession.getParms()); }
	public Map<String, String> getHeaders() { return(httpSession.getHeaders()); }

	public String getUri() {
		if(null == uri) {
			return(httpSession.getUri());
		} else {
			return(uri);
		}
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Method getMethod() { return(httpSession.getMethod()); }
	public InputStream getInputStream() { return(httpSession.getInputStream()); }
	public CookieHandler getCookies() { return(httpSession.getCookies()); }
	public void parseBody(Map<String, String> files) throws IOException, ResponseException { httpSession.parseBody(files); }
	public String getQueryParameterString() { return(httpSession.getQueryParameterString()); }
}
