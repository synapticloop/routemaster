package synapticloop.nanohttpd;

/*
 * Copyright (c) 2013-2017 synapticloop.
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


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.AsciiArt;
import synapticloop.nanohttpd.utils.SimpleLogger;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

public class RouteMasterServer extends NanoHTTPD {
	private static final Logger LOGGER = Logger.getLogger(RouteMasterServer.class.getName());

	private static final String OPTION_QUIET = "quiet";
	private static final String OPTION_PORT = "port";
	private static final String OPTION_HOST = "host";

	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 5474;

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

	/**
	 * Instantiate a new Route Master Server
	 * 
	 * @param host the host to bind to - by default localhost (127.0.0.1)
	 * @param port The port to bind to - by default 5474
	 * @param rootDir the root directory for serving up files - by default the current working directory (.)
	 * @param quiet whether to suppress messages
	 */
	public RouteMasterServer(String host, int port, File rootDir, boolean quiet) {
		super(host, port);
		SimpleLogger.setShouldLog(!quiet);
		this.rootDir = rootDir;
	}

	public RouteMasterServer(String host, int port, String rootDir, boolean quiet) {
		this(host, port, new File(rootDir), !quiet);
	}

	@Override
	public Response serve(IHTTPSession httpSession) {
		return(RouteMaster.route(rootDir, httpSession));
	}


	public static void main(String[] args) {
		// Defaults
		int port = DEFAULT_PORT;

		String host = DEFAULT_HOST;
		boolean quiet = false;
		File rootDir = null;

		HashMap<String, String> options = new HashMap<String, String>();


		// Parse command-line, with short and long versions of the options.
		for (int i = 0; i < args.length; ++i) {
			if ("-h".equalsIgnoreCase(args[i]) || "-host".equalsIgnoreCase(args[i])) {
				host = args[i + 1];
			} else if ("-p".equalsIgnoreCase(args[i]) || "--port".equalsIgnoreCase(args[i])) {
				port = Integer.parseInt(args[i + 1]);
			} else if ("-q".equalsIgnoreCase(args[i]) || "--quiet".equalsIgnoreCase(args[i])) {
				quiet = true;
			} else if ("-d".equalsIgnoreCase(args[i]) || "--dir".equalsIgnoreCase(args[i])) {
				rootDir = new File(args[i + 1]).getAbsoluteFile();
			} else if ("--licence".equalsIgnoreCase(args[i])) {
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

		options.put(OPTION_HOST, host);
		options.put(OPTION_PORT, Integer.toString(port));
		options.put(OPTION_QUIET, String.valueOf(quiet));
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(rootDir.getCanonicalPath());
		} catch (IOException ioex) {
			LOGGER.log(Level.SEVERE, "Could not get the path for the root directory - results may vary...", ioex);
		}

		options.put("home", sb.toString());
		SimpleLogger.logTable(options, "RouteMaster options", "option", "value");

		RouteMaster.initialise();
		System.out.println("\n\n" + AsciiArt.ROUTEMASTER);
		System.out.print(AsciiArt.ROUTEMASTER_STARTED);
		System.out.print(AsciiArt.LINE);
		System.out.println("                    |          Servicing port " + port + ".       |");
		System.out.println(AsciiArt.LINE);
		ServerRunner.executeInstance(new RouteMasterServer(host, port, rootDir, quiet));
	}
}
