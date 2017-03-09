package synapticloop.nanohttpd.servant;

import java.io.File;
import java.util.logging.Logger;

import synapticloop.nanohttpd.utils.HttpUtils;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class StaticFileListingServant extends StaticFileServant {
	private static final Logger LOGGER = Logger.getLogger(StaticFileListingServant.class.getSimpleName());

	public StaticFileListingServant(String routeContext) {
		super(routeContext);
	}

	@Override
	public Response serve(File rootDir, IHTTPSession httpSession) {
		String uri = HttpUtils.cleanUri(httpSession.getUri());
		File file = new File(rootDir.getAbsolutePath() + uri);

		if(file.exists() && file.isDirectory()) {
			return(HttpUtils.okResponse("text/html", getFileList(rootDir, uri, file)));
		}

		return(super.serve(rootDir, httpSession));
	}

	private String getFileList(File rootDir, String uri, File file) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(FILE_LISTING_TEMPLATE_START);
		stringBuilder.append(String.format("<h2>Directory Listing for %s</h2>", uri));

		if(!"/".equals(uri)) {
			// we need to determine the parent directory
			String[] split = uri.split("/");
			StringBuilder parentDirectoryBuilder = new StringBuilder();
			for(int i = 0; i < split.length - 1; i++) {
				parentDirectoryBuilder.append(split[i]);
				parentDirectoryBuilder.append("/");
			}

			stringBuilder.append(String.format("<p><a href=\"%s\">.. (parent directory)</a></p>", parentDirectoryBuilder.toString()));
		}


		stringBuilder.append("<ul>");
		File[] listFiles = file.listFiles();

		int numFiles = listFiles.length;

		for (File listFile : listFiles) {
			String absoluteFileLink = uri +
					listFile.getName() +
					(listFile.isDirectory()? "/" : "");

			String fileName = listFile.getName() +
					(listFile.isDirectory()? "/" : "");

			stringBuilder.append(String.format("<li><a href=\"%s\">%s</a></li>", absoluteFileLink, fileName));
		}

		stringBuilder.append("</ul>");

		stringBuilder.append(String.format("<p>%d files</p>", numFiles));

		stringBuilder.append(FILE_LISTING_TEMPLATE_END);
		return(stringBuilder.toString());
	}


	private static final String FILE_LISTING_TEMPLATE_START = "<!doctype html>\n" + 
			"<html lang=\"en\">\n" + 
			"<head>\n" + 
			"<title>RouteMaster</title>\n" + 
			"<style>html,body,div,span,applet,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,a,abbr,acronym,address,big,cite,code,del,dfn,em,img,ins,kbd,q,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,tr,th,td,article,aside,canvas,details,embed,figure,figcaption,footer,header,hgroup,menu,nav,output,ruby,section,summary,time,mark,audio,video{margin:0;padding:0;border:0;font-size:100%;font:inherit;vertical-align:baseline}article,aside,details,figcaption,figure,footer,header,hgroup,menu,nav,section{display:block}body{line-height:1}ol,ul{list-style:none}blockquote,q{quotes:none}blockquote:before,blockquote:after,q:before,q:after{content:'';content:none}table{border-collapse:collapse;border-spacing:0}\n" + 
			"body{margin:0;padding:0;padding-top:10px;text-align:center;font-family:monospace;color:#1d1d1d}a:link,a:visited,a:hover,a:active{color:#0FC48F}#main{width:800px;text-align:left;border:0;padding:0;margin:0 auto}h1{text-align:center;font-size:20pt;padding:7px 0;margin:20px 0;border-bottom:1px solid #adadad}h2{font-size:14pt;padding:5px 0;margin:14px 0;border-bottom:1px solid #adadad}p{margin-bottom:7px}em{font-style:italic}strong{font-weight:700}ol{list-style-type:decimal}ol li{padding-bottom:7px;margin-left:40px}.code{color:#C30F43;border-bottom:1px solid #C30F43}#footer{border-top:1px solid #000;margin:12px 0;padding:12px;text-align:center;}p{padding:14px 0;}</style>\n" + 
			"</head>\n" + 
			"<body>\n" + 
			"<div id=\"main\">\n" + 
			"<h1>RouteMaster <em>(nanoHTTPD edition)</em></h1>\n";

	private static final String FILE_LISTING_TEMPLATE_END ="\n" + 
			"<div id=\"footer\">\n" + 
			"<p>powered by: synapticloop - routemaster - nanohttpd</p>\n" + 
			"</div>\n" + 
			"\n" + 
			"</div>\n" + 
			"</body>\n" + 
			"</html>\n" + 
			"";
}
