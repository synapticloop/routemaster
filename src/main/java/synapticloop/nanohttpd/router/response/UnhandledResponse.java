package synapticloop.nanohttpd.router.response;

import java.io.InputStream;

import synapticloop.nanohttpd.utils.Response;

public class UnhandledResponse extends Response {

	protected UnhandledResponse(IStatus status, String mimeType, InputStream data, long totalBytes) {
		super(status, mimeType, data, totalBytes);
	}

}
