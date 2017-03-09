package synapticloop.nanohttpd.utils;

/*
 * Copyright (c) 2013-2017 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

import java.io.File;

import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;

public class TemplarHelper {
	private TemplarHelper() {}

	public static Parser getParser(File rootDir, String filePath) throws ParseException {
		File inputFile = new File(rootDir.getAbsolutePath() + filePath);
		Parser parser = null;
		if(inputFile.isFile()) {
			parser = new Parser(inputFile);
		} else {
			parser = new Parser(TemplarHelper.class.getResourceAsStream(filePath));
		}
		return(parser);
	}
}
