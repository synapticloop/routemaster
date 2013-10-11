package synapticloop.nanohttpd.utils;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class HttpUtils {
	public static String cleanUri(String uri) {
		return(uri.replaceAll("/\\.\\./", "/"));
	}

	public static Response okResponse(String mimeType, String content) {
		return(new Response(Response.Status.OK, mimeType, content));
	}

	public static Response okResponseHtml(String content) {
		return(okResponse(NanoHTTPD.MIME_PLAINTEXT, content));
	}

	public static Response notFoundResponse(String mimeType, String content) {
		return(new Response(Response.Status.NOT_FOUND, mimeType, content));
	}
	public static Response notFoundResponseHtml(String content) {
		return(notFoundResponse(NanoHTTPD.MIME_PLAINTEXT, content));
	}

	public static Response rangeNotSatisfiableResponse(String mimeType, String content) {
		return(new Response(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, content));
	}

	public static Response methodNotAllowedResponse(String mimeType, String content) {
		// TODO this should be a 405-method not allowed
		return(new Response(Response.Status.BAD_REQUEST, mimeType, content));
	}

	public static Response methodNotAllowedHtmlResponse(String content) {
		return(methodNotAllowedResponse(NanoHTTPD.MIME_HTML, content));
	}
}
