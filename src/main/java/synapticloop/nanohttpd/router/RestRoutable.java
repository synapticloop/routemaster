package synapticloop.nanohttpd.router;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class RestRoutable extends Routable {
	protected List<String> restParamNames = new ArrayList<String>();

	public RestRoutable(String routeContext, List<String> params) {
		super(routeContext);
		this.restParamNames = params;
	}

	/**
	 * Serve the correct http method (GET, POST, PUT, DELETE, HEAD)
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The session
	 *
	 * @return The response
	 */
	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		String uri = httpSession.getUri();

		String routeUriParams = uri.substring(routeContext.length());

		String[] splits = routeUriParams.split("/");
		HashMap<String, String> restParams = new HashMap<String, String>();
		int i = 0;
		for (String restParamName : restParamNames) {
			String value = null;
			try {
				value = splits[i];
			} catch(ArrayIndexOutOfBoundsException aioobex) {
				// do nothing - no parameter
			}
			restParams.put(restParamName, value);
			i++;
		}

		StringBuilder stringBuilder = new StringBuilder();
		while(i < splits.length) {
			stringBuilder.append("/");
			stringBuilder.append(splits[i]);
			i++;
		}

		String unmappedParams = stringBuilder.toString();

		switch(httpSession.getMethod()) {
		case GET:
			return(doGet(rootDir, httpSession, restParams, unmappedParams));
		case POST:
			return(doPost(rootDir, httpSession, restParams, unmappedParams));
		case PUT:
			return(doPut(rootDir, httpSession, restParams, unmappedParams));
		case DELETE:
			return(doDelete(rootDir, httpSession, restParams, unmappedParams));
		case HEAD:
			return(doHead(rootDir, httpSession, restParams, unmappedParams));
		case TRACE:
			return(doTrace(rootDir, httpSession, restParams, unmappedParams));
		case PATCH:
			return(doPatch(rootDir, httpSession, restParams, unmappedParams));
		case CONNECT:
			return(doConnect(rootDir, httpSession, restParams, unmappedParams));
		case OPTIONS:
			return(doOptions(rootDir, httpSession, restParams, unmappedParams));
		default:
			return(HttpUtils.methodNotAllowedResponse());
		}
	}

	/**
	 * Override this method to respond to http 'GET' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'POST' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doPost(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'PUT' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doPut(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'DELETE' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doDelete(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'HEAD' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doHead(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'OPTIONS' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doOptions(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'TRACE' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doTrace(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'PATCH' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doPatch(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

	/**
	 * Override this method to respond to http 'CONNECT' requests
	 *
	 * @param rootDir The root directory of the RouteMaster server
	 * @param httpSession The current session
	 * @param restParams the mapped RESTful parameters
	 * @param unmappedParams any other URI components that could not be mapped
	 *
	 * @return the response
	 */
	public Response doConnect(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.methodNotAllowedResponse());
	}

}
