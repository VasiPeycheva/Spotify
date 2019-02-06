package bg.sofia.uni.fmi.mjt.database.music.library;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
}
