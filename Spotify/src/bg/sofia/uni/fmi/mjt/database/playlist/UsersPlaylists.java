package bg.sofia.uni.fmi.mjt.database.playlist;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.database.music.library.MusicLibrary;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.PlaylistDoesntExistException;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.SongAlreadyExistException;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.SongNotFoundException;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class UsersPlaylists {

	/**
	 * Map(username,Map(playlistName, Playlist))
	 */
	private Map<String, Map<String, Playlist>> database;
	private Logger logger;
	private String folder;
	private MusicLibrary library;

	public UsersPlaylists(String rootFolder, MusicLibrary library, Logger logger) {
		this.logger = logger;
		this.folder = rootFolder;
		this.library = library;
		database = new HashMap<String, Map<String, Playlist>>();
		loadDatabase(rootFolder);
	}

	/**
	 * 
	 * Show all songs in user playlist
	 * 
	 * @param username
	 * @param playlistName
	 * @return all Songs in user playlist
	 */
	public String showAllSongs(String username, String playlistName) {
		return database.get(username).get(playlistName).getAllSongs();
	}

	/**
	 * 
	 * Add song to user playlist
	 * 
	 * @param username
	 * @param playlistName
	 * @param songName
	 * @throws SongAlreadyExistException
	 *             if song is already in the user given playlist
	 * @throws PlaylistDoesntExistException
	 *             if user request to add song in not existing playlist
	 * @throws SongNotFoundException
	 *             if song is not in Server {@link MusicLibrary}
	 */
	public void addSong(String username, String playlistName, String songName)
			throws SongAlreadyExistException, PlaylistDoesntExistException, SongNotFoundException {
		if (!library.search(songName).equals("")) {
			throw new SongNotFoundException(songName);
		}
		if (database.get(username).containsKey(playlistName)) {
			database.get(username).get(playlistName).addSong(songName);
		} else {
			logger.log("Playlist <" + playlistName + "> does not exists!", Level.WARINING);
			throw new PlaylistDoesntExistException(playlistName);
		}
	}

	// TODO:repair create function
	/**
	 * 
	 * Create @username new playlist
	 * 
	 * @param username
	 * @param playlistName
	 * @return true if user playlist is successfully created; false otherwise
	 */
	public boolean create(String username, String playlistName) {
		if (database.containsKey(username)) {
			if (database.get(username).containsKey(playlistName)) {
				return false;
			} else {
				database.get(username).put(playlistName,
						new Playlist(new File(getFilename(username, playlistName)), logger));
				return true;
			}
		} else {
			Map<String, Playlist> playlist = new HashMap<>();
			playlist.put(playlistName, new Playlist(new File(getFilename(username, playlistName)), logger));
			database.put(username, playlist);
			return true;
		}
	}

	/**
	 * 
	 * Load all users playlist
	 * 
	 * @param folderName
	 *            - folder with all users playlists
	 */
	private void loadDatabase(String folderName) {
		File folder = new File(folderName);
		File[] files = folder.listFiles();
		for (File f : files) {
			String[] token = f.getName().split("_");
			String username = token[0];
			String playlistName = token[1];
			Map<String, Playlist> playlist = new HashMap<>();
			playlist.put(playlistName, new Playlist(f, logger));
			database.put(username, playlist);
		}
	}

	/**
	 * 
	 * Construct user playlist filename in the {@link UsersPlaylists#folder}
	 * 
	 * @param owner
	 * @param playlistName
	 * @return filename of user playlist file
	 */
	private String getFilename(String owner, String playlistName) {
		StringBuilder filename = new StringBuilder();
		filename.append(folder);
		filename.append("\\\\");
		filename.append(owner);
		filename.append('_');
		filename.append(playlistName);
		filename.append("_.txt");
		return filename.toString();
	}

}
