package synapticloop.nanohttpd.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHelper {
	private static final Logger LOGGER = Logger.getLogger(FileHelper.class.getSimpleName());

	private FileHelper() {}

	public static void writeFile(File outputFile, InputStream inputStream) {
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
			if(null != bufferedWriter) {
				try {
					bufferedWriter.close();
				} catch (IOException ioex) {
					// do nothing
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
