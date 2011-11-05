package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.berkeley.cs.cs162.Server.Board;

public class BoardInfo implements Writable {
	WritableList<WritableList<StoneColorInfo>> boardState;
    protected BoardInfo(Board b) {
    	WritableList<StoneColorInfo> bz = new WritableList<StoneColorInfo>(StoneColorInfo.class);
    	boardState = new WritableList< WritableList<StoneColorInfo> >();
    	for (int i = 0; i < b.getSize(); i++)
    	{
    		//adds a new row of stone colors.
    		boardState.add(new WritableList<StoneColorInfo>());
    		for (int j = 0; j < b.getSize(); j++)
    		{
    			boardState.get(i).add(new StoneColorInfo(b.getAtLocation(new edu.berkeley.cs.cs162.Server.Location(i,j)).getByte()));
    		}
    	}
    }
    
    protected BoardInfo()
    {
    	
    }

    //@Override
    public void readFrom(InputStream in) throws IOException {
        // TODO Auto-generated method stub

    }

    //@Override
    public void writeTo(OutputStream out) throws IOException {
        // TODO Auto-generated method stub

    }

}
