package bg.sofia.uni.fmi.mjt.logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger implements Logger {

	/**
	 * @param output
	 *            autoflushable output for log message
	 *
	 */
	private PrintWriter output;

	public FileLogger(String fileName) {
		try {
			output = new PrintWriter(new FileWriter(fileName, false), true);
		} catch (FileNotFoundException e) {
			System.err.println("> Failed to open " + fileName + " (FILE NOT FOUND)");
		} catch (IOException e) {
			System.err.println("> Failed to open " + fileName + " (INTERNAL ERROR)");
		}
	}

	@Override
	public void log(String msg, Level level) {
		output.print(level.toString() + " > ");
		output.println(msg);
	}

}
