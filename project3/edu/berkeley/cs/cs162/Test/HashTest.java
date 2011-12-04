package edu.berkeley.cs.cs162.Test;

import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

import static org.junit.Assert.*;

public class HashTest {

    @Test
    public void testHash(){

        String plaintext = "plainTextPassword";
        String hashed = "";

        try{
            byte[] hashedBytes = MessageDigest.getInstance("SHA-256").digest(plaintext.getBytes("UTF-16"));
            hashed = String.format("%064x", new BigInteger(hashedBytes));
            assertEquals("01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029", hashed);

            String hashed2 = String.format("%064x", new BigInteger(MessageDigest.getInstance("SHA-256").digest(plaintext.getBytes("UTF-16"))));
            assertEquals("01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029", hashed2);
        }
        catch (Exception e){
            System.out.println("Exception...");
        }
    }
}
