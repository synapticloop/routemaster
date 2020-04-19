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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleLogger {
	private static final String INFO = "[INFO]:  ";
	private static final String ERROR = "[ERROR]: ";
	private static final String FATAL = "[FATAL]: ";
	private static final String WARN = "[WARN]:  ";
	private static boolean shouldLog = true;

	private SimpleLogger() {}

	public static void logTable(Map<?, ?> map, String tableTitle, String keyTitle, String valueTitle) {
		int maxKeyLength = keyTitle.length();
		int maxValueLength = valueTitle.length();

		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		Iterator<?> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			String stringKey = key.toString();
			int keyLength = stringKey.length();
			if(keyLength > maxKeyLength) {
				maxKeyLength = keyLength;
			}
			keys.add(stringKey);

			String value = map.get(key).toString();
			int valueLength = value.length();
			if(valueLength > maxValueLength) {
				maxValueLength = valueLength;
			}
			values.add(value);
		}

		if(tableTitle.length() > maxKeyLength + maxValueLength + 3) {
			// need to pad out the values to fit the title
			maxValueLength = tableTitle.length() - valueTitle.length() -3;
		}

		// now print it out
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(INFO + "+-");
		for (int i = 0; i < maxKeyLength; i++) {
			stringBuilder.append("-");
		}
		stringBuilder.append("-+-");
		for (int i = 0; i < maxValueLength; i++) {
			stringBuilder.append("-");
		}
		stringBuilder.append("-+");
		String breakLine = stringBuilder.toString();

		// print out the table title
		System.out.print(INFO + "+-");
		for (int i = 0; i < maxKeyLength; i++) {
			System.out.print("-");
		}
		System.out.print("---");
		for (int i = 0; i < maxValueLength; i++) {
			System.out.print("-");
		}

		System.out.println("-+");

		System.out.print(INFO + "| ");
		System.out.print(String.format("%-" + (maxKeyLength + maxValueLength + 3) + "s", tableTitle));
		System.out.println(" |");


		System.out.println(breakLine);

		// now the keyTitle and keyValue
		System.out.print(INFO + "| ");
		System.out.print(String.format("%-" + maxKeyLength + "s", keyTitle));
		System.out.print(" | ");
		System.out.print(String.format("%-" + maxValueLength + "s", valueTitle));
		System.out.println(" |");

		System.out.println(breakLine);

		// now go through the array lists and print out the values
		for(int i = 0; i < keys.size(); i++) {
			System.out.print(INFO + "| ");
			System.out.print(String.format("%-" + maxKeyLength + "s", keys.get(i)));
			System.out.print(" | ");
			System.out.print(String.format("%-" + maxValueLength + "s", values.get(i)));
			System.out.println(" |");
		}


		System.out.println(breakLine);
	}

	@SuppressWarnings("rawtypes")
	public static void logTable(List list, String tableTitle) {
		int maxLength = tableTitle.length();

		for (Object object : list) {
			int length = object.toString().length();
			if(length > maxLength) {
				maxLength = length;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(INFO + "+-");
		for (int i = 0; i < maxLength; i++) {
			stringBuilder.append("-");
		}
		stringBuilder.append("-+");
		String breakLine = stringBuilder.toString();

		System.out.println(breakLine);
		System.out.println(String.format(INFO + "| %-" + maxLength + "s |", tableTitle));
		System.out.println(breakLine);
		for (Object object : list) {
			System.out.println(String.format(INFO + "| %-" + maxLength + "s |", object.toString()));
		}
		System.out.println(breakLine);

	}

	private static void log(String type, String... messages) {
		if(shouldLog) {
			System.out.print(type);
			for (int i = 0; i < messages.length; i++) {
				if(i != 0) { System.out.print(" "); }
				System.out.print(messages[i]);
			}
			System.out.println();
		}
	}

	private static void logException(String type, String message, Throwable throwable, boolean printStackTrace) {
		log(type, message, "Exception[", throwable.getClass().getCanonicalName(), "] message was:", throwable.getMessage());
		if(printStackTrace) {
			throwable.printStackTrace();
		}
	}

	public static void logFatal(String message) { log(FATAL, message); }
	public static void logFatal(String message, Throwable throwable) { logException(FATAL, message, throwable, true);}

	public static void logError(String message) { log(ERROR, message); }
	public static void logError(String message, Throwable throwable) { logException(ERROR, message, throwable, true); }

	public static void logWarn(String message) { log(WARN, message); }
	public static void logWarn(String message, Throwable throwable) { logException(WARN, message, throwable, false); }

	public static void logInfo(String message) { log(INFO, message); }
	public static void logInfo(String message, Throwable throwable) { logException(INFO, message, throwable, false); }

	public static boolean getShouldLog() { return shouldLog; }
	public static void setShouldLog(boolean shouldLog) { SimpleLogger.shouldLog = shouldLog; }
}
