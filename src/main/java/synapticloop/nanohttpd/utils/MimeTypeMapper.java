package synapticloop.nanohttpd.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import synapticloop.nanohttpd.router.RouteMaster;

public class MimeTypeMapper {
	protected static final String MIMETYPES_PROPERTIES = "mimetypes.properties";
	protected static final String MIMETYPES_EXAMPLE_PROPERTIES = "mimetypes.example.properties";

	private static Map<String, String> mimeTypes = new HashMap<String, String>();

	private MimeTypeMapper() {}

	static {
		Properties properties = new Properties();
		InputStream inputStream = RouteMaster.class.getResourceAsStream("/" + MIMETYPES_PROPERTIES);

		// maybe it is in the current working directory

		File mimetypesFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + MIMETYPES_PROPERTIES);
		if(null == inputStream) {
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
				loadMimeTypesFromProperties(properties, inputStream);
			} else {
				SimpleLogger.logInfo("Filling up the cache with default values from file '" + MIMETYPES_EXAMPLE_PROPERTIES + "'.");
				inputStream = RouteMaster.class.getResourceAsStream("/" + MIMETYPES_EXAMPLE_PROPERTIES);
				loadMimeTypesFromProperties(properties, inputStream);
				// now also write out the file to the filesytem
				SimpleLogger.logInfo("writing out the default  '" + MIMETYPES_PROPERTIES + "' file.");
				inputStream.close();
				inputStream = RouteMaster.class.getResourceAsStream("/" + MIMETYPES_EXAMPLE_PROPERTIES);
				FileHelper.writeFile(mimetypesFile, inputStream);
				
			}
		} catch (IOException ioex) {
			SimpleLogger.logFatal("Could not load the '" + MIMETYPES_PROPERTIES + "' file, ignoring.", ioex);
		}
	}

	private static void loadMimeTypesFromProperties(Properties properties, InputStream inputStream) throws IOException {
		properties.load(inputStream);
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			mimeTypes.put(key, properties.getProperty(key));
		}
	}

	public static void logMimeTypes() { SimpleLogger.logTable(mimeTypes, "registered mime types", "extension", "mime type"); }
	public static Map<String, String> getMimeTypes() { return mimeTypes; }

}
