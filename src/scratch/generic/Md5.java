package scratch.generic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	
	/**
	 * Get the MD5 hash of a byte array.
	 * 
	 * @param b content to hash
	 * @return the hash value
	 */
	public static String getMd5(byte[] b) {
		try {
			
			// Static getInstance method is called with hashing MD5 
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			// digest() method is called to calculate message digest 
			//  of an input digest() return array of byte 
			byte[] messageDigest = md.digest(b);
			
			// Convert byte array into signum representation 
			BigInteger no = new BigInteger(1, messageDigest);
			
			// Convert message digest into hex value 
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
