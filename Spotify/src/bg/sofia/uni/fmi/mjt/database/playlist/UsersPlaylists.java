package bg.sofia.uni.fmi.mjt.database.playlist;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.logger.Logger;

public class UsersPlaylists {

	/**
	 * Map<username,Map<playlistName, Playlist>>
	 */
	private Map<String, Map<String, Playlist>> database;
	private Logger logger;

	public UsersPlaylists(String rootFolder, Logger logger) {
		// loadPlaylist
		this.logger = logger;
	}

	public String showAllSongs(String username, String playlistName) {
		return database.get(username).get(playlistName).getAllSongs();
	}

	public boolean addSong(String username, String playlistName, String songName) {
		return database.get(username).get(playlistName).addSong(songName);
	}

	public boolean create(String username, String playlistName) {
		if (database.get(username).containsKey(playlistName)) {
			return false;
		} else {
			database.get(username).put(playlistName, new Playlist(username, playlistName, logger));
			return true;
		}
	}

	private void loadDatabase(String folderName) {
		database = new HashMap<String, Map<String, Playlist>>();
		File folder = new File(folderName);
		File[] files = folder.listFiles();
		for (File f : files) {
			String[] token = f.getName().split("_");
			String username = token[0];
			String playlistName = token[1];
			Map<String, Playlist> playlist = new HashMap<>();
			playlist.put(playlistName, new Playlist(username, playlistName, logger));
			database.put(username, playlist);
		}
	}

}
