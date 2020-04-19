package synapticloop.nanohttpd.servant;

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

import java.io.File;

import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.utils.AsciiArt;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class UninitialisedServant extends Routable {

	public UninitialisedServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		return(HttpUtils.okResponse(AsciiArt.ROUTEMASTER + AsciiArt.ROUTE_NOT_IN_SERVICE + "\n          'routemaster.properties', not found or could not be loaded.\n\n        Please include a 'routemaster.properties' file in your classpath,\n\n              or the directory from where routemaster was started.\n\n          (otherwise, this is going to be a pretty boring experience!)"));
	}
}
