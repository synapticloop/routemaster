package synapticloop.nanohttpd.utils;

/*
 * Copyright (c) 2013-2020 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

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
