package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ServerReader implements Runnable {

	private BufferedReader input;
	private Logger logger;

	public ServerReader(InputStream in, Logger logger) {
		input = new BufferedReader(new InputStreamReader(in));
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			String response;
			while ((response = input.readLine()) != null) {
				System.out.println(response);
			}
		} catch (IOException e) {
			logger.log("Unable to get server response", Level.ERROR);
		}
	}

}
