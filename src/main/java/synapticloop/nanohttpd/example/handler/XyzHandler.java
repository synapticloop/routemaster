package synapticloop.nanohttpd.example.handler;

import java.io.File;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.utils.HttpUtils;

public class XyzHandler extends Handler {
	private static final String XYZ_POSTFIX = ".xyz";
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	public boolean canServeUri(String uri) {
		return(uri.endsWith(XYZ_POSTFIX));
	}


	@Override
	public Response serveFile(File rootDir, String uri, Map<String, String> headers, IHTTPSession session) {
		Random random = new Random(System.currentTimeMillis());
		int nextInt = random.nextInt(ALPHABET.length() -1);
		String letter = ALPHABET.substring(nextInt, nextInt + 1);
		return(HttpUtils.okResponse("XYZ handler - responded - is brought to you by the letter " + letter));
	}

}
