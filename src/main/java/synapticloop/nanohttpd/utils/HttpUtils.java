package synapticloop.nanohttpd.utils;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class HttpUtils {
	public static String cleanUri(String uri) {
		return(uri.replaceAll("/\\.\\./", "/"));
	}

	public static Response okResponse() { return(okResponse("OK")); }
	public static Response okResponse(String content) { return(okResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response okResponse(InputStream content) { return(okResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response okResponse(String mimeType, String content) { return(new Response(Response.Status.OK, mimeType, content)); }
	public static Response okResponse(String mimeType, InputStream content) { return(new Response(Response.Status.OK, mimeType, content)); }

	public static Response notFoundResponse() { return(notFoundResponse("NOT FOUND")); }
	public static Response notFoundResponse(String content) { return(notFoundResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response notFoundResponse(InputStream content) { return(notFoundResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response notFoundResponse(String mimeType, String content) { return(new Response(Response.Status.NOT_FOUND, mimeType, content)); }
	public static Response notFoundResponse(String mimeType, InputStream content) { return(new Response(Response.Status.NOT_FOUND, mimeType, content)); }

	public static Response rangeNotSatisfiableResponse() { return(rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, "RANGE NOT SATISFIABLE")); }
	public static Response rangeNotSatisfiableResponse(String content) { return(rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response rangeNotSatisfiableResponse(InputStream content) { return(rangeNotSatisfiableResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response rangeNotSatisfiableResponse(String mimeType, String content) { return(new Response(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, content)); }
	public static Response rangeNotSatisfiableResponse(String mimeType, InputStream content) { return(new Response(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, content)); }

	public static Response methodNotAllowedResponse() { return(methodNotAllowedResponse(NanoHTTPD.MIME_PLAINTEXT, "METHOD NOT ALLOWED")); }
	public static Response methodNotAllowedResponse(String content) { return(methodNotAllowedResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response methodNotAllowedResponse(InputStream content) { return(methodNotAllowedResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response methodNotAllowedResponse(String mimeType, String content) { return(new Response(Response.Status.METHOD_NOT_ALLOWED, mimeType, content)); }
	public static Response methodNotAllowedResponse(String mimeType, InputStream content) { return(new Response(Response.Status.METHOD_NOT_ALLOWED, mimeType, content)); }

	public static Response internalServerErrorResponse() { return(internalServerErrorResponse(NanoHTTPD.MIME_PLAINTEXT, "INTERNAL SERVER ERROR")); }
	public static Response internalServerErrorResponse(String content) { return(internalServerErrorResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response internalServerErrorResponse(InputStream content) { return(internalServerErrorResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response internalServerErrorResponse(String mimeType, String content) { return(new Response(Response.Status.INTERNAL_ERROR, mimeType, content)); }
	public static Response internalServerErrorResponse(String mimeType, InputStream content) { return(new Response(Response.Status.INTERNAL_ERROR, mimeType, content)); }

	public static Response forbiddenResponse() { return(forbiddenResponse(NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN")); }
	public static Response forbiddenResponse(String content) { return(forbiddenResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response forbiddenResponse(InputStream content) { return(forbiddenResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response forbiddenResponse(String mimeType, String content) { return(new Response(Response.Status.FORBIDDEN, mimeType, content)); }
	public static Response forbiddenResponse(String mimeType, InputStream content) { return(new Response(Response.Status.FORBIDDEN, mimeType, content)); }

	public static Response notModifiedResponse() { return(notModifiedResponse(NanoHTTPD.MIME_PLAINTEXT, "NOT MODIFIED")); }
	public static Response notModifiedResponse(String content) { return(notModifiedResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response notModifiedResponse(InputStream content) { return(notModifiedResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response notModifiedResponse(String mimeType, String content) { return(new Response(Response.Status.NOT_MODIFIED, mimeType, content)); }
	public static Response notModifiedResponse(String mimeType, InputStream content) { return(new Response(Response.Status.NOT_MODIFIED, mimeType, content)); }

	public static Response partialContentResponse() { return(notModifiedResponse(NanoHTTPD.MIME_PLAINTEXT, "NOT MODIFIED")); }
	public static Response partialContentResponse(String content) { return(partialContentResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response partialContentResponse(InputStream content) { return(partialContentResponse(NanoHTTPD.MIME_PLAINTEXT, content)); }
	public static Response partialContentResponse(String mimeType, String content) { return(new Response(Response.Status.PARTIAL_CONTENT, mimeType, content)); }
	public static Response partialContentResponse(String mimeType, InputStream content) { return(new Response(Response.Status.PARTIAL_CONTENT, mimeType, content)); }

	public static Response redirectResponse(String uri) { return(redirectResponse(uri, "<html><body>Redirected: <a href=\"" + uri + "\">" + uri + "</a></body></html>")); }
	public static Response redirectResponse(String uri, String message) {
		Response res = new Response(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, message);
		res.addHeader("Location", uri);
		return(res);
	}
}
