package bg.sofia.uni.fmi.mjt.client;

public class UnableToConnectException extends Exception {

	private String message;

	public UnableToConnectException(String host, Integer port, String errorMsg) {
		message = "Unable to connect to <" + host + "> on port " + port + " : " + errorMsg;
	}

	public String getMessage() {
		return message;
	}

}
