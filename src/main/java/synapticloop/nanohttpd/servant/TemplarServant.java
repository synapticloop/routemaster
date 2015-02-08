package synapticloop.nanohttpd.servant;

import java.io.File;

import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class TemplarServant extends Routable {

	public TemplarServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		System.out.println(rootDir.getAbsolutePath());
		return(HttpUtils.okResponse());
	}

}
