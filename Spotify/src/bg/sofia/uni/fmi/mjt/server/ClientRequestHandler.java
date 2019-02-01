package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ClientRequestHandler implements Runnable {

	private PrintWriter write;
	private BufferedReader read;
	private Logger logger;

	public ClientRequestHandler(Socket s, Logger logger) {
		this.logger = logger;
		try {
			read = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			logger.log("Unable to get socket input stream", Level.ERROR);
		}
		try {
			write = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) {
			logger.log("Unable to get socket output stream", Level.ERROR);
		}
	}

	@Override
	public void run() {
		String request = null;
		try {
			while ((request = read.readLine()) != null) {
				write.println("server answer: >" + request);
			}
		} catch (IOException e) {
			logger.log("unable to handle client request", Level.ERROR);
		}

	}

}
