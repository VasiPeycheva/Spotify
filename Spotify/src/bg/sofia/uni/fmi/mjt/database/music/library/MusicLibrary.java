package bg.sofia.uni.fmi.mjt.database.music.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public String search(String keyword) {
		StringBuilder result = new StringBuilder();
		for (String song : songs.keySet()) {
			if (song.toLowerCase().contains(keyword.toLowerCase())) {
				result.append(song + "\n");
			}
		}
		return result.toString();
	}

	public Collection<String> top(int n) {
		return songs.entrySet().stream().sorted((x, y) -> Integer.compare(x.getValue().hitRate, y.getValue().hitRate))
				.map(x -> x.getKey()).limit(n).collect(Collectors.toList());
	}

	private void loadSongs(String libraryPath) {
		File directory = new File(libraryPath);
		File[] contents = directory.listFiles();
		for (File f : contents) {
			if (f.isDirectory()) {
				loadSongs(f.getPath());
			}
			if (f.isFile()) {
				songs.put(f.getName(), new SongInfo(f.getAbsolutePath()));
			}
		}
		logger.log("library <" + libraryPath + "> loaded successfully", Level.INFO);
	}

	public void play(String songName, Socket socket) {
		OutputStream userOutputStream = null;
		BufferedReader read = null;
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			userOutputStream = socket.getOutputStream();
		} catch (IOException e1) {
			logger.log("Failed accessing client socket", Level.ERROR);
		}
		String songPath = songs.get(songName).path;
		File song = new File(songPath);
		try {
			AudioFormat format = AudioSystem.getAudioInputStream(song).getFormat();
			synchronized (userOutputStream) {
				prepareStream(userOutputStream, format);
				InputStream in = (new FileInputStream(song));
				byte[] data = new byte[1024];
				int bytesRead = 0;
				while (true) {
					bytesRead = in.read(data);
					if (bytesRead == -1)
						break;
					userOutputStream.write(data);
				}
				in.close();
			}
		} catch (UnsupportedAudioFileException | IOException e) {
			logger.log("Error while streaming song <" + songName + "> (sending audio format)", Level.ERROR);
		}
	}

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
