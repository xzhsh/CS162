package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public enum StoneColor {
    BLACK, WHITE, NONE;

    public String toString() {
        switch (this) {
            case BLACK:
                return "Black";
            case WHITE:
                return "White";
            case NONE:
                return "None";
            default:
                assert false : "Unimplemented Stone Color";
                return "UNIMPLEMENTED";
        }
    }

    public byte getByte() {
        switch (this) {
            case BLACK:
                return MessageProtocol.STONE_BLACK;
            case WHITE:
                return MessageProtocol.STONE_WHITE;
            case NONE:
                return MessageProtocol.STONE_NONE;
            default:
                assert false : "Unimplemented Stone Color";
                return MessageProtocol.UNUSED;
        }
    }
}

