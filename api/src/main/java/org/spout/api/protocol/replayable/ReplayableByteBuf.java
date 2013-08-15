/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;

public class ReplayableByteBuf extends ByteBuf {
	private final static ReplayableException ERROR_INSTANCE = new ReplayableException("");
	private ByteBuf buffer;

	public ByteBuf setBuffer(ByteBuf buffer) {
		this.buffer = buffer;
		return this;
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
	public ByteBuf readerIndex(int readerIndex) {
		indexRangeCheck(readerIndex);
		buffer.readerIndex(readerIndex);
		return this;
	}

	@Override
	public int writerIndex() {
		return buffer.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int writerIndex) {
		indexRangeCheck(writerIndex);
		return this;
	}

	@Override
	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		indexRangeCheck(readerIndex);
		indexRangeCheck(writerIndex);
		buffer.setIndex(readerIndex, writerIndex);
		return this;
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
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return buffer.isWritable();
	}

	@Override
	public ByteBuf clear() {
		return unsupported();
	}

	@Override
	public ByteBuf markReaderIndex() {
		buffer.markReaderIndex();
		return this;
	}

	@Override
	public ByteBuf resetReaderIndex() {
		buffer.resetReaderIndex();
		return this;
	}

	@Override
	public ByteBuf markWriterIndex() {
		buffer.markWriterIndex();
		return this;
	}

	@Override
	public ByteBuf resetWriterIndex() {
		buffer.resetWriterIndex();
		return this;
	}

	@Override
	public ByteBuf discardReadBytes() {
		return unsupported();
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
		checkAvail(index, 2);
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
	public ByteBuf getBytes(int index, ByteBuf dst) {
		return unsupported();
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst) {
		checkAvail(index, dst.length);
		buffer.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
		checkAvail(index, length);
		buffer.getBytes(index, dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuffer dst) {
		return unsupported();
	}

	@Override
	public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
		return unsupported();
	}

	@Override
	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		return unsupported();
	}

	@Override
	public ByteBuf setByte(int index, int value) {
		return unsupported();
	}

	@Override
	public ByteBuf setShort(int index, int value) {
		return unsupported();
	}

	@Override
	public ByteBuf setMedium(int index, int value) {
		return unsupported();
	}

	@Override
	public ByteBuf setInt(int index, int value) {
		return unsupported();
	}

	@Override
	public ByteBuf setLong(int index, long value) {
		return unsupported();
	}

	@Override
	public ByteBuf setChar(int index, int value) {
		return unsupported();
	}

	@Override
	public ByteBuf setFloat(int index, float value) {
		return unsupported();
	}

	@Override
	public ByteBuf setDouble(int index, double value) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuffer src) {
		return unsupported();
	}

	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		return unsupported();
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		return unsupported();
	}

	@Override
	public ByteBuf setZero(int index, int length) {
		return unsupported();
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
		checkAvail(2);
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
	public ByteBuf readBytes(int length) {
		checkAvail(length);
		int readable = buffer.readableBytes();
		try {
			return buffer.readBytes(length);
		} catch (ReplayableException e) {
			System.out.println("Error: readable = " + readable + " length = " + length);
			throw e;
		}
	}

	@Override
	public ByteBuf readSlice(int length) {
		checkAvail(length);
		return buffer.readSlice(length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst) {
		return unsupported();
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
		checkAvail(length);
		buffer.readBytes(dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf readBytes(byte[] dst) {
		checkAvail(dst.length);
		try {
			buffer.readBytes(dst);
		} catch (ReplayableException e) {
			throw e;
		}
		return this;
	}

	@Override
	public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
		checkAvail(length);
		buffer.readBytes(dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf readBytes(ByteBuffer dst) {
		return unsupported();
	}

	@Override
	public ByteBuf readBytes(OutputStream out, int length) throws IOException {
		return unsupported();
	}

	@Override
	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		return unsupported();
	}

	@Override
	public ByteBuf skipBytes(int length) {
		checkAvail(length);
		buffer.skipBytes(length);
		return this;
	}

	@Override
	public ByteBuf writeByte(int value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeShort(int value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeMedium(int value) {
		return unsupported();
	}

	@Override
	public int writeBytes(InputStream in, int length) throws IOException {
		return unsupported();
	}

	@Override
	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		return unsupported();
	}

	@Override
	public ByteBuf writeBoolean(boolean value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeInt(int value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeLong(long value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeChar(int value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeFloat(float value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeDouble(double value) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(byte[] src) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
		return unsupported();
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer src) {
		return unsupported();
	}

	@Override
	public ByteBuf writeZero(int length) {
		return unsupported();
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, byte value) {
		indexRangeCheck(toIndex);
		indexRangeCheck(fromIndex);
		return buffer.indexOf(fromIndex, toIndex, value);
	}

	@Override
	public int bytesBefore(byte value) {
		return unsupported();
	}

	@Override
	public int bytesBefore(int length, byte value) {
		return unsupported();
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		return unsupported();
	}

	@Override
	public ByteBuf copy() {
		return unsupported();
	}

	@Override
	public ByteBuf copy(int index, int length) {
		checkAvail(index, length);
		return buffer.copy(index, length);
	}

	@Override
	public ByteBuf slice() {
		return buffer.slice();
	}

	@Override
	public ByteBuf slice(int index, int length) {
		checkAvail(index, length);
		return buffer.slice(index, length);
	}

	@Override
	public ByteBuf duplicate() {
		return buffer.duplicate();
	}

	@Override
	public boolean hasArray() {
		return false;
	}

	@Override
	public byte[] array() {
		return unsupported();
	}

	@Override
	public int arrayOffset() {
		return unsupported();
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
	public int compareTo(ByteBuf buffer) {
		return unsupported();
	}

	private <R> R unsupported() {
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

	@Override
	public ByteBuf capacity(int newCapacity) {
		buffer.capacity(newCapacity);
		return this;
	}

	@Override
	public int maxCapacity() {
		return buffer.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return unsupported();
	}

	@Override
	public ByteBuf order(ByteOrder endianness) {
		return unsupported();
	}

	@Override
	public ByteBuf unwrap() {
		return unsupported();
	}

	@Override
	public int maxWritableBytes() {
		return unsupported();
	}

	@Override
	public boolean isReadable(int size) {
		return false;
	}

	@Override
	public boolean isWritable(int size) {
		return buffer.isWritable(size);
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		return unsupported();
	}

	@Override
	public ByteBuf ensureWritable(int minWritableBytes) {
		buffer.ensureWritable(minWritableBytes);
		return this;
	}

	@Override
	public int ensureWritable(int minWritableBytes, boolean force) {
		return buffer.ensureWritable(minWritableBytes, force);
	}

	@Override
	public boolean getBoolean(int index) {
		return unsupported();
	}

	@Override
	public ByteBuf setBoolean(int index, boolean value) {
		buffer.setBoolean(index, value);
		return this;
	}

	@Override
	public boolean readBoolean() {
		return unsupported();
	}


	@Override
	public int forEachByte(ByteBufProcessor processor) {
		return unsupported();
	}

	@Override
	public int forEachByte(int index, int length, ByteBufProcessor processor) {
		return unsupported();
	}

	@Override
	public int forEachByteDesc(ByteBufProcessor processor) {
		return unsupported();
	}

	@Override
	public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
		return unsupported();
	}

	@Override
	public int nioBufferCount() {
		return unsupported();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return unsupported();
	}

	@Override
	public ByteBuffer nioBuffer(int index, int length) {
		return unsupported();
	}

	@Override
	public ByteBuffer internalNioBuffer(int index, int length) {
		return unsupported();
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return unsupported();
	}

	@Override
	public ByteBuffer[] nioBuffers(int index, int length) {
		return unsupported();
	}

	@Override
	public boolean hasMemoryAddress() {
		return false;
	}

	@Override
	public long memoryAddress() {
		return unsupported();
	}

	@Override
	public int hashCode() {
		return 123 + buffer.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ReplayableByteBuf && buffer.equals(((ReplayableByteBuf) obj).buffer);
	}

	@Override
	public String toString() {
		return "Replayable" + buffer.toString();
	}

	@Override
	public ByteBuf retain(int increment) {
		buffer.retain(increment);
		return this;
	}

	@Override
	public ByteBuf retain() {
		buffer.retain();
		return this;
	}

	@Override
	public int refCnt() {
		return unsupported();
	}

	@Override
	public boolean release() {
		return unsupported();
	}

	@Override
	public boolean release(int decrement) {
		return unsupported();
	}
}
