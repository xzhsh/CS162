package edu.berkeley.cs.cs162.Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/28/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Security {
    public static String computeHash(String original) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {

        }

        byte[] result = md.digest(original.getBytes());

        return new String(result);
    }

    public static String computeHashWithSalt(String hash, String salt) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {

        }

        String intermediate = hash.concat(salt);

        byte[] result = md.digest(intermediate.getBytes());

        return new String(result);
    }
}
