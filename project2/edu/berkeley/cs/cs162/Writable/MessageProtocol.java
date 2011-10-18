package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;

public class MessageProtocol {
    
    // StoneColor definitions
    public static final byte STONE_BLACK = (byte) 0;
    public static final byte STONE_WHITE = (byte) 1;
    public static final byte STONE_NONE  = (byte) 2;
    
    // PlayerType definitions
    public static final byte TYPE_HUMAN = (byte) 0;
    public static final byte TYPE_MACHINE = (byte) 1;
    public static final byte TYPE_OBSERVER = (byte) 2;
    
    // MoveType definitions
    public static final byte MOVE_STONE = (byte) 0;
    public static final byte MOVE_PASS = (byte) 1;
    
    // Client to server message opcodes
    public static final byte OP_TYPE_CONNECT     = (byte) 10;
    public static final byte OP_TYPE_DISCONNECT  = (byte) 11;
    public static final byte OP_TYPE_SENDMOVE    = (byte) 12;
    public static final byte OP_TYPE_LISTGAMES   = (byte) 13;
    public static final byte OP_TYPE_JOIN        = (byte) 14;
    public static final byte OP_TYPE_LEAVE       = (byte) 15;
    public static final byte OP_TYPE_WAITFORGAME = (byte) 16;
    
    // Server to client message opcodes
    public static final byte OP_TYPE_GAMESTART     = (byte) 20;
    public static final byte OP_TYPE_GAMEOVER      = (byte) 21;
    public static final byte OP_TYPE_MAKEMOVE      = (byte) 22;
    public static final byte OP_TYPE_STONECAPTURED = (byte) 23;
    public static final byte OP_TYPE_PLAYERERROR   = (byte) 24;
    
    // Status return codes
    public static final byte OP_STATUS_OK = (byte) 10;
    
    // Error return codes
    public static final byte OP_ERROR_REJECTED     = (byte) 20;
    public static final byte OP_ERROR_INVALID_GAME = (byte) 21;
    public static final byte OP_ERROR_INVALID_USER = (byte) 22;
    public static final byte OP_ERROR_UNCONNECTED  = (byte) 23;
    
    // gameOver status codes
    public static final byte GAME_OK = (byte) 10;
    
    // playerError error codes
    public static final byte PLAYER_INVALID_MOVE  = (byte) 20;
    public static final byte PLAYER_TIMEOUT       = (byte) 21;
    public static final byte PLAYER_KO_RULE       = (byte) 22;
    public static final byte PLAYER_DISCONNECT    = (byte) 23;
    
	public static byte readOpCodeFrom(InputStream input) throws IOException {
		int results = input.read();
		if (results > 0)
		{
			return (byte)results;
		}
		assert false : "Programmer error, inputstream should never be EOF";
		throw new IOException("Invalid read from inputstream");
	}    
}
