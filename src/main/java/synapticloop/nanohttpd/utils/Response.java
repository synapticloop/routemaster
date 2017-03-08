package synapticloop.nanohttpd.utils;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class Response extends NanoHTTPD.Response {

	protected Response(IStatus status, String mimeType, InputStream data, long totalBytes) {
		super(status, mimeType, data, totalBytes);
	}
}
