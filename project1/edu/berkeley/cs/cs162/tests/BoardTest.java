package edu.berkeley.cs.cs162.tests;

import edu.berkeley.cs.cs162.Board;
import edu.berkeley.cs.cs162.Location;
import edu.berkeley.cs.cs162.StoneColor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Board class.
 */

public class BoardTest {

    @Test
    public void testConstruction(){

        Board board = new Board(5);
        assertEquals(board.getSize(), 5);

        // You cannot have a board smaller than 3x3
        try { @SuppressWarnings("unused")
		Board smallBoard = new Board(1); }
        catch (IllegalArgumentException e){ assertTrue(true); }

        // You cannot have a board bigger than 19x19
        try { @SuppressWarnings("unused")
		Board bigBoard = new Board(20); }
        catch (IllegalArgumentException e) { assertTrue(true); }

    }

    @Test
    public void testStones(){

        Board board = new Board(5);
        Location loc = new Location(1, 1);
        Location loc2 = new Location(10, 10);

        // You cannot add a stone to an occupied space.
        assertTrue(board.addStone(loc, StoneColor.BLACK));
        assertFalse(board.addStone(loc, StoneColor.WHITE));

        assertEquals(board.getAtLocation(loc), StoneColor.BLACK);

        // You cannot remove a stone from an empty space.
        assertTrue(board.removeStone(loc));
        assertFalse(board.removeStone(loc));

        assertEquals(board.getAtLocation(loc), StoneColor.NONE);

        // You cannot add or remove a stone from an out-of-bounds space.
        assertFalse(board.addStone(loc2, StoneColor.BLACK));
        assertFalse(board.removeStone(loc2));
    }
}
