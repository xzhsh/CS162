package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class CompositeMessage extends Message {
	List<Writable> writables;
	protected CompositeMessage(byte opCode, Writable... writables)
	{
		this.opCode = opCode;
		this.writables = Arrays.asList(writables);
	}
	@Override
	public void readFrom(InputStream in) throws IOException {
		opCode = DataTypeIO.readByte(in);
		//writables = new ArrayList<Writable>(length);
		for (int i = 0; i < writables.size(); i ++)
		{
			writables.get(i).readFrom(in);
		}
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		DataTypeIO.writeByte(out, opCode);
		//writables = new ArrayList<Writable>(length);
		for (int i = 0; i < writables.size(); i ++)
		{
			writables.get(i).writeTo(out);
		}
	}

	@Override
	public boolean isSynchronous() {
		// TODO Auto-generated method stub
		return false;
	}

}
