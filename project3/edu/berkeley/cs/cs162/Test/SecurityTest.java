package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.Security;

public class SecurityTest {

    @Test
    public void testHash(){
        assertEquals("01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029", Security.computeHash("plainTextPassword"));
        assertEquals(Security.computeHash("Pikachu"), "0476b4c947c9d374c0162756cbbe5e4229e062041a47eec50f67d6fca7262098");
    }

    @Test
    public void testSaltAndHash(){
        String hashed = "01faa5e19c568a4a322c8a1ee53d747c64e9e960f68bad74b5235425fe799029";
        String salt = "cs162project3istasty";

        assertEquals("b1b216d0522b7f5f841b1f5cbd8e5d86ba808a81f5e37826f28bf12dc53fe5ee", Security.computeHashWithSalt(hashed, salt));
        assertEquals("7b4abcf140b0228e3baf8e158245cdee72a0d60150d1506d7c57ab6cb053587d", 
        		Security.computeHashWithSalt("0476b4c947c9d374c0162756cbbe5e4229e062041a47eec50f67d6fca7262098", salt));
    }
}
