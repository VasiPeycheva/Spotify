package bg.sofia.uni.fmi.mjt.database.music.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class MusicLibrary {

	/**
	 * @key - song name
	 * @value - song hit rate
	 */
	private Map<String, SongInfo> songs;
	private Logger logger;

	public MusicLibrary(String path, Logger logger) {
		this.logger = logger;
		songs = new HashMap<>();
		loadSongs(path);
	}

	/**
	 * 
	 * Search for all songs in the music library that match the given keyword
	 * 
	 * @param keyword
	 * @return all matching songs with keyword
	 */
	public String search(String keyword) {
		StringBuilder result = new StringBuilder();
		for (String song : songs.keySet()) {
			if (song.toLowerCase().contains(keyword.toLowerCase())) {
				result.append(song + "\n");
			}
		}
		return result.toString();
	}

	/**
	 * 
	 * Check library for @n most streamed songs in the music library
	 * 
	 * @param n
	 *            number of matching results
	 * @return Collection of all @n most streamed songs
	 */
	public Collection<String> top(int n) {
		return songs.entrySet().stream().sorted((x, y) -> Integer.compare(y.getValue().hitRate, x.getValue().hitRate))
				.map(x -> x.getKey()).limit(n).collect(Collectors.toList());
	}

	/**
	 * 
	 * Load all songs from library folder
	 * 
	 * @param libraryPath
	 */
	private void loadSongs(String libraryPath) {
		File directory = new File(libraryPath);
		File[] contents = directory.listFiles();
		for (File f : contents) {
			if (f.isFile()) {
				songs.put(f.getName(), new SongInfo(f.getAbsolutePath()));
			}
		}
		logger.log("library <" + libraryPath + "> loaded successfully", Level.INFO);
	}

	/**
	 * Stream @songName to user socket ONLY if @songName is in the Music Library
	 * 
	 * @param songName
	 * @param socket
	 */
	public void play(String songName, Socket socket) {
		OutputStream out = null;

		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			logger.log("Failed accessing client socket", Level.ERROR);
		}

		String songPath = songs.get(songName).path;
		if (songPath == null) {
			logger.log(songName + " is not in the Music Library", Level.WARINING);
			return;
		}
		synchronized (songs.get(songName)) {
			songs.get(songName).hitRate++;
		}

		File song = new File(songPath);
		try {
			AudioFormat format = AudioSystem.getAudioInputStream(song).getFormat();
			synchronized (out) {
				prepareStream(out, format);
				streamSong(out, song);
			}
		} catch (UnsupportedAudioFileException e) {
			logger.log("Error while streaming song <" + songName + "> (unsupported audio format)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Error while streaming song <" + songName + "> (IO)", Level.ERROR);
		}
	}

	/**
	 * 
	 * Sending bytes of @song to @out
	 * 
	 * @param out
	 *            - user output stream
	 * @param song
	 *            - song file to be streamed
	 */
	private void streamSong(OutputStream out, File song) {
		try (InputStream in = (new FileInputStream(song))) {
			byte[] data = new byte[1024];
			int bytesRead = 0;
			while (true) {
				bytesRead = in.read(data);
				if (bytesRead == -1)
					break;
				out.write(data);
			}
		} catch (IOException e) {
			logger.log("Failed streaming <" + song.getName() + ">", Level.ERROR);
		}
	}

	/**
	 * 
	 * Prepare user for the Audio Format that is going to be send
	 * 
	 * @param out
	 * @param format
	 */
	private void prepareStream(OutputStream out, AudioFormat format) {
		PrintWriter write = new PrintWriter(out, true);
		write.println("prepare stream");
		write.println(format.getEncoding());
		write.println(format.getSampleRate());
		write.println(format.getSampleSizeInBits());
		write.println(format.getChannels());
		write.println(format.getFrameSize());
		write.println(format.getFrameRate());
		write.println(format.isBigEndian());
	}
}
