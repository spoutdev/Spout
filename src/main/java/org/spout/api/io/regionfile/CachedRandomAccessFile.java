package org.spout.api.io.regionfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class CachedRandomAccessFile {

	private final RandomAccessFile file;
	private long pos = 0;
	private boolean posDirty = false;
	private final ArrayList<byte[]> pages = new ArrayList<byte[]>();
	private final int PAGE_SHIFT;
	private final int PAGE_SIZE;
	private final long PAGE_MASK;

	private static boolean debug = false;
	private static AtomicLong timeUsed = new AtomicLong(0);
	private static AtomicLong lastReport = new AtomicLong(0);
	private long timeUsedLocal = 0;

	public CachedRandomAccessFile(File filePath, String permissions) throws FileNotFoundException {
		this(filePath, permissions, 16);
	}

	public CachedRandomAccessFile(File filePath, String permissions, int pageShift) throws FileNotFoundException {
		this.file = new RandomAccessFile(filePath, permissions);
		this.PAGE_SHIFT = pageShift;
		PAGE_SIZE = (1 << PAGE_SHIFT);
		PAGE_MASK = PAGE_SIZE - 1;
	}

	public long length() throws IOException {
		timeStart();
		try {
			return file.length();
		} finally {
			timeEnd();
		}
	}

	public void close() throws IOException {
		timeStart();
		try {
			file.close();
		} finally {
			timeEnd();
		}
	}

	public void writeInt(int i) throws IOException {
		timeStart();
		try {
			writeCacheByte((byte)(i >> 24));
			writeCacheByte((byte)(i >> 16));
			writeCacheByte((byte)(i >> 8));
			writeCacheByte((byte)(i >> 0));
		} finally {
			timeEnd();
		}
	}

	public int readInt() throws IOException {
		timeStart();
		try {
			int i = 0;
			i |= (readCacheByte() & 0xFF) << 24;
			i |= (readCacheByte() & 0xFF) << 16;
			i |= (readCacheByte() & 0xFF) << 8;
			i |= (readCacheByte() & 0xFF) << 0;
			return i;
		} finally {
			timeEnd();
		}
	}

	private byte[] getPage(int pageIndex) throws IOException {
		while (pageIndex >= pages.size()) {
			pages.add(null);
		}
		byte[] page = pages.get(pageIndex);
		if (page == null) {
			long oldPos = pos;
			long pagePosition = pageIndex << PAGE_SHIFT;
			file.seek(pagePosition);
			page = new byte[PAGE_SIZE];
			long len = Math.min(file.length() - pagePosition, page.length);
			if (len > 0) {
				file.readFully(page, 0, (int)len);
			}
			pages.set(pageIndex, page);
			pos = oldPos;
			posDirty = true;
		}
		return page;

	}

	public void seek(long pos) throws IOException {
		timeStart();
		try {
			file.seek(pos);
			this.pos = pos;
			posDirty = false;
		} finally {
			timeEnd();
		}
	}

	public void readFully(byte[] b) throws IOException {
		timeStart();
		try {
			int pageIndex = (int)(pos >> PAGE_SHIFT);
			int offset = (int)(pos & PAGE_MASK);
			int endPageOne = Math.min(b.length + offset, PAGE_SIZE);

			byte[] page = getPage(pageIndex);

			int j = 0;
			for (int i = offset; i < endPageOne; i++) {
				b[j++] = page[i];
			}

			while (b.length > j) {
				pageIndex++;
				page = getPage(pageIndex);
				if (b.length - j > PAGE_SIZE) {
					for (int i = 0; i < PAGE_SIZE; i++) {
						b[j++] = page[i];
					}
				} else {
					for (int i = 0; j < b.length; i++) {
						b[j++] = page[i];
					}
				}
			}

			pos += b.length;
			posDirty = true;
		} finally {
			timeEnd();
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		timeStart();
		try {
			int pageIndex = (int)(pos >> PAGE_SHIFT);
			int offset = (int)(pos & PAGE_MASK);
			int endPageOne = Math.min(len + offset, PAGE_SIZE);

			byte[] page = getPage(pageIndex);

			int j = 0;
			for (int i = offset; i < endPageOne; i++) {
				page[i] = b[off + (j++)];
			}

			while (len > j) {
				pageIndex++;
				page = getPage(pageIndex);
				if (len - j > PAGE_SIZE) {
					for (int i = 0; i < PAGE_SIZE; i++) {
						page[i] = b[off + (j++)];
					}
				} else {
					for (int i = 0; j < len; i++) {
						page[i] = b[off + (j++)];
					}
				}
			}

			if (posDirty) {
				file.seek(pos);
				posDirty = false;
			}
			file.write(b, off, len);

			pos += b.length;
		} finally {
			timeEnd();
		}
	}

	private void writeCacheByte(byte b) throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		byte[] page = getPage(pageIndex);
		page[offset] = b;
		if (posDirty) {
			file.seek(pos);
			posDirty = false;
		}
		file.writeByte(b & 0xFF);
		pos++;
	}

	private byte readCacheByte() throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		byte[] page = getPage(pageIndex);
		pos++;
		posDirty = true;
		return page[offset];
	}

	private void timeStart() {
		if (debug) {
			timeUsedLocal -= System.nanoTime();
		}
	}

	private void timeEnd() {
		if (debug) {
			timeUsedLocal += System.nanoTime();
			timeUsed.addAndGet(timeUsedLocal);
			timeUsedLocal = 0;
			long currentTime = System.currentTimeMillis();
			long lastReportLocal = lastReport.get();
			long timeElapsed = currentTime - lastReportLocal;
			if (timeElapsed > 5000) {
				if (lastReport.compareAndSet(lastReportLocal, currentTime)) {
					System.out.println("Time since last report: " + timeElapsed);
					long timeUsedLocal = timeUsed.get();
					timeUsed.addAndGet(-timeUsedLocal);
					System.out.println("Time consumed: " + timeUsedLocal);
				}
			}
		}
	}

}
