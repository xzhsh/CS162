package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.StoneColor;

public class BoardInfo implements Writable {

    private StoneColorInfo[][] board;

    public BoardInfo() {
    	
    }
    
    protected BoardInfo(int size) {
    	board = new StoneColorInfo[size][size];
    }

    protected StoneColorInfo[][] getStoneColorState() {
        return board;
    }

    @Override
    public void readFrom(InputStream in) throws IOException {
        int xlen = DataTypeIO.readInt(in);
        board = new StoneColorInfo[xlen][];
        for (int i = 0; i < xlen; i++) {
            int ylen = DataTypeIO.readInt(in);
            board[i] = new StoneColorInfo[ylen];
            for (int j = 0; j < ylen; j++) {
                board[i][j] = new StoneColorInfo();
                board[i][j].readFrom(in);
            }
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        int xlen = board.length;
        DataTypeIO.writeInt(out, xlen);
        for (int i = 0; i < xlen; i++) {
            int ylen = board[i].length;
            DataTypeIO.writeInt(out, ylen);
            for (int j = 0; j < ylen; j++) {
                board[i][j].writeTo(out);
            }
        }
    }

    public GoBoard getNewBoard() {
        return new GoBoard(board.length);
    }

    // TODO TEST ME
    public GoBoard getBoard() {
        Board b = new Board(board.length);
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                b.addStone(new BoardLocation(i, j), StoneColor.getStoneColor(board[i][j].getColor()));
            }
        }

        return new GoBoard(b);
    }
}
