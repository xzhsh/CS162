package edu.berkeley.cs.cs162.Writable;

import java.io.*;

public class DataTypeIO {

    private static DataInputStream getDataInputStream(InputStream input) {
        return (DataInputStream)input;//(input instanceof DataInputStream) ? (DataInputStream) input : new DataInputStream(input);
    }

    private static DataOutputStream getDataOutputStream(OutputStream output) {
        return (DataOutputStream) output;//(output instanceof DataOutputStream) ? (DataOutputStream) output : new DataOutputStream(output);
    }

    /**
     * Read and write ints.
     */

    public static int readInt(InputStream input) throws IOException {
        return getDataInputStream(input).readInt();
    }

    public static void writeInt(OutputStream output, int val) throws IOException {
        getDataOutputStream(output).writeInt(val);
    }


    /**
     * Read and write Strings.
     */

    public static String readString(InputStream input) throws IOException {
        DataInputStream dataIn = getDataInputStream(input);

        int length = dataIn.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(dataIn.readChar());
        }
        return sb.toString();
    }

    public static void writeString(OutputStream output, String strVal) throws IOException {
        DataOutputStream dataOut = getDataOutputStream(output);

        dataOut.writeInt(strVal.length());
        for (int i = 0; i < strVal.length(); i++) {
            dataOut.writeChar(strVal.charAt(i));
        }
    }


    /**
     * Read and write bytes.
     */
    public static byte readByte(InputStream input) throws IOException {
        int val = input.read();
        if (val > -1) {
            return (byte) val;
        } else {
            throw new IOException("End of stream reached");
        }
    }

    public static void writeByte(OutputStream output, byte val) throws IOException {
        output.write(val);
    }

    /**
     * Read and write doubles.
     */

    public static double readDouble(InputStream input) throws IOException {
        return getDataInputStream(input).readDouble();
    }

    public static void writeDouble(OutputStream output, double val) throws IOException {
        getDataOutputStream(output).writeDouble(val);
    }
}