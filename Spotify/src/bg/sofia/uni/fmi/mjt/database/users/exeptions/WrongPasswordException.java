package bg.sofia.uni.fmi.mjt.database.users.exeptions;

public class WrongPasswordException extends Exception {
	private String message = null;

	public WrongPasswordException() {
		message = "> login failed - wrong password!\n";
	}

	public String getMessage() {
		return message;
	}
}
