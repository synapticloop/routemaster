package synapticloop.nanohttpd.example.servant;

import java.io.File;

import synapticloop.nanohttpd.router.Routable;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class PluginServant extends Routable {

	public PluginServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		// TODO Auto-generated method stub
		return null;
	}

}
