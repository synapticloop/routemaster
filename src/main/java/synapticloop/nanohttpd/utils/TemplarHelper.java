package synapticloop.nanohttpd.utils;

import java.io.File;

import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;

public class TemplarHelper {

	public static Parser getParser(String filePath) throws ParseException {
		File inputFile = new File("src/main/html/templar/router-snippet.templar");
		Parser parser = null;
		if(inputFile.isFile()) {
			parser = new Parser(inputFile);
		} else {
			parser = new Parser(TemplarHelper.class.getResourceAsStream("src/main/html/templar/router-snippet.templar"));
		}
		return(parser);
	}
}
