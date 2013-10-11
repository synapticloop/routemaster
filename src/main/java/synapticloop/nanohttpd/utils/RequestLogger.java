package synapticloop.nanohttpd.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class RequestLogger {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss Z]");
	private static String format = null;
	public RequestLogger() {

	}

	public void logRequest(IHTTPSession httpSession) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
		stringBuilder.append(" \"");
		stringBuilder.append(httpSession.getMethod().toString());
		stringBuilder.append(httpSession.getUri());
		stringBuilder.append("\"");
		System.out.println(stringBuilder.toString());
	}
}
