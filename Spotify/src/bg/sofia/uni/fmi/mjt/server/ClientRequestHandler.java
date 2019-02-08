package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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
	private UsersDatabase users;
	private MusicLibrary library;
	private Logger logger;
	private Socket socket;

	public ClientRequestHandler(Socket socket, Logger logger, UsersDatabase users, MusicLibrary library) {
		this.logger = logger;
		this.users = users;
		this.library = library;
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
					search(tokens[1]);
				} else if (tokens[0].equals("top")) {
					top(Integer.parseInt(tokens[1]));
				} else if (tokens[0].equals("play")) {
					library.play(tokens[1], socket);
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

	private void search(String keyword) {
		String result = library.search(keyword);
		if (result.equals("")) {
			write.println(("no match"));
		} else {
			write.println(result);
		}

	}

	private void top(int n) {
		ArrayList<String> result = (ArrayList<String>) library.top(n);
		if (result.isEmpty()) {
			write.println("We cannot find the " + n + " best hits");
			return;
		}
		for (String bestHits : library.top(n)) {
			write.println(bestHits);
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
