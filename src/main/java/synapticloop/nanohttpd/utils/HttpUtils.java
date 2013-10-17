package synapticloop.nanohttpd.utils;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class HttpUtils {
	public static String cleanUri(String uri) {
		return(uri.replaceAll("/\\.\\./", "/"));
	}

	public static Response okResponse() { return(okResponse("OK")); }
	public static Response okResponse(String content) { return(okResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response okResponse(String mimeType, String content) { return(new Response(Response.Status.OK, mimeType, content)); }

	public static Response notFoundResponse() { return(notFoundResponse("NOT FOUND")); }
	public static Response notFoundResponse(String content) { return(notFoundResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response notFoundResponse(String mimeType, String content) { return(new Response(Response.Status.NOT_FOUND, mimeType, content)); }

	public static Response rangeNotSatisfiableResponse() { return(rangeNotSatisfiableResponse(NanoHTTPD.MIME_HTML, "RANGE NOT SATISFIABLE")); }
	public static Response rangeNotSatisfiableResponse(String content) { return(rangeNotSatisfiableResponse(NanoHTTPD.MIME_HTML, content)); }
	public static Response rangeNotSatisfiableResponse(String mimeType, String content) { return(new Response(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, content)); }

	public static Response methodNotAllowedResponse() { return(methodNotAllowedResponse(NanoHTTPD.MIME_HTML, "METHOD NOT ALLOWED")); }
	public static Response methodNotAllowedResponse(String content) { return(methodNotAllowedResponse(NanoHTTPD.MIME_HTML, content)); }
	public static Response methodNotAllowedResponse(String mimeType, String content) { return(new Response(Response.Status.METHOD_NOT_ALLOWED, mimeType, content)); }

}
