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

	@Override
	public void run() {
		establishConnection();
		try {
			String input = null;
			while ((input = read.readLine()) != null) {
				String[] tokens = input.split(":");
				if (tokens[0].equals("search")) {
					server.search(tokens[1], write);
				} else if (tokens[0].equals("top")) {
					server.top(Integer.parseInt(tokens[1]), write);
				} else if (tokens[0].equals("play")) {
					server.play(tokens[1], socket);
				} else if (tokens[0].equals("show")) {
					server.show(username, tokens[1], write);
				} else if (tokens[0].equals("add")) {
					server.addSong(username, tokens[1], tokens[2], write);
				} else if (tokens[0].equals("create")) {
					server.create(username, tokens[1], write);
				} else if (tokens[0].equals("exit")) {
					logger.log(username + " successfully exit", Level.INFO);
					socket.close();
					break;
				} else {
					write.println("Command not found");
				}

			}
		} catch (IOException e) {
			logger.log("unable to get client request", Level.ERROR);
		}
	}

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

	private boolean connect(String line) {
		String[] token = line.split(" ");
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
