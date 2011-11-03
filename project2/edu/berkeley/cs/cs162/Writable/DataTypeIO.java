package edu.berkeley.cs.cs162.Writable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
public class DataTypeIO {
	public static int readInt(InputStream input) throws IOException
	{
		if (input instanceof DataInputStream)
		{
			return ((DataInputStream) input).readInt();
		}
		else {
			return (new DataInputStream(input)).readInt();
		}
	}
	
	public static String readString(InputStream input) throws IOException
	{
		DataInputStream dataIn;
		if (input instanceof DataInputStream)
		{
			
			dataIn = ((DataInputStream) input);
		}
		else {
			dataIn = new DataInputStream(input);
		}
		int length = dataIn.readInt();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			sb.append(dataIn.readChar());
		}
		return sb.toString();
	}
	
	public static void writeInt(OutputStream output, int val) throws IOException
	{
		if (output instanceof DataOutputStream)
		{
			((DataOutputStream) output).writeInt(val);
		}
		else {
			(new DataOutputStream(output)).writeInt(val);
		}
	}
	
	public static void writeString(OutputStream output, String strVal) throws IOException
	{
		DataOutputStream dataOut;
		if (output instanceof DataOutputStream)
		{
			
			dataOut = ((DataOutputStream) output);
		}
		else {
			dataOut = new DataOutputStream(output);
		}
		dataOut.writeInt(strVal.length());
		for (int i = 0; i < strVal.length(); i++)
		{
			dataOut.writeChar(strVal.charAt(i));
		}
	}

	public static byte readByte(InputStream input) throws IOException {
		int val = input.read();
		if (val > -1)
		{
			return (byte) val;
		}
		else{
			throw new IOException("End of stream reached");
		}
	}
	public static void writeByte(OutputStream output, byte val) throws IOException {
		output.write(val);
	}
}