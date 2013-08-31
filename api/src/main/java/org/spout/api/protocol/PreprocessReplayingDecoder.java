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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.spout.api.protocol.replayable.ReplayableByteBuf;
import org.spout.api.protocol.replayable.ReplayableException;

/**
 * This class is both a {@link ByteToMessageDecoder} but also allows processing pre-decode via {@code decodeProcessed}.
 *
 */
public abstract class PreprocessReplayingDecoder extends ByteToMessageDecoder implements ProcessorHandler {
	private final AtomicReference<ChannelProcessor> processor = new AtomicReference<>(null);
	private final ReplayableByteBuf replayableBuffer = new ReplayableByteBuf();
	private final AtomicBoolean locked = new AtomicBoolean(false);
	private final int capacity;
	private ByteBuf processedBuffer = null;

	/**
	 * Constructs a new replaying decoder.<br> <br> The internal buffer is dynamically sized, but if it grows larger than the given capacity, it will be resized downwards when possible.  This allows
	 * handling of larger packets without requiring the buffers to be set larger than the size of the largest packet.
	 *
	 * @param capacity the default capacity of the internal buffer.
	 */
	public PreprocessReplayingDecoder(int capacity) {
		this.capacity = capacity;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> frames) throws Exception {
		Channel c = ctx.channel();

		if (!buf.isReadable()) {
			throw new IllegalStateException("Empty buffer sent to decode()");
		}

		if (locked.get()) {
			throw new IllegalStateException("Decode attempted when channel was locked");
		}

		Object lastFrame = null;
		Object newFrame = null;

		ChannelProcessor processor = this.processor.get();
		ByteBuf liveBuffer;
		do {
			if (processor == null) {
				liveBuffer = buf;
			} else {
				if (processedBuffer == null) {
					processedBuffer = processor.write(ctx, buf);
				} else if (buf.isReadable()) {
					processedBuffer = processor.write(ctx, buf, processedBuffer);
				}
				liveBuffer = processedBuffer;
			}
			int readPointer = liveBuffer.readerIndex();
			try {
				newFrame = decodeProcessed(ctx, c, replayableBuffer.setBuffer(liveBuffer));
			} catch (ReplayableException e) {
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
					} else {
						locked.set(false);
					}
					setupMessage.setProcessorHandler(this);
				}
				processor = this.processor.get();
			}
		} while (newFrame != null && !locked.get());

		if (processedBuffer != null) {
			if (processedBuffer instanceof CompositeByteBuf || (processedBuffer.capacity() > capacity && processedBuffer.isWritable())) {
				ByteBuf newBuffer = getNewBuffer(ctx, Math.max(capacity, processedBuffer.readableBytes()));
				if (processedBuffer.isReadable()) {
					// This method transfers the data in processedBuffer to the newBuffer.
					// However, for some reason, if processedBuffer is zero length, it causes an exception.
					newBuffer.writeBytes(processedBuffer);
				}
				ByteBuf old = processedBuffer;
				processedBuffer = newBuffer;
				old.release();
			}
			processedBuffer.discardReadBytes();
		}

		if (lastFrame != null) {
			frames.add(lastFrame);
		}
	}

	@Override
	public void setProcessor(ChannelProcessor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("Processor may not be set to null");
		} else if (!this.processor.compareAndSet(null, processor)) {
			throw new IllegalArgumentException("Processor may only be set once");
		}
		locked.set(false);
	}

	/**
	 * This method is the equivalent of the decode method for the standard ReplayingDecoder<br> The method call is repeated if decoding causes the ByteBuf to run out of bytes<br>
	 *
	 * @param ctx the channel handler context
	 * @param channel the channel
	 * @param buffer the channel buffer
	 * @return the message to pass to the next stage
	 */
	protected abstract Object decodeProcessed(ChannelHandlerContext ctx, Channel channel, ByteBuf buffer) throws Exception;

	private static ByteBuf getNewBuffer(ChannelHandlerContext ctx, int capacity) {
		return ctx.channel().config().getAllocator().buffer(capacity);
	}
}
