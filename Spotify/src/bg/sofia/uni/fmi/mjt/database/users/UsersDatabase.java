package bg.sofia.uni.fmi.mjt.database.users;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.database.users.exeptions.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class UsersDatabase {

	private final String USERS_FILENAME = "users_file.txt";
	private PrintWriter write;
	private Map<String, Integer> users;
	private Logger logger;

	public UsersDatabase(Logger logger) {
		this.logger = logger;
		users = new HashMap<>();
		try {
			write = new PrintWriter(new FileOutputStream(USERS_FILENAME, true), true);
		} catch (FileNotFoundException e) {
			logger.log("Failed to open " + USERS_FILENAME + " (FILE NOT FOUND)", Level.ERROR);
		}
		loadUsers();
	}

	public void register(String username, String password) throws UserAlreadyExistException {
		synchronized (users) {
			if (users.containsKey(username)) {
				logger.log("username <" + username + "> already exist!", Level.INFO);
				throw new UserAlreadyExistException(username);
			} else {
				users.put(username, password.hashCode());
				logger.log("user <" + username + "> have been successfully registered!", Level.INFO);
				saveUser(username, password);
				return true;
			}
		}
	}

	public void login(String username, String password) throws UserNotRegisteredException, WrongPasswordException {
		if (users.containsKey(username)) {
			Integer check = users.get(username);
			if (check.equals(password.hashCode())) {
				logger.log("user <" + username + "> logged successfully!", Level.INFO);
				return true;
			} else {
				logger.log("user <" + username + "> login failed - wrong password!", Level.INFO);
				throw new WrongPasswordException();
			}
		}
		logger.log("user <" + username + "> is not registered!", Level.INFO);
		throw new UserNotRegisteredException(username);
	}

	private void saveUser(String username, String password) {
		write.println(username + ":" + "password");
	}

	private void loadUsers() {
		BufferedReader read = null;
		String line = null;
		try {
			read = new BufferedReader(new FileReader(USERS_FILENAME));
		} catch (FileNotFoundException e) {
			logger.log("Failed to load users database (FILE NOT FOUND)", Level.ERROR);
		}
		try {
			while ((line = read.readLine()) != null) {
				String[] tokens = line.split(":");
				users.put(tokens[0], Integer.parseInt(tokens[1]));
			}
		} catch (IOException e) {
			logger.log("Failed to load users database (INTERNAL ERROR)", Level.ERROR);
		}
	}

}
