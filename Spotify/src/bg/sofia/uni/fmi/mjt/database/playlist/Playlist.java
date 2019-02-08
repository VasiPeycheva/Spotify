package bg.sofia.uni.fmi.mjt.database.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Playlist {
	private String owner;
	private String playlistName;
	private Set<String> playlist;
	private PrintWriter write;
	private Logger logger;

	public Playlist(String owner, String playlistName, Logger logger) {
		this.owner = owner;
		this.playlistName = playlistName;
		this.logger = logger;
		playlist = new HashSet<>();
		String fileName = getFilename();
		try {
			write = new PrintWriter(new FileOutputStream(fileName, true), true);
		} catch (FileNotFoundException e) {
			this.logger.log("Failed to open " + fileName + "(FILE NOT FOUND)", Level.ERROR);
		}
		loadFile(fileName);
	}

	public String getAllSongs() {
		StringBuilder result = new StringBuilder();
		for (String song : playlist) {
			result.append(song);
			result.append(System.lineSeparator());
		}
		return result.toString();
	}

	public boolean addSong(String name) {
		if (playlist.add(name)) {
			saveToFile(name);
			return true;
		} else {
			return false;
		}
	}

	private void saveToFile(String name) {
		write.println(name);
	}

	private void loadFile(String fileName) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(new File(fileName)));
			String name = null;
			while ((name = read.readLine()) != null) {
				addSong(name);
			}
		} catch (FileNotFoundException e) {
			logger.log("Failed to open " + fileName + "(FILE NOT FOUND)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Failed to load song from " + fileName, Level.ERROR);
		}

	}

	private String getFilename() {
		StringBuilder filename = new StringBuilder();
		filename.append(owner);
		filename.append('_');
		filename.append(playlistName);
		filename.append("_.txt");
		return filename.toString();
	}

}
