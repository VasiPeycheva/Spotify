package bg.sofia.uni.fmi.mjt.server.exceptions;

public class PortAlreadyTaken extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 25209762410503958L;
	private String message = null;

	public PortAlreadyTaken(Integer PORT) {
		message = "> Port " + PORT + " is already taken!\n";
	}

	public String getMessage() {
		return message;
	}
}
