package bg.sofia.uni.fmi.mjt.database.users.exeptions;

public class UserAlreadyExistException extends Exception {

	private String message = null;

	public UserAlreadyExistException(String username) {
		message = "> username <" + username + "> already exist!\n";
	}

	public String getMessage() {
		return message;
	}

}
