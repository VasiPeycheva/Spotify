package bg.sofia.uni.fmi.mjt.database.users.exeptions;

public class UserNotRegisteredException extends Exception {
	private String message = null;

	public UserNotRegisteredException(String username) {
		message = "> username <" + username + "> is not registered!\n";
	}

	public String getMessage() {
		return message;
	}
}
