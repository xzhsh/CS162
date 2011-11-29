package edu.berkeley.cs.cs162.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * The Board contains all information about a board state. A Board is square,
 * and can have a width of any integer value in the range [3,19].
 */
public class Board {
    private Map<BoardLocation, StoneColor> locations;
    private int size;

    //for debugging purposes only//
    private BoardLocation lastAdded;
    ///////////////////////////////

    /**
     * @param loc
     * @return true if location is valid
     */
    public boolean locationValid(BoardLocation loc) {
        return locations.containsKey(loc);
    }

    public boolean isSameBoard(Board other) {
        for (BoardLocation loc : locations.keySet()) {
            if (other.locations.get(loc) != locations.get(loc)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param size The width of one side of the board.
     */
    public Board(int size) {
        this.locations = new HashMap<BoardLocation, StoneColor>();
        this.size = size;
        lastAdded = new BoardLocation(0, 0);

        if ((size < 3) || (size > 19))
            throw new IllegalArgumentException("Board size out of bounds.");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                locations.put(new BoardLocation(i, j), StoneColor.NONE);
            }
        }
    }

    /**
     * @return The size of the board (width of one side of the board).
     */
    public int getSize() {
        return size;
    }

    /**
     * Add a stone of the specified color to a location on the board.
     */
    public boolean addStone(BoardLocation loc, StoneColor color) {
        if (!locations.containsKey(loc)) {
            return false;
        }
        if (locations.get(loc) == StoneColor.NONE) {
            locations.put(loc, color);
            lastAdded = loc;
            return true;
        }
        return false;
    }

    /**
     * Remove a stone from a location on the board.
     */
    public boolean removeStone(BoardLocation loc) {
        if (!locations.containsKey(loc)) {
            return false;
        }
        if (locations.get(loc) != StoneColor.NONE) {
            locations.put(loc, StoneColor.NONE);
            return true;
        }

        return false;
    }

    /**
     * @return The StoneColor of the stone at the specified location.
     */
    public StoneColor getAtLocation(BoardLocation loc) {
        if (!locations.containsKey(loc)) {
            throw new IllegalArgumentException("Location out of bound");
        }
        // TODO test me
        StoneColor c = locations.get(loc);

        if (c == null) {
            return StoneColor.NONE;
        }

        return c;
    }

    public Board copy() {
        Board board = new Board(size);
        board.locations = new HashMap<BoardLocation, StoneColor>(locations);
        board.lastAdded = this.lastAdded;
        return board;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char representation = '_';
                BoardLocation loc = new BoardLocation(j, i);

                if (loc.equals(lastAdded)) {
                    switch (getAtLocation(loc)) {
                        case BLACK:
                            representation = 'X';
                            break;
                        case WHITE:
                            representation = 'O';
                            break;
                        case NONE:
                            representation = '?';
                            break;
                        default:
                            assert false : "Programmer error: Unimplemented stone color";
                    }
                } else {
                    switch (getAtLocation(loc)) {
                        case BLACK:
                            representation = '\u2022';
                            break;
                        case WHITE:
                            representation = 'o';
                            break;
                        case NONE:
                            representation = '.';
                            break;
                        default:
                            assert false : "Programmer error: Unimplemented stone color";
                    }
                }

                sb.append(representation);
                sb.append(' ');
            }

            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * @param color
     * @return the number of spaces with the specified color
     */
    public int getNumberOfColor(StoneColor color) {
        int count = 0;

        for (StoneColor s : locations.values()) {
            if (s == color) {
                count++;
            }
        }

        return count;
    }
}
