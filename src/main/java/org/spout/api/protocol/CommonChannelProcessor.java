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
package org.spout.api.protocol;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * Bridge class for passing ChannelBuffers through byte array read/write processing<br>
 * <br>
 * The internal ChannelBuffer can be reused only if the amount output by the processor
 * for a single input is less than the capacity of the reused buffer.  If the data read 
 * from the processor due to a single write is larger than the capacity, then additional 
 * ChannelBuffers are generated to store the result.  These additional buffers are single 
 * usage.  The reuse channel buffer is always reused.<br>
 * <br>
 * Subclasses should extends the read and write methods.  It is guaranteed that the read
 * and write methods will not be called from more than 1 thread at the same time.
 */
public abstract class CommonChannelProcessor implements ChannelProcessor {
	
	private final static ChannelBuffer[] DUMMY_ARRAY = new ChannelBuffer[0];

	private ChannelBuffer reusedChannelBuffer;
	private final byte[] byteBuffer;
	protected final int capacity;
	
	public CommonChannelProcessor(int capacity) {
		this.capacity = capacity;
		this.byteBuffer = new byte[capacity];
	}
	
	@Override
	public final synchronized ChannelBuffer write(ChannelHandlerContext ctx, ChannelBuffer input) {
		if (reusedChannelBuffer == null) {
			reusedChannelBuffer = getNewBufferInstance(ctx, capacity);
		} else {
			reusedChannelBuffer.clear();
		}
		ChannelBuffer channelBuffer = reusedChannelBuffer;
		int nextSize = capacity;
		int remaining;
		ArrayList<ChannelBuffer> consumedBuffers = null;
		while ((remaining = input.readableBytes()) > 0) {
			int clamped = (remaining > byteBuffer.length) ? byteBuffer.length : remaining;
			input.readBytes(byteBuffer, 0, clamped);
			write(byteBuffer, clamped);
			int read;
			while ((read = read(byteBuffer)) > 0) {
				if (channelBuffer.writableBytes() >= read) {
					channelBuffer.writeBytes(byteBuffer, 0, read);
				} else if (channelBuffer.writableBytes() + channelBuffer.readerIndex() > read) {
					channelBuffer.discardReadBytes();
					channelBuffer.writeBytes(byteBuffer, 0, read);
				} else {
					ChannelBuffer newBuffer = getNewBufferInstance(ctx, nextSize);
					nextSize *= 2;
					if (consumedBuffers == null) {
						consumedBuffers = new ArrayList<ChannelBuffer>(16);
					}
					consumedBuffers.add(channelBuffer);
					channelBuffer = newBuffer;
					channelBuffer.writeBytes(byteBuffer, 0, read);					
				}
				
			}
		}
		input.discardReadBytes();
		if (consumedBuffers != null) {
			consumedBuffers.add(channelBuffer);
			return ChannelBuffers.wrappedBuffer(consumedBuffers.toArray(DUMMY_ARRAY));
		} else {
			return channelBuffer;
		}
	}
	
	/**
	 * Writes data to the processor<br>
	 * <br>
	 * This method does not need to be thread safe
	 * 
	 * @param buf a buffer containing the data
	 * @param length the length of the data to write
	 */
	protected abstract void write(byte[] buf, int length);
	
	/**
	 * Reads the data from the processor into the given array<br>
	 * <br>
	 * This method does not need to be thread safe
	 * 
	 * @param buf the byte array to write the data to
	 * @return the number of bytes written
	 */
	protected abstract int read(byte[] buf);
	
	
	private ChannelBuffer getNewBufferInstance(ChannelHandlerContext ctx, int capacity) {
        return ctx.getChannel().getConfig().getBufferFactory().getBuffer(capacity);
	}

}
