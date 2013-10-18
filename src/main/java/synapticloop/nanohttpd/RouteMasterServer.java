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

	private static final String LICENCE =
			"Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n"
			+ "\n"
			+ "Redistribution and use in source and binary forms, with or without\n"
			+ "modification, are permitted provided that the following conditions\n"
			+ "are met:\n"
			+ "\n"
			+ "Redistributions of source code must retain the above copyright notice,\n"
			+ "this list of conditions and the following disclaimer. Redistributions in\n"
			+ "binary form must reproduce the above copyright notice, this list of\n"
			+ "conditions and the following disclaimer in the documentation and/or other\n"
			+ "materials provided with the distribution. The name of the author may not\n"
			+ "be used to endorse or promote products derived from this software without\n"
			+ "specific prior written permission. \n"
			+ " \n"
			+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
			+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
			+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
			+ "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
			+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
			+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

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
			} else if (args[i].equalsIgnoreCase("--licence")) {
				System.out.println(LICENCE + "\n");
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

		RouteMaster.initialise();
		ServerRunner.executeInstance(new RouteMasterServer(host, port, rootDir, quiet));
	}
}
