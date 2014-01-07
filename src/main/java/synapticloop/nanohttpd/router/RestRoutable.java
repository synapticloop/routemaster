package synapticloop.nanohttpd.router;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class RestRoutable extends Routable {
	protected ArrayList<String> restParamNames = new ArrayList<String>();

	public RestRoutable(String routeContext, ArrayList<String> params) {
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

		StringBuilder stringBuilder = new StringBuilder();
		while(i < splits.length) {
			stringBuilder.append("/");
			stringBuilder.append(splits[i]);
			i++;
		}

		String unmappedParams = stringBuilder.toString();

		Method method = httpSession.getMethod();
		switch(method) {
		case GET:
			return(doGet(rootDir, httpSession, restParams, unmappedParams));
		case POST:
			return(doPost(rootDir, httpSession, restParams, unmappedParams));
		case PUT:
			return(doPut(rootDir, httpSession, restParams, unmappedParams));
		case DELETE:
			return(doDelete(rootDir, httpSession, restParams, unmappedParams));
		case HEAD:
			return(doHead(rootDir, httpSession, restParams, unmappedParams));
		}
		return(HttpUtils.methodNotAllowedResponse());
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	public Response doPost(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	public Response doPut(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	public Response doDelete(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	public Response doHead(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}
}
