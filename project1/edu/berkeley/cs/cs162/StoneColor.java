package edu.berkeley.cs.cs162;

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
}

