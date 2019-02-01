package bg.sofia.uni.fmi.mjt.client;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import bg.sofia.uni.fmi.mjt.logger.Logger;

public class ServerWriter implements Runnable {

	private Scanner read;
	private PrintWriter write;
	private Logger logger;

	public ServerWriter(OutputStream out, Logger logger) {
		read = new Scanner(System.in);
		write = new PrintWriter(out, true);
		this.logger = logger;
	}

	@Override
	public void run() {

		String request;
		while ((request = read.nextLine()) != null) {
			write.println(request);
		}
	}

}
