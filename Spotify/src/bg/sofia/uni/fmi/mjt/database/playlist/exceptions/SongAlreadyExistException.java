package bg.sofia.uni.fmi.mjt.database.playlist.exceptions;

public class SongAlreadyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7001165023926586492L;

	private String message = null;

	public SongAlreadyExistException(String song) {
		message = "> Song <" + song + "> already exist \n";
	}

	public String getMessage() {
		return message;
	}
}
