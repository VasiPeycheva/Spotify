package bg.sofia.uni.fmi.mjt.server;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private int PORT = 3333;
	private int THREAD_NUMBER = 20;
	private String LOG_FILENAME = "server_log.txt";
	private ServerSocket serverSocket;
	private ExecutorService tasks;
	private PrintWriter errorOutput;

	/**
	 * @param errorOutput
	 *            autoflushable output for error message
	 *
	 */
	public Server() {
		try {
			errorOutput = new PrintWriter(new FileWriter(LOG_FILENAME, false), true);
		} catch (FileNotFoundException e) {
			System.err.println("> Failed to open " + LOG_FILENAME + " (FILE NOT FOUND)");
		} catch (IOException e) {
			System.err.println("> Failed to open " + LOG_FILENAME + " (INTERNAL ERROR)");
		}

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			errorOutput.println("> Failed to open server socket");
		}

		tasks = Executors.newFixedThreadPool(THREAD_NUMBER);
	}

	public void start() {
		while (true) {
			try {
				Socket s = serverSocket.accept();
				System.out.println("> connected : user " + s.getInetAddress());
				// execute
			} catch (IOException e) {
				errorOutput.println("> Failed to accept client connection");
			}
		}

	}

	public static void main(String[] args) {
		Server s = new Server();
		s.start();

	}

}
