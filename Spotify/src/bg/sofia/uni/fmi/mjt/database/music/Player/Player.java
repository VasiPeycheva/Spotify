package bg.sofia.uni.fmi.mjt.database.music.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Player {
	private InputStream in;
	private OutputStream out;
	private String songPath;
	private Logger logger;

	public Player(String songPath, Socket socket, Logger logger) {
		this.songPath = songPath;
		this.logger = logger;
		try {
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			logger.log("Failed creating Player streams", Level.ERROR);
		}

	}

	public void play() {
		Boolean stop = new Boolean(false);
		Thread send = new Thread(new StreamSong(songPath, stop, out, logger));
		Thread read = new Thread(new ListenClient(in, stop, logger));

		send.start();
		read.start();

	}

}
