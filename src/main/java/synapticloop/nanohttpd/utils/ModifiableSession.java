package synapticloop.nanohttpd.utils;

/*
 * Copyright (c) 2013-2017 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

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

	@SuppressWarnings("deprecation")
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
	public Map<String, List<String>> getParameters() { return(httpSession.getParameters()); };

	@Override
	public String getRemoteIpAddress() { return(httpSession.getRemoteIpAddress()); }

	@Override
	public String getRemoteHostName() { return(httpSession.getRemoteHostName()); }
}
