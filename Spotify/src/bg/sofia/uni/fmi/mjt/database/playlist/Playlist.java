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

import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.SongAlreadyExistException;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class Playlist {

	private Set<String> playlist;
	private PrintWriter write;
	private Logger logger;

	public Playlist(File file, Logger logger) {
		this.logger = logger;
		playlist = new HashSet<>();
		try {
			write = new PrintWriter(new FileOutputStream(file, true), true);
		} catch (FileNotFoundException e) {
			this.logger.log("Failed to open " + file + "(FILE NOT FOUND)", Level.ERROR);
		}
		loadFile(file);
	}

	public String getAllSongs() {
		StringBuilder result = new StringBuilder();
		for (String song : playlist) {
			result.append(song);
			result.append(System.lineSeparator());
		}
		return result.toString();
	}

	public void addSong(String name) throws SongAlreadyExistException {
		if (playlist.add(name)) {
			saveToFile(name);
		} else {
			logger.log(name + " already exists ", Level.WARINING);
			throw new SongAlreadyExistException(name);
		}
	}

	private void saveToFile(String name) {
		write.println(name);
	}

	private void loadFile(File file) {
		try (BufferedReader read = new BufferedReader(new FileReader(file))) {
			String name = null;
			while ((name = read.readLine()) != null) {
				playlist.add(name);
			}
		} catch (FileNotFoundException e) {
			logger.log("Failed to open " + file.getName() + "(FILE NOT FOUND)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Failed to load song from " + file.getName(), Level.ERROR);
		}
	}

}
