package synapticloop.nanohttpd.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class ModifiableSession implements IHTTPSession {
	private IHTTPSession httpSession = null;
	private String uri = null;

	public ModifiableSession(IHTTPSession originalSession) { this.httpSession = originalSession; }

	@Override
	public void execute() throws IOException { httpSession.execute(); }
	@Override
	public Map<String, String> getParms() { return(httpSession.getParms()); }
	@Override
	public Map<String, String> getHeaders() { return(httpSession.getHeaders()); }

	@Override
	public String getUri() {
		if(null == uri) {
			return(httpSession.getUri());
		} else {
			return(uri);
		}
	}

	public void setUri(String uri) { this.uri = uri; }
	@Override
	public Method getMethod() { return(httpSession.getMethod()); }
	@Override
	public InputStream getInputStream() { return(httpSession.getInputStream()); }
	@Override
	public CookieHandler getCookies() { return(httpSession.getCookies()); }
	@Override
	public void parseBody(Map<String, String> files) throws IOException, ResponseException { httpSession.parseBody(files); }
	@Override
	public String getQueryParameterString() { return(httpSession.getQueryParameterString()); }

	public boolean isValidRequest() {
		// if the original session is not a modifiable session then all is good
		// if it is, then we are about to go into an infinite loop.... best to stop
		// now
		return(!(httpSession instanceof ModifiableSession));
	}

	@Override
	public Map<String, List<String>> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteIpAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHostName() {
		// TODO Auto-generated method stub
		return null;
	}
}
