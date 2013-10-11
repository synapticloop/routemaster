package synapticloop.nanohttpd.router;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class RestRoutable extends Routable {
	protected ArrayList<String> restParamNames = new ArrayList<>();

	protected RestRoutable(String routeContext, ArrayList<String> params) {
		super(routeContext);
		this.restParamNames = params;
	}

	public Response serve(File rootDir, IHTTPSession httpSession) {
		String uri = httpSession.getUri();

		String routeUriParams = uri.substring(routeContext.length());

		String[] splits = routeUriParams.split("/");
		HashMap<String, String> restParams = new HashMap<String, String>();
		int i = 0;
		for (String restParamName : restParamNames) {
			String value = null;
			try {
				value = splits[i];
			} catch(ArrayIndexOutOfBoundsException aioobex) {
				// do nothing - no parameter
			}
			restParams.put(restParamName, value);
			i++;
		}
		
		Method method = httpSession.getMethod();
		switch(method) {
		case GET:
			return(doGet(rootDir, httpSession, restParams));
		case POST:
			return(doPost(rootDir, httpSession, restParams));
		case PUT:
			return(doPut(rootDir, httpSession, restParams));
		case DELETE:
			return(doDelete(rootDir, httpSession, restParams));
		case HEAD:
			return(doHead(rootDir, httpSession, restParams));
		}
		// TODO - correct return method here
		return(null);
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(HttpUtils.methodNotAllowedHtmlResponse("Method not allowed"));
	}

	public Response doPost(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(HttpUtils.methodNotAllowedHtmlResponse("Method not allowed"));
	}

	public Response doPut(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(HttpUtils.methodNotAllowedHtmlResponse("Method not allowed"));
	}

	public Response doDelete(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(HttpUtils.methodNotAllowedHtmlResponse("Method not allowed"));
	}

	public Response doHead(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(HttpUtils.methodNotAllowedHtmlResponse("Method not allowed"));
	}
}
