package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bg.sofia.uni.fmi.mjt.database.music.library.MusicLibrary;
import bg.sofia.uni.fmi.mjt.database.users.UsersDatabase;
import bg.sofia.uni.fmi.mjt.logger.FileLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Server {

	private int PORT = 3333;
	private int THREAD_NUMBER = 20;
	private String LOG_FILENAME = "server_log.txt";
	private String LIBRARY_PATH = "resources";
	private ServerSocket serverSocket;
	private ExecutorService tasks;
	private Logger logger;
	private UsersDatabase users;
	private MusicLibrary library;

	public Server() {
		logger = new FileLogger(LOG_FILENAME);
		try {
			serverSocket = new ServerSocket(PORT);
			logger.log("server is running on localhost:" + PORT, Level.INFO);
		} catch (IOException e) {
			logger.log("Failed to open server socket", Level.ERROR);
		}

		tasks = Executors.newFixedThreadPool(THREAD_NUMBER);
		users = new UsersDatabase(logger);
		library = new MusicLibrary(LIBRARY_PATH, logger);
	}

	public void start() {
		while (true) {
			try {
				Socket s = serverSocket.accept();
				logger.log("connected : user " + s.getInetAddress(), Level.INFO);
				ClientRequestHandler request = new ClientRequestHandler(s, logger, users, library);
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
