package edu.berkeley.cs.cs162.Writable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class WritableDouble implements Writable{

    private double value;

    protected WritableDouble()
    {
        value = 0;
    }

    protected WritableDouble(double value)
    {
        this.value = value;
    }

    public void writeTo(OutputStream output) throws IOException
    {
        DataTypeIO.writeDouble(output, value);
    }

    public void readFrom(InputStream input) throws IOException
    {
        value = DataTypeIO.readDouble(input);
    }

}
