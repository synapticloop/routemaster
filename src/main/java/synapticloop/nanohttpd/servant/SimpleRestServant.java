package synapticloop.nanohttpd.servant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;

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

	private Response doMethod(String method, HashMap<String, String> restParams) {
		return(HttpUtils.okResponseHtml(this.getClass().getName() + " [ " + method + " ] request: says OK, with params: " + getRestParams(restParams)));
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(doMethod("GET", restParams));
	}

	public Response doPost(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(doMethod("POST", restParams));
	}

	public Response doPut(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(doMethod("PUT", restParams));
	}

	public Response doDelete(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(doMethod("DELETE", restParams));
	}

	public Response doHead(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams) {
		return(doMethod("HEAD", restParams));
	}

}
