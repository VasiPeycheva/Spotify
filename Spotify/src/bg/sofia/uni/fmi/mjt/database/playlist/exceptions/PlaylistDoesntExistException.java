package bg.sofia.uni.fmi.mjt.database.playlist.exceptions;

public class PlaylistDoesntExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8075429303893396038L;

	private String message = null;

	public PlaylistDoesntExistException(String playlist) {
		message = "> Playlist <" + playlist + "> doesn`t exist!\n";
	}

	public String getMessage() {
		return message;
	}
}
