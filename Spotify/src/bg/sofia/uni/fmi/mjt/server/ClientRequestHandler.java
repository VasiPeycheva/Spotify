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
	// @Override
	// public void run() {
	// establishConnection();
	// try {
	// String input = null;
	// while ((input = read.readLine()) != null) {
	// String[] tokens = input.split(" ");
	// if (tokens[0].equals("search")) {
	// server.search(tokens[1], write);
	// } else if (tokens[0].equals("top")) {
	// server.top(Integer.parseInt(tokens[1]), write);
	// } else if (tokens[0].equals("play")) {
	// server.play(tokens[1], socket);
	// } else if (tokens[0].equals("show-playlist")) {
	// server.show(username, tokens[1], write);
	// } else if (tokens[0].equals("add-song")) {
	// server.addSong(username, tokens[1], tokens[2], write);
	// } else if (tokens[0].equals("create-playlist")) {
	// server.create(username, tokens[1], write);
	// } else {
	// write.println("Command not found");
	// }
	// }
	// } catch (IOException e) {
	// logger.log("unable to get client request", Level.ERROR);
	// }
	// }

	@Override
	public void run() {
		establishConnection();
		try {
			String input = null;
			while ((input = read.readLine()) != null) {
				String[] tokens = input.split(" ");
				try {
					if (tokens[0].toLowerCase().equals(("search"))) {
						server.search(tokens[1], write);
					} else if (tokens[0].toLowerCase().equals("top")) {
						try {
							int n = Integer.parseInt(tokens[1]);
							server.top(n, write);
						} catch (IllegalArgumentException e) {
							write.println("Please enter positive number!");
						}
					} else if (tokens[0].toLowerCase().equals("play")) {
						server.play(getSong(input), socket);
					} else if (tokens[0].toLowerCase().equals("show-playlist")) {
						server.show(username, tokens[1], write);
					} else if (tokens[0].toLowerCase().equals("add-song")) {
						server.addSong(username, tokens[1], getSong(getSong(input)), write);
					} else if (tokens[0].toLowerCase().equals("create-playlist")) {
						server.create(username, tokens[1], write);
					} else {
						write.println("Command not found");
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					write.println("Please pass your request correctly!");
					logger.log("Incorrect number of arguments", Level.ERROR);
				}
			}
		} catch (IOException e) {
			logger.log("unable to get client request", Level.ERROR);
		}
	}

	private String getSong(String line) {
		return line.substring(line.indexOf(" ") + 1);
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