package synapticloop.nanohttpd.utils;

import fi.iki.elonen.NanoHTTPD.Response;

public class HttpUtils {
	public static String cleanUri(String uri) {
		return(uri.replaceAll("/\\.\\./", "/"));
	}

	public static Response okResponse(String mimeType, String content) {
		return(new Response(Response.Status.OK, mimeType, content));
	}

	public static Response rangeNotSatisfiableResponse(String mimeType, String content) {
		return(new Response(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, content));
	}
}
