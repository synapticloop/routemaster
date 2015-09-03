package synapticloop.nanohttpd.example.servant;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import synapticloop.nanohttpd.handler.Handler;
import synapticloop.nanohttpd.router.Routable;
import synapticloop.nanohttpd.router.RouteMaster;
import synapticloop.nanohttpd.utils.HttpUtils;
import synapticloop.nanohttpd.utils.TemplarHelper;
import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import synapticloop.templar.utils.TemplarContext;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HandlerTemplarServant extends Routable {

	public HandlerTemplarServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		StringBuilder content = new StringBuilder();
		Map<String, Handler> handlerCache = RouteMaster.getHandlerCache();

		TemplarContext templarContext = new TemplarContext();

		for (Iterator<String> iterator = handlerCache.keySet().iterator(); iterator.hasNext();) {
			String extension = iterator.next();
			Handler plugin = handlerCache.get(extension);

			templarContext.clear();
			templarContext.add("extension", extension);
			templarContext.add("handler", plugin.getName());

			try {
				Parser parser = TemplarHelper.getParser("/templar/handler-snippet.templar");
				content.append(parser.render(templarContext));
			} catch (ParseException pex) {
				return(HttpUtils.internalServerErrorResponse(pex.getMessage()));
			} catch (RenderException rex) {
				return(HttpUtils.internalServerErrorResponse(rex.getMessage()));
			}
		}

		return(HttpUtils.okResponse(content.toString()));
	}

}
