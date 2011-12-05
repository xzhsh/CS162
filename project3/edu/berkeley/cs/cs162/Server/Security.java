package edu.berkeley.cs.cs162.Server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    public static String computeHash(String password) {
        try{
            return String.format("%064x", new BigInteger(MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-16"))));
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String computeHashWithSalt(String hash, String salt) {
        return computeHash(hash + salt);
    }
}
