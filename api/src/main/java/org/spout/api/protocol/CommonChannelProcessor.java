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
package org.spout.api.protocol;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * Bridge class for passing ChannelBuffers through byte array read/write processing
 */
public abstract class CommonChannelProcessor implements ChannelProcessor {
	private final static ChannelBuffer[] DUMMY_ARRAY = new ChannelBuffer[0];
	private final byte[] byteBuffer;
	protected final int capacity;

	public CommonChannelProcessor(int capacity) {
		this.capacity = capacity;
		this.byteBuffer = new byte[capacity];
	}

	@Override
	public final ChannelBuffer write(ChannelHandlerContext ctx, ChannelBuffer input) {
		return write(ctx, input, null);
	}

	@Override
	public final synchronized ChannelBuffer write(ChannelHandlerContext ctx, ChannelBuffer input, ChannelBuffer buffer) {
		ChannelBuffer channelBuffer = buffer == null ? getNewBufferInstance(ctx, capacity) : buffer;
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
		if (consumedBuffers == null) {
			return channelBuffer;
		}

		consumedBuffers.add(channelBuffer);
		return ChannelBuffers.wrappedBuffer(consumedBuffers.toArray(DUMMY_ARRAY));
	}

	/**
	 * Writes data to the processor<br> <br> This method does not need to be thread safe
	 *
	 * @param buf a buffer containing the data
	 * @param length the length of the data to write
	 */
	protected abstract void write(byte[] buf, int length);

	/**
	 * Reads the data from the processor into the given array<br> <br> This method does not need to be thread safe
	 *
	 * @param buf the byte array to write the data to
	 * @return the number of bytes written
	 */
	protected abstract int read(byte[] buf);

	private ChannelBuffer getNewBufferInstance(ChannelHandlerContext ctx, int capacity) {
		return ctx.getChannel().getConfig().getBufferFactory().getBuffer(capacity);
	}
}
