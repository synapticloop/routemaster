package synapticloop.nanohttpd.handler;

import java.io.File;
import java.util.Map;

import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import synapticloop.nanohttpd.utils.TemplarHelper;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class TemplarHandler extends Handler {
	private static final String TEMPLAR_POSTFIX = ".templar";

	public boolean canServeUri(String uri, File rootDir) {
		return(uri.endsWith(TEMPLAR_POSTFIX));
	}

	public Response serveFile(String uri, Map<String, String> headers, IHTTPSession session, File file) {
		// get the mime type if it exists
		String mimeType = getMimeType(file.getAbsolutePath());

		if(null == mimeType) {
			mimeType = "text/plain";
		}

		try {
			Parser parser = new Parser(file);
			return(HttpUtils.okResponse(mimeType, parser.render()));
		} catch (ParseException pex) {
			return(HttpUtils.internalServerErrorResponse(pex.getMessage()));
		} catch (RenderException rex) {
			return(HttpUtils.internalServerErrorResponse(rex.getMessage()));
		}
	}

	private String getMimeType(String uri) {
		String[] split = uri.split("\\.");
		int length = split.length;

		if(length > 2) {
			// the last one is .templar
			// the second last one is the mime type we need to lookup
			return(MimeTypeMapper.getMimeTypes().get(split[length -2]));
		}
		return(null);
	}

	@Override
	public Response serveFile(String uri, Map<String, String> headers, IHTTPSession session) {
		String mimeType = getMimeType(uri);

		if(null == mimeType) {
			mimeType = NanoHTTPD.MIME_PLAINTEXT;
		}

		try {
			Parser parser = TemplarHelper.getParser(uri);
			return(HttpUtils.okResponse(mimeType, parser.render()));
		} catch (ParseException pex) {
			return(HttpUtils.internalServerErrorResponse(pex.getMessage()));
		} catch (RenderException rex) {
			return(HttpUtils.internalServerErrorResponse(rex.getMessage()));
		}
	}

}
