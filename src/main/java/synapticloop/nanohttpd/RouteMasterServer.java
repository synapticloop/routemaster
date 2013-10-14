package synapticloop.nanohttpd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

public class RouteMasterServer extends NanoHTTPD {
	private final File rootDir;

	public RouteMasterServer(String host, int port, File rootDir, boolean quiet) {
		super(host, port);
		SimpleLogger.setShouldLog(!quiet);
		this.rootDir = rootDir;
	}

	public Response serve(IHTTPSession httpSession) {
		return(RouteMaster.route(rootDir, httpSession));
	}

	
	public static void main(String[] args) {
		// Defaults
		int port = 5474;

		String host = "127.0.0.1";
		boolean quiet = false;
		File rootDir = null;
		Map<String, String> options = new HashMap<String, String>();
		

		// Parse command-line, with short and long versions of the options.
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--host")) {
				host = args[i + 1];
			} else if (args[i].equalsIgnoreCase("-p") || args[i].equalsIgnoreCase("--port")) {
				port = Integer.parseInt(args[i + 1]);
			} else if (args[i].equalsIgnoreCase("-q") || args[i].equalsIgnoreCase("--quiet")) {
				quiet = true;
			} else if (args[i].equalsIgnoreCase("-d") || args[i].equalsIgnoreCase("--dir")) {
				rootDir = new File(args[i + 1]).getAbsoluteFile();
			} else if (args[i].startsWith("-X:")) {
				int dot = args[i].indexOf('=');
				if (dot > 0) {
					String name = args[i].substring(0, dot);
					String value = args[i].substring(dot + 1, args[i].length());
					options.put(name, value);
				}
			}
		}

		if (null == rootDir) {
			rootDir = new File(".").getAbsoluteFile();
		}

		options.put("host", host);
		options.put("port", ""+port);
		options.put("quiet", String.valueOf(quiet));
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(rootDir.getCanonicalPath());
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		options.put("home", sb.toString());

		ServerRunner.executeInstance(new RouteMasterServer(host, port, rootDir, quiet));
	}
}
