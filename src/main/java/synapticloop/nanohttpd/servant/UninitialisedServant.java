package synapticloop.nanohttpd.servant;

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

	public Response serve(File rootDir, IHTTPSession httpSession) {
		return(HttpUtils.okResponse(AsciiArt.ROUTEMASTER + AsciiArt.ROUTE_NOT_IN_SERVICE + "\n          'routemaster.properties', not found or could not be loaded.\n\n        Please include a 'routemaster.properties' file in your classpath,\n\n              or the directory from where routemaster was started.\n\n          (otherwise, this is going to be a pretty boring experience!)"));
	}
}
