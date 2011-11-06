package edu.berkeley.cs.cs162.Server;

import java.util.Vector;

/**
 * Wrapper class for the Board with GoRules.
 *
 * @author xshi
 */
public class GoBoard {
    Board board;
    Board prevBoard;

    public Board getCurrentBoard() {
        return board;
    }

    /**
     * Exception thrown if a move is illegal.
     *
     * @author xshi
     */
    class IllegalMoveException extends Exception {
        private static final long serialVersionUID = 1L;

        public IllegalMoveException(String message) {
            super("Illegal move: " + message);
        }
    }

    /**
     * Constructs a new GoBoard with a certain size.
     *
     * @param size
     */
    public GoBoard(int size) {
        board = new Board(size);
        prevBoard = new Board(size);
    }

    /**
     * Copies the target board.
     *
     * @param goBoard
     */
    public GoBoard(GoBoard goBoard) {
        board = goBoard.board.copy();
        prevBoard = goBoard.prevBoard.copy();
    }

    public GoBoard(Board initialBoard) {
        board = initialBoard.copy();
        prevBoard = initialBoard.copy();
    }

    /**
     * Makes a specific move in this GoBoard.
     * <p/>
     * This will change the state of this board unless an {@link IllegalMoveException} is thrown
     *
     * @param moveLoc     Where the stone is placed
     * @param activeColor The color of the stone
     * @return A {@link Vector} of {@link BoardLocation} of stones that were captured, if any.
     * @throws IllegalMoveException if the move is illegal
     */
    public Vector<BoardLocation> makeMove(BoardLocation moveLoc, StoneColor activeColor) throws IllegalMoveException {
        Board tempBoard = board.copy();
        Vector<BoardLocation> captured = testMove(moveLoc, activeColor, tempBoard);

        //propagate the changes.
        prevBoard = board.copy();
        board = tempBoard.copy();

        return captured;
    }

    /**
     * @return a copy of this GoBoard's state.
     */
    public GoBoard copy() {
        GoBoard goBoard = new GoBoard(this);
        return goBoard;
    }

    public Vector<BoardLocation> testMove(BoardLocation moveLoc, StoneColor activeColor) throws IllegalMoveException {
        Board tempBoard = board.copy();
        return testMove(moveLoc, activeColor, tempBoard);
    }

    public Vector<BoardLocation> testMove(BoardLocation moveLoc, StoneColor activeColor, Board tempBoard) throws IllegalMoveException {
        //Temporary board, saved so we can restore the board if something happens.
        if (!tempBoard.locationValid(moveLoc)) {
            //if the location is not valid, or if there is already a piece there, send a illegal move error
            throw new IllegalMoveException("Location doesn't exist on board");
        }

        if (tempBoard.getAtLocation(moveLoc) != StoneColor.NONE) {
            //if the location is not valid, or if there is already a piece there, send a illegal move error
            throw new IllegalMoveException("Stone exists at location");
        }

        Vector<BoardLocation> captured = Rules.getCapturedStones(tempBoard, activeColor, moveLoc);

        //change the board
        tempBoard.addStone(moveLoc, activeColor);

        for (BoardLocation loc : captured) {
            tempBoard.removeStone(loc);
        }

        if (tempBoard.isSameBoard(prevBoard)) {
            //Ko rule.
            throw new IllegalMoveException("Ko rule excludes this play");
        }

        return captured;
    }

    public String toString() {
        return board.toString();
    }

    public int getSize() {
        return board.getSize();
    }
}
