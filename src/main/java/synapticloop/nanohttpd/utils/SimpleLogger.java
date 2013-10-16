package synapticloop.nanohttpd.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleLogger {
	private static final String INFO = "INFO";
	private static final String ERROR = "ERROR";
	private static final String FATAL = "FATAL";
	private static final String WARN = "WARN";
	private static boolean shouldLog = true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void logTable(AbstractMap map, String tableTitle, String keyTitle, String valueTitle) {
		int maxKeyLength = keyTitle.length();
		int maxValueLength = valueTitle.length();

		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		Iterator<Object> iterator = map.keySet().iterator();
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
		stringBuilder.append("INFO: +-");
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
		System.out.print("INFO: +-");
		for (int i = 0; i < maxKeyLength; i++) {
			System.out.print("-");
		}
		System.out.print("---");
		for (int i = 0; i < maxValueLength; i++) {
			System.out.print("-");
		}

		System.out.println("-+");

		System.out.print("INFO: | ");
		System.out.print(String.format("%-" + (maxKeyLength + maxValueLength + 3) + "s", tableTitle)); 
		System.out.println(" |");
		

		System.out.println(breakLine);

		// now the keyTitle and keyValue
		System.out.print("INFO: | ");
		System.out.print(String.format("%-" + maxKeyLength + "s", keyTitle)); 
		System.out.print(" | ");
		System.out.print(String.format("%-" + maxValueLength + "s", valueTitle)); 
		System.out.println(" |");

		System.out.println(breakLine);

		// now go through the arraylists and print out the values
		for(int i = 0; i < keys.size(); i++) {
			System.out.print("INFO: | ");
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
		stringBuilder.append("INFO: +-");
		for (int i = 0; i < maxLength; i++) {
			stringBuilder.append("-");
		}
		stringBuilder.append("-+");
		String breakLine = stringBuilder.toString();

		System.out.println(breakLine);
		System.out.println(String.format("INFO: | %-" + maxLength + "s |", tableTitle)); 
		System.out.println(breakLine);
		for (Object object : list) {
			System.out.println(String.format("INFO: | %-" + maxLength + "s |", object.toString())); 
		}
		System.out.println(breakLine);

	}
	private static void log(String type, String... messages) {
		if(shouldLog) {
			System.out.print(type);
			System.out.print(": ");
			for (int i = 0; i < messages.length; i++) {
				if(i != 0) { System.out.print(" "); }
				System.out.print(messages[i]);
			}
			System.out.println();
		}
	}

	public static void logFatal(String message) {
		log(FATAL, message);
	}

	public static void logFatal(String message, Exception exception) {
		log(FATAL, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
		exception.printStackTrace();
	}

	public static void logError(String message) {
		log(ERROR, message);
	}

	public static void logError(String message, Exception exception) {
		log(ERROR, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}

	public static void logWarn(String message) {
		log(WARN, message);
	}

	public static void logWarn(String message, Exception exception) {
		log(WARN, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}

	public static void logInfo(String message) {
		log(INFO, message);
	}

	public static void logInfo(String message, Exception exception) {
		log(INFO, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}

	public static void logNull() {
		System.out.println();
	}

	public static boolean getShouldLog() { return shouldLog; }
	public static void setShouldLog(boolean shouldLog) { SimpleLogger.shouldLog = shouldLog; }
}
