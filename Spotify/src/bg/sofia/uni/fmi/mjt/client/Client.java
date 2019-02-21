package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import bg.sofia.uni.fmi.mjt.logger.FileLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Client {

	private final String LOG_FILENAME = "client_log.txt";
	private final int PORT = 3333;
	private final String HOST = "localhost";

	private Socket socket;
	private Logger logger;

	public Client() throws UnableToConnectException {
		logger = new FileLogger(LOG_FILENAME);
		try {
			socket = new Socket(HOST, PORT);
			logger.log("connected to " + HOST + ":" + PORT, Level.INFO);
		} catch (UnknownHostException e) {
			logger.log("Failed to create client socket (UNKNOWN HOST)", Level.ERROR);
			throw new UnableToConnectException(HOST, PORT, e.getMessage());
		} catch (IOException e) {
			logger.log("Failed to create client socket (POWER OFF SERVER OR WRONG PORT)", Level.ERROR);
			throw new UnableToConnectException(HOST, PORT, e.getMessage());
		}
	}

	public void start() {
		ServerReader reader = new ServerReader(socket, logger);
		ServerWriter writer = new ServerWriter(socket, logger);

		Thread requestHandler = new Thread(writer);
		Thread responseHandler = new Thread(reader);

		requestHandler.start();
		responseHandler.start();
	}

	public static void main(String[] args) {
		Client client;
		try {
			client = new Client();
		} catch (UnableToConnectException e) {
			System.out.println(e.getMessage());
			return;
		}
		client.start();
	}

}
