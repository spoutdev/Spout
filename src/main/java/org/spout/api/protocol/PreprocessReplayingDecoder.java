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

import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.spout.api.protocol.replayable.ReplayableChannelBuffer;
import org.spout.api.protocol.replayable.ReplayableError;

public abstract class PreprocessReplayingDecoder extends FrameDecoder {

	private final int capacity;
	private ChannelProcessor processor = null;

	private final ReplayableChannelBuffer replayableBuffer = new ReplayableChannelBuffer();

	private ChannelBuffer processedBuffer = null;
	private List<Object> frames = new LinkedList<Object>();
	
	int total = 0;
	int processedOut = 0;
	
	/**
	 * Constructs a new replaying decoder.<br>
	 * <br>
	 * The internal buffer is dynamically sized, but if it grows larger than 
	 * the given capacity, it will be resized downwards when possible.  This allows
	 * handling of larger packets without requiring the buffers to be set larger than
	 * the size of the largest packet.
	 * 
	 * @param capacity the default capacity of the internal buffer.
	 */
	public PreprocessReplayingDecoder(int capacity) {
		super(true);
		this.capacity = capacity;
	}

	@Override
	protected final Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf) throws Exception {

		if (!buf.readable()) {
			throw new IllegalStateException("Empty buffer sent to decode()");
		}
		
		frames.clear();
		Object lastFrame = null;
		Object newFrame = null;

		ChannelBuffer liveBuffer;
		do {
			if (processor == null) {
				liveBuffer = buf;
			} else {
				if (processedBuffer == null) {
					processedBuffer = processor.write(ctx, buf);
				} else if (buf.readable()) {
					processedBuffer = processor.write(ctx, buf, processedBuffer);
				}
				liveBuffer = processedBuffer;
			}
			int readPointer = liveBuffer.readerIndex();
			try {
				newFrame = decodeProcessed(ctx, c, replayableBuffer.setBuffer(liveBuffer));
				if (newFrame != null) {
					int outputLength = ((byte[])newFrame).length;
					processedOut += outputLength;
				}
			} catch (ReplayableError e) {
				// roll back liveBuffer read to state prior to calling decodeProcessed
				liveBuffer.readerIndex(readPointer);
				// No frame returned
				newFrame = null;
			}

			if (newFrame != null) {
				if (lastFrame == null) {
					lastFrame = newFrame;
				} else {
					frames.add(lastFrame);
					lastFrame = newFrame;
				}
			}
		} while (newFrame != null);

		if (processedBuffer != null) {
			if (processedBuffer instanceof CompositeChannelBuffer || (processedBuffer.capacity() > capacity && processedBuffer.writable())) {
				ChannelBuffer newBuffer = getNewBuffer(ctx, Math.max(capacity, processedBuffer.readableBytes()));
				if (processedBuffer.readable()) {
					// This method transfers the data in processedBuffer to the newBuffer.
					// However, for some reason, if processedBuffer is zero length, it causes an exception.
					newBuffer.writeBytes(processedBuffer);
				}
				processedBuffer = newBuffer;
			}
			processedBuffer.discardReadBytes();
		}

		if (frames.size() > 0) {
			frames.add(lastFrame);
			return frames;
		} else {
			return lastFrame;
		}
	}

	/**
	 * Sets the processor to be used to preprocess the packets<br>
	 * <br>
	 * This method must be called just before returning from the decodeProcessed method
	 * @param processor
	 */
	public void setProcessor(ChannelProcessor processor) {
		if (this.processor != null) {
			throw new IllegalArgumentException("Processor may only be set once");
		} else if (processor == null) {
			throw new IllegalArgumentException("Processor may not be set to null");
		} else {
			this.processor = processor;
		}
	}

	/**
	 * This method is the equivalent of the decode method for the standard ReplayingDecoder<br>
	 * The method call is repeated if decoding causes the ChannelBuffer to run out of bytes<br>
	 * 
	 * @param ctx the channel handler context
	 * @param channel the channel
	 * @param buffer the channel buffer
	 * @return the message to pass to the next stage
	 * @throws Exception
	 */
	public abstract Object decodeProcessed(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception;

	private static ChannelBuffer getNewBuffer(ChannelHandlerContext ctx, int capacity) {
		return ctx.getChannel().getConfig().getBufferFactory().getBuffer(capacity);
	}

}
