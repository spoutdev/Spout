package org.getspout.spout.chunkstore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SimpleChunkBuffer extends ByteArrayOutputStream  {

	final SimpleRegionFile rf;
	final int index;
	
	SimpleChunkBuffer(SimpleRegionFile rf, int index) {
		super(1024);
		this.rf = rf;
		this.index = index;
	}
	
	@Override
	public void close() throws IOException {
		rf.write(index, buf, count);
	}
	
}
