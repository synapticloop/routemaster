package synapticloop.nanohttpd.handler;

import java.io.File;
import java.util.Map;

import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class TemplarHandler extends Handler {
	private static final String TEMPLAR_POSTFIX = ".templar";

	public boolean canServeUri(String uri, File rootDir) {
		return(uri.endsWith(TEMPLAR_POSTFIX));
	}

	public Response serveFile(String uri, Map<String, String> headers, IHTTPSession session, File file, String mimeType) {
		// get the mime type if it exists
		String[] split = file.getAbsolutePath().split("\\.");
		String newMimeType = null;
		int length = split.length;

		if(length > 2) {
			// the last one is .templar
			// the second last one is the mime type we need to lookup
			newMimeType = MimeTypeMapper.getMimeTypes().get(split[length -1]);
		}

		if(null == newMimeType) {
			newMimeType = "text/plain";
		}

		try {
			Parser parser = new Parser(file);
			return(HttpUtils.okResponse(newMimeType, parser.render()));
		} catch (ParseException pex) {
			return(HttpUtils.internalServerErrorResponse());
		} catch (RenderException rex) {
			return(HttpUtils.internalServerErrorResponse());
		}
	}

}
