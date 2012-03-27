package org.spout.api.io.regionfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class SimpleRegionFileOutputStream extends ByteArrayOutputStream  {

	private final SimpleRegionFile srf;
	private final int index;
	private final Lock lock;
	private final AtomicBoolean lockUnlocked;
	
	SimpleRegionFileOutputStream(SimpleRegionFile srf, int index, int estimatedSize, Lock lock) {
		super(estimatedSize);
		this.srf = srf;
		this.index = index;
		this.lock = lock;
		this.lockUnlocked = new AtomicBoolean(false);
	}
	
	@Override
	public void close() throws IOException {
		if (this.lockUnlocked.compareAndSet(false, true)) {
			try {
				srf.write(index, buf, count);
			} finally {
				lock.unlock();
			}
		} else {
			throw new SimpleRegionFileException("Attempt made to close a block output stream twice");
		}
	}
	
}