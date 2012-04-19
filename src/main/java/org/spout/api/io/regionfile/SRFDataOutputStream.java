package org.spout.api.io.regionfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SRFDataOutputStream extends DataOutputStream {
	
	private int count = 0;

	public SRFDataOutputStream(OutputStream out) {
		super(out);
	}
	
	public void write(byte[] b) throws IOException {
		count += b.length;
		super.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		count += len;
		super.write(b, off, len);
	}
	
	public void write(int b) throws IOException {
		count++;
		super.write(b);
	}
	
	public void close() throws IOException {
		System.out.println("Output stream closed, length = " + count );
		super.close();
	}

}
