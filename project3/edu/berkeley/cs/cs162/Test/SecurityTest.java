package edu.berkeley.cs.cs162.Test;

import edu.berkeley.cs.cs162.Server.Security;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

import static org.junit.Assert.*;

public class SecurityTest {

    @Test
    public void testHash(){
        assertEquals("01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029", Security.computeHash("plainTextPassword"));
    }

    @Test
    public void testSaltAndHash(){
        String hashed = "01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029";
        String salt = "cs162project3istasty";

        assertEquals("b1b216d0522b7f5f841b1f5cbd8e5d86ba808a81f5e37826f28bf12dc53fe5ee", Security.computeHashWithSalt(hashed, salt));
    }
}
