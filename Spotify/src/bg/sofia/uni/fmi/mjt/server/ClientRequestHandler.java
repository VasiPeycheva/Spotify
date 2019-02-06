package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.database.music.library.MusicLibrary;
import bg.sofia.uni.fmi.mjt.database.users.UsersDatabase;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ClientRequestHandler implements Runnable {

	private PrintWriter write;
	private BufferedReader read;
	private Logger logger;
	private UsersDatabase users;
	private MusicLibrary library;

	public ClientRequestHandler(Socket s, Logger logger, UsersDatabase users, MusicLibrary library) {
		this.logger = logger;
		this.users = users;
		this.library = library;
		try {
			read = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			logger.log("Unable to get socket input stream", Level.ERROR);
		}
		try {
			write = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) {
			logger.log("Unable to get socket output stream", Level.ERROR);
		}
	}

	@Override
	public void run() {
		establishConnection();
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
		} else if (token[0].equalsIgnoreCase("exit")) {
			write.println("> Successfully exit");
			// TODO
		} else {
			write.println("> Please REGISTER or LOGIN first");
		}
		return false;
	}
}
