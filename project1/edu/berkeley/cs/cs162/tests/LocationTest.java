package edu.berkeley.cs.cs162.tests;

import edu.berkeley.cs.cs162.Location;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Location class.
 */

public class LocationTest {

    @Test
    public void testConstruction(){

        Location loc = new Location(1, 2);
        assertEquals(loc.getX(), 1);
        assertEquals(loc.getY(), 2);

    }

    @Test
    public void testEquivalence(){

        Location loc = new Location(1, 2);
        Location loc2 = new Location(1, 2);
        Location loc3 = new Location(1, 3);

        assertTrue(loc.equals(loc2));
        assertFalse(loc.equals(loc3));
    }

    @Test
    public void testParser(){

        String[] tokens = {"", "", "Location", "1", "2"};
        Location ploc = Location.parseMove(tokens);
        Location loc = new Location(1, 2);
        Location loc2 = new Location(1, 3);

        assertTrue(loc.equals(ploc));
        assertFalse(loc2.equals(ploc));
    }
}
