package synapticloop.nanohttpd.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import synapticloop.nanohttpd.router.RouteMaster;

public class MimeTypeMapper {
	protected static final String MIMETYPES_PROPERTIES = "mimetypes.properties";

	private static HashMap<String, String> MIME_TYPES = new HashMap<String, String>();
	static {
		Properties properties = new Properties();
		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + MIMETYPES_PROPERTIES);

		// maybe it is in the current working directory

		if(null == inputStream) {
			File mimetypesFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + MIMETYPES_PROPERTIES);
			if(mimetypesFile.exists() && mimetypesFile.canRead()) {
				try {
					inputStream = new BufferedInputStream(new FileInputStream(mimetypesFile));
				} catch (FileNotFoundException fnfex) {
					// do nothing - one doesn't exist
				}
			} else {
				SimpleLogger.logWarn("Could not load the '" + MIMETYPES_PROPERTIES + "' file from the classpath, or from the current directory.");
			}
		}

		try {
			if(null != inputStream) {
				properties.load(inputStream);
				Enumeration<Object> keys = properties.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					MIME_TYPES.put(key, properties.getProperty(key));
				}
			}
		} catch (IOException ioex) {
			SimpleLogger.logFatal("Could not load the '" + MIMETYPES_PROPERTIES + "' file, ignoring.", ioex);
		}

		if(MIME_TYPES.isEmpty()) {
			SimpleLogger.logInfo("Filling up the cache with default values");
			// fill up the default
			MIME_TYPES.put("css", "text/css");
			MIME_TYPES.put("htm", "text/html");
			MIME_TYPES.put("html", "text/html");
			MIME_TYPES.put("xml", "text/xml");
			MIME_TYPES.put("java", "text/x-java-source, text/java");
			MIME_TYPES.put("md", "text/plain");
			MIME_TYPES.put("txt", "text/plain");
			MIME_TYPES.put("asc", "text/plain");
			MIME_TYPES.put("gif", "image/gif");
			MIME_TYPES.put("jpg", "image/jpeg");
			MIME_TYPES.put("jpeg", "image/jpeg");
			MIME_TYPES.put("png", "image/png");
			MIME_TYPES.put("mp3", "audio/mpeg");
			MIME_TYPES.put("m3u", "audio/mpeg-url");
			MIME_TYPES.put("mp4", "video/mp4");
			MIME_TYPES.put("ogv", "video/ogg");
			MIME_TYPES.put("flv", "video/x-flv");
			MIME_TYPES.put("mov", "video/quicktime");
			MIME_TYPES.put("swf", "application/x-shockwave-flash");
			MIME_TYPES.put("js", "application/javascript");
			MIME_TYPES.put("pdf", "application/pdf");
			MIME_TYPES.put("doc", "application/msword");
			MIME_TYPES.put("ogg", "application/x-ogg");
			MIME_TYPES.put("zip", "application/octet-stream");
			MIME_TYPES.put("exe", "application/octet-stream");
			MIME_TYPES.put("class", "application/octet-stream");
		}
	}

	public static void logMimeTypes() { SimpleLogger.logTable(MIME_TYPES, "registered mime types", "extension", "mime type"); }
	public static HashMap<String, String> getMimeTypes() { return MIME_TYPES; }

}
