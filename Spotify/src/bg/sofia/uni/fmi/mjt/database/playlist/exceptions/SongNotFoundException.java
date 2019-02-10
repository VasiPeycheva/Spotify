package bg.sofia.uni.fmi.mjt.database.playlist.exceptions;

public class SongNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -920095876234760762L;
	private String message = null;

	public SongNotFoundException(String song) {
		message = "> Song <" + song + "> is not in the music library \n";
	}

	public String getMessage() {
		return message;
	}
}
