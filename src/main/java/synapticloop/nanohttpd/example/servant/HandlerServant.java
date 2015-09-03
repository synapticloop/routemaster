package synapticloop.nanohttpd.example.servant;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HandlerServant extends Routable {

	public HandlerServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		StringBuilder content = new StringBuilder();
		Map<String, Handler> handlerCache = RouteMaster.getHandlerCache();

		for (Iterator<String> iterator = handlerCache.keySet().iterator(); iterator.hasNext();) {
			String extension = iterator.next();
			Handler plugin = handlerCache.get(extension);

			content.append("<p> extension: <strong>.");
			content.append(extension);
			content.append("</strong> =&gt; ");
			content.append(plugin.getName());
			content.append("</p>");
		}

		return(HttpUtils.okResponse(content.toString()));
	}

}
