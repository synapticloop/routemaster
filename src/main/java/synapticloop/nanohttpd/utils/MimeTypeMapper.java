package synapticloop.nanohttpd.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MimeTypeMapper {
	protected static final String MIMETYPES_PROPERTIES = "mimetypes.properties";
	protected static final String MIMETYPES_EXAMPLE_PROPERTIES = "mimetypes.example.properties";

	private static Map<String, String> mimeTypes = new HashMap<String, String>();

	private MimeTypeMapper() {}

	static {
		Properties properties = null;
		try {
			properties = FileHelper.confirmPropertiesFileDefault(MIMETYPES_PROPERTIES, MIMETYPES_EXAMPLE_PROPERTIES);
		} catch (IOException ioex) {
			SimpleLogger.logFatal("Could not load the '" + MIMETYPES_PROPERTIES + "' file.", ioex);
		}

		if(null != properties) {
			loadMimeTypesFromProperties(properties);
		}
	}

	private static void loadMimeTypesFromProperties(Properties properties) {
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			mimeTypes.put(key, properties.getProperty(key));
		}
	}

	public static void logMimeTypes() { SimpleLogger.logTable(mimeTypes, "registered mime types", "extension", "mime type"); }
	public static Map<String, String> getMimeTypes() { return mimeTypes; }

}
