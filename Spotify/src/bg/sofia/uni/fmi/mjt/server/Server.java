package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bg.sofia.uni.fmi.mjt.database.music.library.MusicLibrary;
import bg.sofia.uni.fmi.mjt.database.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.database.playlist.UsersPlaylists;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.PlaylistDoesntExistException;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.SongAlreadyExistException;
import bg.sofia.uni.fmi.mjt.database.playlist.exceptions.SongNotFoundException;
import bg.sofia.uni.fmi.mjt.database.users.UsersDatabase;
import bg.sofia.uni.fmi.mjt.logger.FileLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;
import bg.sofia.uni.fmi.mjt.server.exceptions.PortAlreadyTaken;

public class Server {

	private final int PORT = 3333;
	private final int THREAD_NUMBER = 20;
	private final String LOG_FILENAME = "server_log.txt";
	private final String LIBRARY_PATH = "resources";
	private final String PLAYLIST_PATH = "resources\\\\playlists";

	private ServerSocket serverSocket;
	private ExecutorService tasks;
	private Logger logger;
	private UsersPlaylists playlists;
	private UsersDatabase users;
	private MusicLibrary library;

	public Server() throws PortAlreadyTaken {
		logger = new FileLogger(LOG_FILENAME);
		try {
			serverSocket = new ServerSocket(PORT);
			logger.log("server is running on localhost:" + PORT, Level.INFO);
		} catch (IOException e) {
			logger.log("Failed to open server socket", Level.ERROR);
			throw new PortAlreadyTaken(PORT);
		}

		tasks = Executors.newFixedThreadPool(THREAD_NUMBER);
		users = new UsersDatabase(logger);
		library = new MusicLibrary(LIBRARY_PATH, logger);
		playlists = new UsersPlaylists(PLAYLIST_PATH, library, logger);
	}

	/**
	 * @Server start to listen for connections;
	 */
	public void start() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				logger.log("connected : user " + socket.getInetAddress(), Level.INFO);
				ClientRequestHandler request = new ClientRequestHandler(socket, logger, users, this);
				tasks.execute(request);
			} catch (IOException e) {
				logger.log("Failed to accept client connection", Level.ERROR);
			}
		}

	}

	/**
	 * 
	 * Send information throw @write for all matching songs from Server Music
	 * Library with @keyword
	 * 
	 * @param keyword
	 *            - user input keyword
	 * @param write
	 *            - user output stream
	 */
	public void search(String keyword, PrintWriter write) {
		String result = library.search(keyword);
		if (result.equals("")) {
			write.println(("no match"));
		} else {
			write.println(result);
		}
	}

	/**
	 * 
	 * Send information throw @write with name of all @n most streamed songs from
	 * Server Music Library
	 * 
	 * @param n
	 *            - number of wanted results
	 * @param write
	 *            - user output stream
	 */
	public void top(int n, PrintWriter write) {
		ArrayList<String> result = (ArrayList<String>) library.top(n);
		if (result.isEmpty()) {
			write.println("We cannot find the " + n + " best hits");
			return;
		}
		for (String bestHits : library.top(n)) {
			write.println(bestHits);
		}
	}

	/**
	 * 
	 * Use Server Music Library method @play()
	 * 
	 * @see MusicLibrary#play(String, Socket)
	 * 
	 * @param songName
	 *            - the song that the user wants to play
	 * @param socket
	 *            - user socket
	 */
	public void play(String songName, Socket socket) {
		library.play(songName, socket);
	}

	/**
	 * 
	 * Send to user throw @write all songs listed in the given @playlistName
	 * 
	 * @see Playlist#getAllSongs()
	 * 
	 * @param username
	 *            - used for authentification
	 * @param playlistName
	 * @param write
	 *            - user output stream
	 */
	public void show(String username, String playlistName, PrintWriter write) {
		String result = playlists.showAllSongs(username, playlistName);
		if (result.equals("")) {
			write.println("no songs in playlist <" + playlistName + ">");
		} else {
			write.println(result);
		}
	}

	/**
	 * 
	 * Create new playlist in @username account
	 * 
	 * @param username
	 * @param playlistName
	 * @param write
	 */
	public void create(String username, String playlistName, PrintWriter write) {
		if (playlists.create(username, playlistName)) {
			write.println("Successfully created <" + playlistName + ">");
		} else {
			write.println("Playlist <" + playlistName + "> already exists");
		}
	}

	/**
	 * 
	 * Add new song to existing user playlist
	 * 
	 * @param username
	 * @param playlistName
	 * @param songName
	 * @param write
	 */
	public void addSong(String username, String playlistName, String songName, PrintWriter write) {
		try {
			playlists.addSong(username, playlistName, songName);
			write.println("Successfully added <" + songName + "> in playlist <" + playlistName + "> ");
		} catch (SongAlreadyExistException | PlaylistDoesntExistException | SongNotFoundException e) {
			write.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Server s;
		try {
			s = new Server();
		} catch (PortAlreadyTaken e) {
			System.out.println(e.getMessage());
			return;
		}
		s.start();
	}

}
