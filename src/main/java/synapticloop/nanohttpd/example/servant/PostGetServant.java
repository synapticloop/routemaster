package synapticloop.nanohttpd.example.servant;

import java.io.File;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import synapticloop.nanohttpd.router.RestRoutable;
import synapticloop.nanohttpd.utils.HttpUtils;

public class PostGetServant extends RestRoutable {

	public PostGetServant(String routeContext, List<String> params) {
		super(routeContext, params);
	}

	@Override
	public Response doGet(File rootDir, 
			IHTTPSession httpSession, 
			Map<String, String> restParams, 
			String unmappedParams) {

		return(HttpUtils.okResponse("text/html", restParams.get("title")));
	}

	@Override
	public Response doPost(File rootDir, 
			IHTTPSession httpSession, 
			Map<String, String> restParams,
			String unmappedParams) {

		return(HttpUtils.okResponse("text/html", restParams.get("title")));
	}
}
