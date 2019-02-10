package bg.sofia.uni.fmi.mjt.database.users.exeptions;

public class UserNotRegisteredException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4900106959370094371L;
	private String message = null;

	public UserNotRegisteredException(String username) {
		message = "> Username <" + username + "> is not registered!\n";
	}

	public String getMessage() {
		return message;
	}
}
