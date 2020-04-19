package synapticloop.nanohttpd.example.servant;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SimpleRestServant extends RestRoutable {

	public SimpleRestServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	private static String getRestParams(Map<String, String> restParams) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Iterator<String> iterator = restParams.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			stringBuilder.append(key + ":" + restParams.get(key));
		}
		return(stringBuilder.toString());
	}

	private Response doMethod(String method, Map<String, String> restParams, String unmappedParams) {
		return(HttpUtils.okResponse(this.getClass().getName() + " [ " + method + " ] request: says OK, with params: " + getRestParams(restParams) + ", and un-mapped params of:" + unmappedParams));
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("GET", restParams, unmappedParams));
	}

	@Override
	public Response doPost(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("POST", restParams, unmappedParams));
	}

	@Override
	public Response doPut(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("PUT", restParams, unmappedParams));
	}

	@Override
	public Response doDelete(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("DELETE", restParams, unmappedParams));
	}

	@Override
	public Response doHead(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("HEAD", restParams, unmappedParams));
	}

	@Override
	public Response doOptions(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		return(doMethod("OPTIONS", restParams, unmappedParams));
	}

}
