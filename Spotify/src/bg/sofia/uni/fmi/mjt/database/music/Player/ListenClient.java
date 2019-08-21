package bg.sofia.uni.fmi.mjt.database.music.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ListenClient implements Runnable {

	private BufferedReader read;
	private Logger logger;
	private Boolean stop;

	public ListenClient(InputStream in, Boolean stop, Logger logger) {
		this.logger = logger;
		this.stop = stop;
		read = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public void run() {
		try {
			String command = null;
			while ((command = read.readLine()) != null) {
				if (command.equals("stop")) {
					stop = true;
					logger.log("User stop song", Level.INFO);
					break;
				}
			}
			read.close();
		} catch (IOException e) {
			logger.log("Failed listening client request (LISTEN CLIENT)", Level.ERROR);
		}
	}

}
