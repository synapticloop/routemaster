package synapticloop.nanohttpd.handler;

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
import java.io.InputStream;
import java.util.Map;

import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * The Templar Handler responds to all requests that ends in '.templar' which 
 * will then utilise the templar rendering engine to output the content.
 *
 */
public class TemplarHandler extends Handler {
	private static final String TEMPLAR_POSTFIX = ".templar";

	@Override
	public boolean canServeUri(String uri) {
		return(uri.endsWith(TEMPLAR_POSTFIX));
	}

	@Override
	public Response serveFile(File rootDir, String uri, Map<String, String> headers, IHTTPSession session) {
		// get the mime type if it exists
		String mimeType = getMimeType(uri);

		if(null == mimeType) {
			mimeType = "text/plain";
		}

		try {
			// at this point, we are wither going to look at the classpath, or the root directory
			Parser parser = getParser(rootDir, uri);
			if(null != parser) {
				return(HttpUtils.okResponse(mimeType, parser.render()));
			} else {
				return(HttpUtils.notFoundResponse(String.format("Could not locate the file '%s'", uri)));
			}
		} catch (ParseException pex) {
			return(HttpUtils.internalServerErrorResponse(pex.getMessage()));
		} catch (RenderException rex) {
			return(HttpUtils.internalServerErrorResponse(rex.getMessage()));
		}
	}

	private Parser getParser(File rootDir, String uri) throws ParseException {
		// always look at the fileSystem first
		File rootFile = new File(rootDir.getAbsolutePath() + uri);
		if(null != rootFile && rootFile.exists() && rootFile.canRead()) {
			return(new Parser(rootFile));
		} else {
			// try and grab it from the classpath
			InputStream inputStream = this.getClass().getResourceAsStream(uri);
			if(null != inputStream) {
				return(new Parser(inputStream));
			}
		}
		return(null);
	}
}
