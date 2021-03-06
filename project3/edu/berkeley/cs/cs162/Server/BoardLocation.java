package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.Location;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

/**
 * A wrapper for a location on the board. x,y represent the coordinates of a
 * position on the board.
 */

public class BoardLocation {
    private int x, y;

    public BoardLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

	public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Object other) {
        if (other instanceof BoardLocation) {
            BoardLocation otherLocation = (BoardLocation) other;
            return this.x == otherLocation.x && this.y == otherLocation.y;
        }

        return false;
    }

    public int hashCode() {
        return x + 101 * y;
    }

    public String toString() {
        return "(" + getX() + "," + getY() + ")";
    }

    public static BoardLocation parseMove(String[] tokens) {
        if (tokens.length == 5) {
            try {
                if (tokens[2].equals("Location")) {
                    int x = Integer.valueOf(tokens[3]);
                    int y = Integer.valueOf(tokens[4]);
                    return new BoardLocation(x, y);
                }
            } catch (NumberFormatException e) {
                assert false : "Unparseable Location String";
                return null;
            }
        }

        assert false : "Unparseable Location String";
        return null;
    }

    public String toLocationMessage() {
        return "Location" + " " + x + " " + y;
    }

	public Location makeLocationInfo() {
		return MessageFactory.createLocationInfo(x,y);
	}
}
