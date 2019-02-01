package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import bg.sofia.uni.fmi.mjt.logger.FileLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Client {

	private String LOG_FILENAME = "client_log.txt";
	private int PORT = 3333;
	private String HOST = "localhost";
	private Socket socket;
	private Logger logger;

	// TODO handle power off server
	public Client() {
		logger = new FileLogger(LOG_FILENAME);
		try {
			socket = new Socket(HOST, PORT);
			logger.log("connected to " + HOST + ":" + PORT, Level.INFO);
		} catch (UnknownHostException e) {
			logger.log("Failed to create client socket (UNKNOWN HOST)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Failed to create client socket (POWER OFF SERVER OR WRONG PORT)", Level.ERROR);
		}
	}

	public void start() {
		ServerReader reader = null;
		ServerWriter writer = null;
		try {
			reader = new ServerReader(socket.getInputStream(), logger);
		} catch (IOException e) {
			logger.log("Unable to get socket input stream", Level.ERROR);
		}

		try {
			writer = new ServerWriter(socket.getOutputStream(), logger);
		} catch (IOException e) {
			logger.log("Unable to get socket output stream", Level.ERROR);
		}
		Thread requestHandler = new Thread(writer);
		Thread responseHandler = new Thread(reader);

		requestHandler.start();
		responseHandler.start();
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.start();
	}

}
