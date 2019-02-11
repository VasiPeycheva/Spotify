package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.database.users.UsersDatabase;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ClientRequestHandler implements Runnable {

	private PrintWriter write;
	private BufferedReader read;
	private UsersDatabase users;
	private Server server;
	private Logger logger;
	private Socket socket;
	private String username;

	public ClientRequestHandler(Socket socket, Logger logger, UsersDatabase users, Server server) {
		this.logger = logger;
		this.users = users;
		this.server = server;
		this.socket = socket;
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			logger.log("Unable to get socket input stream", Level.ERROR);
		}
		try {
			write = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			logger.log("Unable to get socket output stream", Level.ERROR);
		}
	}

	/**
	 * 
	 * Establish Connection with the user.
	 * 
	 * Listen for user requests.
	 * 
	 * @see ClientRequestHandler#establishConnection()
	 * 
	 */
	@Override
	public void run() {
		establishConnection();
		try {
			String input = null;
			while ((input = read.readLine()) != null) {
				String command = getFirstWord(input);

				if (command.equalsIgnoreCase("search")) {
					search(input);
				} else if (command.equalsIgnoreCase("top")) {
					top(input);
				} else if (command.equalsIgnoreCase("play")) {
					play(input);
				} else if (command.equalsIgnoreCase("show-playlist")) {
					showPlaylist(input);
				} else if (command.equalsIgnoreCase("add-song")) {
					addSong(input);
				} else if (command.equalsIgnoreCase("create-playlist")) {
					createPlaylist(input);
				} else {
					write.println("Command not found");
				}
			}
		} catch (IOException e) {
			logger.log("unable to get client request", Level.ERROR);
		}
	}

	/*
	 * Follow all functions with input validation
	 */

	private void search(String line) {

		String argument = removeFirstWord(line);
		if (argument.equals(line)) {
			write.println("> Please enter keyword!");
			return;
		}
		server.search(argument, write);
	}

	private void top(String line) {
		String argument = removeFirstWord(line);
		if (argument.equals(line)) {
			write.println("> Please enter positive integer number!");
			return;
		}
		try {
			int n = Integer.parseInt(argument);
			server.top(n, write);
		} catch (IllegalArgumentException e) {
			write.println("Please enter positive integer number!");
		}
	}

	private void play(String line) {
		String argument = removeFirstWord(line);
		if (argument.equals(line)) {
			write.println("> Please enter <song name>!");
			return;
		}
		server.play(argument, socket);
	}

	private void showPlaylist(String line) {
		String argument = removeFirstWord(line);
		if (argument.equals(line)) {
			write.println("> Please enter <playlist name>");
			return;
		}
		server.show(username, argument, write);
	}

	private void addSong(String line) {
		String playlist = getFirstWord(removeFirstWord(line));
		String song = removeFirstWord(removeFirstWord(line));
		if (playlist.equals(line) || song.equals(playlist)) {
			write.println("> Please enter <playlist> and <song>!");
			return;
		}
		server.addSong(username, playlist, song, write);
	}

	private void createPlaylist(String line) {
		String playlist = getFirstWord(removeFirstWord(line));
		if (playlist.equals(line)) {
			write.println("> Please enter <playlist name>");
			return;
		}
		server.create(username, playlist, write);
	}

	/**
	 * Return the line with removed first word, where word is considered to be
	 * separated with (" ")
	 * 
	 * @param line
	 * @return line with removed first word
	 */
	private String removeFirstWord(String line) {
		return line.substring(line.indexOf(" ") + 1);
	}

	/**
	 * Return the first word of the line with separator (" ")
	 * 
	 * @param line
	 * @return first word from the given line
	 */
	private String getFirstWord(String line) {
		return line.substring(0, line.indexOf(" "));
	}

	/**
	 * 
	 * Make sure that the connection between the user and the server is established
	 * 
	 * and the user is logged in the system
	 * 
	 */
	private void establishConnection() {
		try {
			String input = null;
			while ((input = read.readLine()) != null) {
				if (connect(input)) {
					return;
				}
			}
		} catch (IOException e) {
			logger.log("unable to get client request while establishing connection", Level.ERROR);
		}
	}

	/**
	 * 
	 * Establish connection between server and user ONLY if the user is correctly
	 * logged in
	 * 
	 * @param line
	 *            - user response
	 * @return true if the user is logged in the system; false if only register or
	 *         pass wrong command
	 */
	private boolean connect(String line) {
		String[] token = line.split(" ");
		if (!users.VERIGICATION_ARGC.equals(token.length)) {
			logger.log("Not enough/too many arguments for authentification!", Level.ERROR);
			write.println("> Please enter correctly <username> and <password>");
			return false;
		}
		if (token[0].equalsIgnoreCase("login")) {
			try {
				users.login(token[1], token[2]);
				username = token[1];
				write.println("> Successfully logged in");
				logger.log("user <" + token[1] + "> successfully logged in", Level.INFO);
				return true;
			} catch (UserNotRegisteredException | WrongPasswordException error) {
				write.println(error.getMessage());
			}
		} else if (token[0].equalsIgnoreCase("register")) {
			try {
				users.register(token[1], token[2]);
				write.println("> Successfully registered, please login");
				logger.log("user <" + token[1] + "> successfully registered", Level.INFO);
			} catch (UserAlreadyExistException error) {
				write.println(error.getMessage());
			}
		} else {
			write.println("> Please REGISTER or LOGIN first");
		}
		return false;
	}
}