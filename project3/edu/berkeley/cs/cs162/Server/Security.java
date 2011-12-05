package edu.berkeley.cs.cs162.Server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    public static String toHexString(byte[] bytes) {
        char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v/16];
            hexChars[j*2 + 1] = hexArray[v%16];
        }
        return new String(hexChars);
    }
    
    public static String computeHash(String original) {
    	try {
			return toHexString(MessageDigest.getInstance("SHA-256").digest(original.getBytes("UTF-16")));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }

    public static String computeHashWithSalt(String hash, String salt) {
        return computeHash(hash+salt);
    }
}
