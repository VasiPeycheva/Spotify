package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bg.sofia.uni.fmi.mjt.Logger.FileLogger;
import bg.sofia.uni.fmi.mjt.Logger.Level;
import bg.sofia.uni.fmi.mjt.Logger.Logger;

public class Server {

	private int PORT = 3333;
	private int THREAD_NUMBER = 20;
	private String LOG_FILENAME = "server_log.txt";
	private ServerSocket serverSocket;
	private ExecutorService tasks;
	private Logger logger;

	public Server() {
		logger = new FileLogger(LOG_FILENAME);
		try {
			serverSocket = new ServerSocket(PORT);
			logger.log("Server is running on localhost:" + PORT, Level.INFO);
		} catch (IOException e) {
			logger.log("Failed to open server socket", Level.ERROR);
		}

		tasks = Executors.newFixedThreadPool(THREAD_NUMBER);
	}

	public void start() {
		while (true) {
			try {
				Socket s = serverSocket.accept();
				logger.log("connected : user " + s.getInetAddress(), Level.INFO);
				// execute
			} catch (IOException e) {
				logger.log("Failed to accept client connection", Level.ERROR);
			}
		}

	}

	public static void main(String[] args) {
		Server s = new Server();
		s.start();

	}

}
