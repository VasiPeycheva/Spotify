package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bg.sofia.uni.fmi.mjt.logger.FileLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Server {

	private int PORT = 3333;
	private int THREAD_NUMBER = 20;
	private String LOG_FILENAME = "server_log.txt";
	private Map<String, Socket> users;
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
		users = new HashMap<>();
	}

	public void start() {
		while (true) {
			try {
				Socket s = serverSocket.accept();
				logger.log("connected : user " + s.getInetAddress(), Level.INFO);
				ClientRequestHandler request = new ClientRequestHandler(s, logger);
				tasks.execute(request);
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
