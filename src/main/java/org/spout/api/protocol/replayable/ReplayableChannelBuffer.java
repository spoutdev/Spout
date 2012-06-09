/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.protocol.replayable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;

public class ReplayableChannelBuffer implements ChannelBuffer {
	
	private final static Error ERROR_INSTANCE = new ReplayableError("");

	private ChannelBuffer buffer;
	
	public ChannelBuffer setBuffer(ChannelBuffer buffer) {
		this.buffer = buffer;
		return this;
	}
	
	@Override
	public ChannelBufferFactory factory() {
		return buffer.factory();
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public ByteOrder order() {
		return buffer.order();
	}

	@Override
	public boolean isDirect() {
		return buffer.isDirect();
	}

	@Override
	public int readerIndex() {
		return buffer.readerIndex();
	}

	@Override
	public void readerIndex(int readerIndex) {
		indexRangeCheck(readerIndex);
		buffer.readerIndex(readerIndex);
	}

	@Override
	public int writerIndex() {
		return buffer.writerIndex();
	}

	@Override
	public void writerIndex(int writerIndex) {
		indexRangeCheck(writerIndex);
	}

	@Override
	public void setIndex(int readerIndex, int writerIndex) {
		indexRangeCheck(readerIndex);
		indexRangeCheck(writerIndex);
		buffer.setIndex(readerIndex, writerIndex);
	}

	@Override
	public int readableBytes() {
		return Integer.MAX_VALUE - buffer.readableBytes();
	}

	@Override
	public int writableBytes() {
		return buffer.writableBytes();
	}

	@Override
	public boolean readable() {
		return true;
	}

	@Override
	public boolean writable() {
		return buffer.writable();
	}

	@Override
	public void clear() {
		unsupported();
	}

	@Override
	public void markReaderIndex() {
		buffer.markReaderIndex();
	}

	@Override
	public void resetReaderIndex() {
		buffer.resetReaderIndex();
	}

	@Override
	public void markWriterIndex() {
		buffer.markWriterIndex();
	}

	@Override
	public void resetWriterIndex() {
		buffer.resetWriterIndex();
	}

	@Override
	public void discardReadBytes() {
		unsupported();
	}

	@Override
	public void ensureWritableBytes(int writableBytes) {
		unsupported();
	}

	@Override
	public byte getByte(int index) {
		checkAvail(index, 1);
		return buffer.getByte(index);
	}

	@Override
	public short getUnsignedByte(int index) {
		checkAvail(index, 1);
		return buffer.getUnsignedByte(index);
	}

	@Override
	public short getShort(int index) {
		checkAvail(index, 2);
		return buffer.getShort(index);
	}

	@Override
	public int getUnsignedShort(int index) {
		checkAvail(index, 2);
		return buffer.getUnsignedShort(index);
	}

	@Override
	public int getMedium(int index) {
		checkAvail(index, 3);
		return buffer.getMedium(index);
	}

	@Override
	public int getUnsignedMedium(int index) {
		checkAvail(index, 3);
		return buffer.getUnsignedMedium(index);
	}

	@Override
	public int getInt(int index) {
		checkAvail(index, 4);
		return buffer.getInt(index);
	}

	@Override
	public long getUnsignedInt(int index) {
		checkAvail(index, 4);
		return buffer.getUnsignedInt(index);
	}

	@Override
	public long getLong(int index) {
		checkAvail(index, 8);
		return buffer.getLong(index);
	}

	@Override
	public char getChar(int index) {
		checkAvail(index, 1);
		return buffer.getChar(index);
	}

	@Override
	public float getFloat(int index) {
		checkAvail(index, 4);
		return buffer.getFloat(index);
	}

	@Override
	public double getDouble(int index) {
		checkAvail(index, 4);
		return buffer.getFloat(index);
	}

	@Override
	public void getBytes(int index, ChannelBuffer dst) {
		unsupported();
	}

	@Override
	public void getBytes(int index, ChannelBuffer dst, int length) {
		unsupported();
	}

	@Override
	public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length) {
		unsupported();
	}

	@Override
	public void getBytes(int index, byte[] dst) {
		checkAvail(index, dst.length);
		buffer.getBytes(index, dst);
	}

	@Override
	public void getBytes(int index, byte[] dst, int dstIndex, int length) {
		checkAvail(index, length);
		buffer.getBytes(index, dst, dstIndex, length);
	}

	@Override
	public void getBytes(int index, ByteBuffer dst) {
		unsupported();
	}

	@Override
	public void getBytes(int index, OutputStream out, int length) throws IOException {
		unsupported();
	}

	@Override
	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public void setByte(int index, int value) {
		unsupported();
	}

	@Override
	public void setShort(int index, int value) {
		unsupported();
	}

	@Override
	public void setMedium(int index, int value) {
		unsupported();
	}

	@Override
	public void setInt(int index, int value) {
		unsupported();
	}

	@Override
	public void setLong(int index, long value) {
		unsupported();
	}

	@Override
	public void setChar(int index, int value) {
		unsupported();
	}

	@Override
	public void setFloat(int index, float value) {
		unsupported();
	}

	@Override
	public void setDouble(int index, double value) {
		unsupported();
	}

	@Override
	public void setBytes(int index, ChannelBuffer src) {
		unsupported();
	}

	@Override
	public void setBytes(int index, ChannelBuffer src, int length) {
		unsupported();
	}

	@Override
	public void setBytes(int index, ChannelBuffer src, int srcIndex, int length) {
		unsupported();
	}

	@Override
	public void setBytes(int index, byte[] src) {
		unsupported();
	}

	@Override
	public void setBytes(int index, byte[] src, int srcIndex, int length) {
		unsupported();
	}

	@Override
	public void setBytes(int index, ByteBuffer src) {
		unsupported();
	}

	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public void setZero(int index, int length) {
		unsupported();
	}

	@Override
	public byte readByte() {
		checkAvail(1);
		return buffer.readByte();
	}

	@Override
	public short readUnsignedByte() {
		checkAvail(1);
		return buffer.readUnsignedByte();
	}

	@Override
	public short readShort() {
		checkAvail(2);
		return buffer.readShort();
	}

	@Override
	public int readUnsignedShort() {
		checkAvail(2);
		return buffer.readUnsignedShort();
	}

	@Override
	public int readMedium() {
		checkAvail(3);
		return buffer.readMedium();
	}

	@Override
	public int readUnsignedMedium() {
		checkAvail(3);
		return buffer.readUnsignedMedium();
	}

	@Override
	public int readInt() {
		checkAvail(4);
		return buffer.readInt();
	}

	@Override
	public long readUnsignedInt() {
		checkAvail(4);
		return buffer.readInt();
	}

	@Override
	public long readLong() {
		checkAvail(8);
		return buffer.readLong();
	}

	@Override
	public char readChar() {
		checkAvail(1);
		return buffer.readChar();
	}

	@Override
	public float readFloat() {
		checkAvail(4);
		return buffer.readFloat();
	}

	@Override
	public double readDouble() {
		checkAvail(8);
		return buffer.readDouble();
	}

	@Override
	public ChannelBuffer readBytes(int length) {
		checkAvail(length);
		int readable = buffer.readableBytes();
		try {
			return buffer.readBytes(length);
		} catch (ReplayableError e) {
			System.out.println("Error: readable = " + readable + " length = " + length);
			throw e;
		}
	}

	@Override
	public ChannelBuffer readBytes(ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return null;
	}

	@Override
	public ChannelBuffer readSlice(int length) {
		checkAvail(length);
		return buffer.readSlice(length);
	}

	@Override
	public ChannelBuffer readSlice(ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return null;
	}

	@Override
	public void readBytes(ChannelBuffer dst) {
		unsupported();
	}

	@Override
	public void readBytes(ChannelBuffer dst, int length) {
		unsupported();
	}

	@Override
	public void readBytes(ChannelBuffer dst, int dstIndex, int length) {
		checkAvail(length);
		buffer.readBytes(dst, dstIndex, length);
	}

	@Override
	public void readBytes(byte[] dst) {
		if (dst.length == 0) {
			throw new IllegalStateException("Shouldn't be reading length zero buffers");
		}
		checkAvail(dst.length);
		try {
			buffer.readBytes(dst);
		} catch (ReplayableError e) {
			throw e;
		}
	}

	@Override
	public void readBytes(byte[] dst, int dstIndex, int length) {
		checkAvail(length);
		buffer.readBytes(dst, dstIndex, length);
	}

	@Override
	public void readBytes(ByteBuffer dst) {
		unsupported();
	}

	@Override
	public void readBytes(OutputStream out, int length) throws IOException {
		unsupported();
	}

	@Override
	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public void skipBytes(int length) {
		checkAvail(length);
		buffer.skipBytes(length);
	}

	@Override
	public int skipBytes(ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return 0;
	}

	@Override
	public void writeByte(int value) {
		unsupported();
	}

	@Override
	public void writeShort(int value) {
		unsupported();
	}

	@Override
	public void writeMedium(int value) {
		unsupported();
	}

	@Override
	public void writeInt(int value) {
		unsupported();
	}

	@Override
	public void writeLong(long value) {
		unsupported();
	}

	@Override
	public void writeChar(int value) {
		unsupported();
	}

	@Override
	public void writeFloat(float value) {
		unsupported();
	}

	@Override
	public void writeDouble(double value) {
		unsupported();
	}

	@Override
	public void writeBytes(ChannelBuffer src) {
		unsupported();
	}

	@Override
	public void writeBytes(ChannelBuffer src, int length) {
		unsupported();
	}

	@Override
	public void writeBytes(ChannelBuffer src, int srcIndex, int length) {
		unsupported();
	}

	@Override
	public void writeBytes(byte[] src) {
		unsupported();
	}

	@Override
	public void writeBytes(byte[] src, int srcIndex, int length) {
		unsupported();
	}

	@Override
	public void writeBytes(ByteBuffer src) {
		unsupported();
	}

	@Override
	public int writeBytes(InputStream in, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		unsupported();
		return 0;
	}

	@Override
	public void writeZero(int length) {
		unsupported();
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, byte value) {
		indexRangeCheck(toIndex);
		indexRangeCheck(fromIndex);
		return buffer.indexOf(fromIndex, toIndex, value);
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(byte value) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(int length, byte value) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(int length, ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		unsupported();
		return 0;
	}

	@Override
	public int bytesBefore(int index, int length, ChannelBufferIndexFinder indexFinder) {
		unsupported();
		return 0;
	}

	@Override
	public ChannelBuffer copy() {
		unsupported();
		return null;
	}

	@Override
	public ChannelBuffer copy(int index, int length) {
		checkAvail(index, length);
		return buffer.copy(index, length);
	}

	@Override
	public ChannelBuffer slice() {
		return buffer.slice();
	}

	@Override
	public ChannelBuffer slice(int index, int length) {
		checkAvail(index, length);
		return buffer.slice(index, length);
	}

	@Override
	public ChannelBuffer duplicate() {
		unsupported();
		return null;
	}

	@Override
	public ByteBuffer toByteBuffer() {
		unsupported();
		return null;
	}

	@Override
	public ByteBuffer toByteBuffer(int index, int length) {
		unsupported();
		return null;
	}

	@Override
	public ByteBuffer[] toByteBuffers() {
		unsupported();
		return null;
	}

	@Override
	public ByteBuffer[] toByteBuffers(int index, int length) {
		unsupported();
		return null;
	}

	@Override
	public boolean hasArray() {
		return false;
	}

	@Override
	public byte[] array() {
		unsupported();
		return null;
	}

	@Override
	public int arrayOffset() {
		unsupported();
		return 0;
	}

	@Override
	public String toString(Charset charset) {
		return buffer.toString();
	}

	@Override
	public String toString(int index, int length, Charset charset) {
		checkAvail(index, length);
		return buffer.toString(index, length, charset);
	}

	@Override
	public String toString(String charsetName) {
		unsupported();
		return null;
	}

	@Override
	public String toString(String charsetName, ChannelBufferIndexFinder terminatorFinder) {
		unsupported();
		return null;
	}

	@Override
	public String toString(int index, int length, String charsetName) {
		unsupported();
		return null;
	}

	@Override
	public String toString(int index, int length, String charsetName, ChannelBufferIndexFinder terminatorFinder) {
		unsupported();
		return null;
	}

	@Override
	public int compareTo(ChannelBuffer buffer) {
		unsupported();
		return 0;
	}

	private void unsupported() {
		throw new UnsupportedOperationException("This method is not supported for a replayable channel buffer");
	}
	
	private void indexRangeCheck(int index) {
		checkAvail(index, 0);
	}
	
	private void checkAvail(int length) {
		if (buffer.readableBytes() < length) {
			throw ERROR_INSTANCE;
		}
	}
	
	private void checkAvail(int index, int length) {
		if (index + length > buffer.writerIndex()) {
			throw ERROR_INSTANCE;
		}
	}
	
}
