package synapticloop.nanohttpd.example.servant;

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
