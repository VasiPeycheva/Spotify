package bg.sofia.uni.fmi.mjt.database.users.exeptions;

public class WrongPasswordException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6244822181860558448L;
	private String message = null;

	public WrongPasswordException() {
		message = "> Login failed - wrong password!\n";
	}

	public String getMessage() {
		return message;
	}
}
