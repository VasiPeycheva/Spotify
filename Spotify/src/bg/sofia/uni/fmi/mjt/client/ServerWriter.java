package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

/**
 * 
 * Send Server request
 * 
 * @author Vasi Peycheva
 *
 */
public class ServerWriter implements Runnable {

	private Scanner read;
	private PrintWriter write;
	private Logger logger;
	private Socket socket;

	public ServerWriter(Socket socket, Logger logger) {
		this.socket = socket;
		read = new Scanner(System.in);
		try {
			write = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			this.logger.log("Failed getting socket output stream (Server Writer)", Level.ERROR);
		}
		this.logger = logger;
	}

	@Override
	public void run() {

		String request = null;
		while (socket.isConnected() && ((request = read.nextLine()) != null)) {
			write.println(request);
		}
	}

}
