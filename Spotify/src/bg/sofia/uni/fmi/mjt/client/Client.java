package bg.sofia.uni.fmi.mjt.client;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private String LOG_FILENAME = "client_log.txt";
	private PrintWriter errorOutput;
	private int PORT = 3333;
	private String HOST = "localhost";
	private Socket socket;

	/**
	 * @param errorOutput
	 *            autoflushable output for error message
	 *
	 */
	public Client() {
		try {
			errorOutput = new PrintWriter(new FileWriter(LOG_FILENAME, false), true);
		} catch (FileNotFoundException e) {
			System.err.println("> Failed to open " + LOG_FILENAME + " (FILE NOT FOUND)");
		} catch (IOException e) {
			System.err.println("> Failed to open " + LOG_FILENAME + " (INTERNAL ERROR)");
		}

		try {
			socket = new Socket(HOST, PORT);
			System.out.println("> connected to " + HOST + ":" + PORT);
		} catch (UnknownHostException e) {
			errorOutput.println("> Failed to create client socket (UNKNOWN HOST)");
		} catch (IOException e) {
			errorOutput.println("> Failed to create client socket (POWER OFF SERVER OR WRONG PORT)");
		}
	}

	public static void main(String[] args) {
		Client c = new Client();
	}

}
