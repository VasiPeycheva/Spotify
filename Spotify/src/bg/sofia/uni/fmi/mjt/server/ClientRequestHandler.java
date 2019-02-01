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
	private Logger logger;
	private UsersDatabase users;

	public ClientRequestHandler(Socket s, Logger logger, UsersDatabase users) {
		this.logger = logger;
		this.users = users;
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
		connect();
	}

	private void connect() {
		try {
			String line = null;
			while ((line = read.readLine()) != null) {
				String[] token = line.split(" ");

				if (token[0].equalsIgnoreCase("login")) {
					try {
						users.login(token[1], token[2]);
						write.println("> Successfully logged in");
						return;
					} catch (UserNotRegisteredException | WrongPasswordException error) {
						write.println(error.getMessage());
					}
				} else if (token[0].equalsIgnoreCase("register")) {
					try {
						users.register(token[1], token[2]);
						write.println("> Successfully registered, please login");
					} catch (UserAlreadyExistException error) {
						write.println(error.getMessage());
					}
				} else if (token[0].equalsIgnoreCase("exit")) {
					write.println("> Successfully exit");
					return;
				} else {
					write.println("> Please REGISTER or LOGIN first");
				}

			}
		} catch (IOException e) {
			logger.log("unable to get client request", Level.ERROR);
		}
	}

}
