package synapticloop.nanohttpd.utils;

import java.io.File;

import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;

public class TemplarHelper {
	private TemplarHelper() {}

	public static Parser getParser(String filePath) throws ParseException {
		File inputFile = new File(filePath);
		Parser parser = null;
		if(inputFile.isFile()) {
			parser = new Parser(inputFile);
		} else {
			parser = new Parser(TemplarHelper.class.getResourceAsStream(filePath));
		}
		return(parser);
	}
}
