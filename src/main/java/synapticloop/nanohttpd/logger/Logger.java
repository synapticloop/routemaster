package synapticloop.nanohttpd.logger;

public class Logger {
	private static final String INFO = "INFO";
	private static final String ERROR = "ERROR";
	private static final String FATAL = "FATAL";

	private static void log(String type, String... messages) {
		System.out.print(type);
		System.out.print(": ");
		for (int i = 0; i < messages.length; i++) {
			if(i != 0) { System.out.print(" "); }
			System.out.print(messages[i]);
		}
		System.out.println();
	}

	public static void logFatal(String message) {
		log(FATAL, message);
	}

	public static void logFatal(String message, Exception exception) {
		log(FATAL, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}

	public static void logError(String message) {
		log(ERROR, message);
	}

	public static void logError(String message, Exception exception) {
		log(ERROR, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}

	public static void logInfo(String message) {
		log(INFO, message);
	}

	public static void logInfo(String message, Exception exception) {
		log(INFO, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
	}
}
