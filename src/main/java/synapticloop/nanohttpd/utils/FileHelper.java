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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHelper {
	private static final Logger LOGGER = Logger.getLogger(FileHelper.class.getSimpleName());

	private FileHelper() {}

	/**
	 * Confirm that the properties file exists - this will do the following (in order)
	 * 
	 * <ol>
	 *   <li>look at the file system to see if the properties file exists in the root directory</li>
	 *   <li>look in the classpath to see if the properties file exists</li>
	 *   <li>finally if the properties file is missing in the above two cases, use the example file
	 *   in the classpath __AND__ write this file to the default file location</li>
	 * </ol>
	 * 
	 * @param propertiesFile the name of the properties file to look for (file system, then classpath)
	 * @param examplePropertiesFile the name of the example properties file if not found above
	 * 
	 * @return the loaded properties file
	 * @throws IOException if we cannot load the properties
	 */
	public static Properties confirmPropertiesFileDefault(String propertiesFile, String examplePropertiesFile) throws IOException {
		// first look it up on the file system
		Properties properties = new Properties();

		InputStream inputStream = null;
		File loadFile = new File("./" + propertiesFile);
		if(loadFile.exists() && loadFile.canRead()) {
			try {
				inputStream = new BufferedInputStream(new FileInputStream(loadFile));
			} catch (FileNotFoundException fnfex) {
				// do nothing - one doesn't exist
			}
		} else {
			SimpleLogger.logWarn("Could not load the '" + propertiesFile + "' file from the current directory.");
		}

		if(null != inputStream) {
			properties.load(inputStream);
			return(properties);
		} else {
			// try to get it from the classpath
			inputStream = FileHelper.class.getResourceAsStream("/" + propertiesFile);
		}

		// maybe it is in the current working directory
		if(null != inputStream) {
			properties.load(inputStream);
			return(properties);
		} else {
			SimpleLogger.logWarn("Could not load the '" + propertiesFile + "' file from the classpath.");
		}

		// if it is still null - lookup the example properties file in the classpath
		inputStream = FileHelper.class.getResourceAsStream("/" + examplePropertiesFile);

		if(null == inputStream) {
			// we are out of options
			SimpleLogger.logFatal("Could not find properties files: '" + propertiesFile + ", or " + examplePropertiesFile + " from the filesystem or classpath.");
			return(null);
		} else {
			// write out the file
			inputStream.mark(Integer.MAX_VALUE);
			properties.load(inputStream);
			inputStream.reset();

			// this will write out the file and then close the stream
			writeFile(new File("./" + propertiesFile), inputStream, true);
			return(properties);
		}
	}

	/**
	 * Write out the contents of the stream to the file.
	 * 
	 * @param outputFile The output life to write to
	 * @param inputStream the input stream to read from
	 * @param closeStream whether to close the inputStream when done
	 */
	public static void writeFile(File outputFile, InputStream inputStream, boolean closeStream) {
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;

			while((line = bufferedReader.readLine()) != null) {
				bufferedWriter.write(line);
				bufferedWriter.write(System.getProperty("line.separator"));
			}
			bufferedWriter.flush();
		} catch (IOException ioex) {
			LOGGER.log(Level.SEVERE, "Could not write to file '" + outputFile.getAbsolutePath() + "'.", ioex);
		} finally {
			if(closeStream) {
				if(null != bufferedWriter) {
					try {
						bufferedWriter.close();
					} catch (IOException ioex) {
						// do nothing
					}
				}
			}

			if(null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException ioex) {
					// do nothing
				}
			}
		}
	}
}
