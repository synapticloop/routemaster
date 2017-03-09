package synapticloop.nanohttpd.example.servant;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class MimeTypesRestServant extends RestRoutable {

	public MimeTypesRestServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, IHTTPSession httpSession, Map<String, String> restParams, String unmappedParams) {
		StringBuilder content = new StringBuilder();
		Map<String, String> mimeTypeCache = MimeTypeMapper.getMimeTypes();
		for (Iterator<String> iterator = mimeTypeCache.keySet().iterator(); iterator.hasNext();) {
			String mimeTypeExtension = iterator.next();
			String mimeType = mimeTypeCache.get(mimeTypeExtension);
			content.append("<p> Mime type: <strong>");
			content.append(mimeTypeExtension);
			content.append("</strong> =&gt; ");
			content.append(mimeType);
			content.append("</p>");
		}

		return(HttpUtils.okResponse(content.toString()));
	}
}
