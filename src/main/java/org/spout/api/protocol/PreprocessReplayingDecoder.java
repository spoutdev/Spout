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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.spout.api.protocol.replayable.ReplayableChannelBuffer;
import org.spout.api.protocol.replayable.ReplayableError;

public abstract class PreprocessReplayingDecoder extends FrameDecoder implements ProcessorHandler {

	private final int capacity;
	private final AtomicReference<ChannelProcessor> processor = new AtomicReference<ChannelProcessor>();
	private final AtomicBoolean locked = new AtomicBoolean(false);

	private final ReplayableChannelBuffer replayableBuffer = new ReplayableChannelBuffer();

	private ChannelBuffer processedBuffer = null;
	private List<Object> frames = new LinkedList<Object>();
	
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
		
		if (locked.get()) {
			throw new IllegalStateException("Decode attempted when channel was locked");
		}
		
		frames.clear();
		Object lastFrame = null;
		Object newFrame = null;
		
		ChannelProcessor processor = this.processor.get();

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
				if (newFrame instanceof ProcessorSetupMessage) {
					ProcessorSetupMessage setupMessage = (ProcessorSetupMessage) newFrame;
					ChannelProcessor newProcessor = setupMessage.getProcessor();
					if (newProcessor != null) {
						setProcessor(newProcessor);
					}
					if (setupMessage.isChannelLocking()) {
						locked.set(true);
					}
					setupMessage.setProcessorHandler(this);
				}
				processor = this.processor.get();
			}
		} while (newFrame != null && !locked.get());

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

	@Override
	public void setProcessor(ChannelProcessor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("Processor may not be set to null");
		} else if (!this.processor.compareAndSet(null, processor)){
			throw new IllegalArgumentException("Processor may only be set once");
		}
		locked.set(false);
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
	protected abstract Object decodeProcessed(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception;

	private static ChannelBuffer getNewBuffer(ChannelHandlerContext ctx, int capacity) {
		return ctx.getChannel().getConfig().getBufferFactory().getBuffer(capacity);
	}

}
