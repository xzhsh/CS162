package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.io.OutputStream;

final class NullOutputStream extends OutputStream {
	@Override
	public void write(int arg0) throws IOException {
		//no-op.
	}
}