package bg.sofia.uni.fmi.mjt.database.users;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class HashGenerator {
	/**
	 * Hashing with SHA1
	 * 
	 * @author https://gist.github.com/giraam/7413306
	 * @param input
	 *            String to hash
	 * @return String hashed
	 */
	public static String getHash(String input, Logger logger) {
		String sha1 = null;
		MessageDigest msdDigest = null;
		try {
			msdDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			logger.log("No Such Algorithms (SHA1)", Level.ERROR);
		}
		try {
			msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
			sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
		} catch (UnsupportedEncodingException e) {
			logger.log("UnsupportedEncodingException (SHA1)", Level.ERROR);
		}
		return sha1;
	}
}
