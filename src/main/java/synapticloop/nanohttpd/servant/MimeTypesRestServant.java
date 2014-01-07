package synapticloop.nanohttpd.servant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.MimeTypeMapper;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class MimeTypesRestServant extends RestRoutable {

	public MimeTypesRestServant(String routeContext, ArrayList<String> params) {
		super(routeContext, params);
	}

	public Response doGet(File rootDir, IHTTPSession httpSession, HashMap<String, String> restParams, String unmappedParams) {
		StringBuilder content = new StringBuilder();
		HashMap<String, String> mimeTypeCache = MimeTypeMapper.getMimeTypes();
		for (Iterator<String> iterator = mimeTypeCache.keySet().iterator(); iterator.hasNext();) {
			String mimeTypeExtension = (String)iterator.next();
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
