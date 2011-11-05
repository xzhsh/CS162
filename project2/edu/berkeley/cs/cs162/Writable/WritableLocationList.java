package edu.berkeley.cs.cs162.Writable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

public class WritableLocationList implements Writable{

    private Location[] locations;

    protected WritableLocationList(List<Location> list)
    {
        locations = list.toArray(locations);
    }

    public void writeTo(OutputStream output) throws IOException
    {
        int length = locations.length;
        DataTypeIO.writeInt(output, length);
        for(int i = 0; i < length; i++)
        {
            locations[i].writeTo(output);
        }
    }

    public void readFrom(InputStream input) throws IOException
    {
        int length = DataTypeIO.readInt(input);
        locations = new Location[length];
        for(int i = 0; i < length; i++)
        {
            locations[i] = new Location();
            locations[i].readFrom(input);
        }
    }

}
