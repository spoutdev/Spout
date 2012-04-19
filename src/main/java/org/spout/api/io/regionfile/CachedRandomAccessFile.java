package org.spout.api.io.regionfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class CachedRandomAccessFile {

	private final RandomAccessFile file;
	private long pos = 0;
	private final ArrayList<byte[]> pages = new ArrayList<byte[]>();
	private final int PAGE_SHIFT = 16;
	private final int PAGE_SIZE = (1 << PAGE_SHIFT);
	private final long PAGE_MASK = PAGE_SIZE - 1;
	
	public CachedRandomAccessFile(File filePath, String permissions) throws FileNotFoundException {
		this.file = new RandomAccessFile(filePath, permissions);
	}
	
	public long length() throws IOException {
		return file.length();
	}
	
	public void close() throws IOException {
		file.close();
	}
	
	public void writeInt(int i) throws IOException {
		writeCacheByte((byte)(i >> 24));
		writeCacheByte((byte)(i >> 16));
		writeCacheByte((byte)(i >> 8));
		writeCacheByte((byte)(i >> 0));
	}
	
	public int readInt() throws IOException {
		int i = 0;
		i |= (readCacheByte() & 0xFF) << 24;
		i |= (readCacheByte() & 0xFF) << 16;
		i |= (readCacheByte() & 0xFF) << 8;
		i |= (readCacheByte() & 0xFF) << 0;
		return i;
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
			long len = Math.min(file.length() - pos, page.length);
			if (len > 0) {
				file.readFully(page, 0, (int)len);
			}
			pages.set(pageIndex, page);
			pos = oldPos;
		}
		return page;
	}
	
	public void seek(long pos) throws IOException {
		file.seek(pos);
		this.pos = pos;
	}
	
	public void readFully(byte[] b) throws IOException {
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
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		int endPageOne = Math.min(b.length + offset, PAGE_SIZE);
		
		byte[] page = getPage(pageIndex);
		
		int j = 0;
		for (int i = offset; i < endPageOne; i++) {
			page[i] = b[off + (j++)];
		}
		
		while (b.length > j) {
			pageIndex++;
			page = getPage(pageIndex);
			if (b.length - j > PAGE_SIZE) {
				for (int i = 0; i < PAGE_SIZE; i++) {
					page[i] = b[off + (j++)];
				}
			} else {
				for (int i = 0; j < b.length; i++) {
					page[i] = b[off + (j++)];
				}
			}
		}
		
		file.seek(pos);
		file.write(b, off, len);
		
		pos += b.length;
	}
	
	public void writeCacheByte(byte b) throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		byte[] page = getPage(pageIndex);
		page[offset] = b;
		file.seek(pos);
		file.writeByte(b & 0xFF);
		pos++;
	}
	
	public byte readCacheByte() throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		byte[] page = getPage(pageIndex);
		pos++;
		return page[offset];
	}

	
}
