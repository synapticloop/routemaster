package synapticloop.nanohttpd.servant.example;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SimpleRestServant extends RestRoutable {

	public SimpleRestServant(String routeContext, ArrayList<String> params) {
		super(routeContext, params);
	}

	private String getRestParams(HashMap<String, String> restParams) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Iterator<String> iterator = restParams.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			stringBuilder.append(key + ":" + restParams.get(key));
		}
		return(stringBuilder.toString());
	}

	private Response doMethod(String method, HashMap<String, String> restParams, String unmappedParams) {
		return(HttpUtils.okResponse(this.getClass().getName() + " [ " + method + " ] request: says OK, with params: " + getRestParams(restParams) + ", and un-mapped params of:" + unmappedParams));
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(doMethod("GET", restParams, unmappedParams));
	}

	public Response doPost(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(doMethod("POST", restParams, unmappedParams));
	}

	public Response doPut(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(doMethod("PUT", restParams, unmappedParams));
	}

	public Response doDelete(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(doMethod("DELETE", restParams, unmappedParams));
	}

	public Response doHead(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		return(doMethod("HEAD", restParams, unmappedParams));
	}
}
