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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a layer of processing after encode but before the message is passed outbound.
 *
 */
public abstract class ProcessingEncoder extends MessageToMessageEncoder<Object> implements ProcessorHandler {
	private final AtomicReference<ChannelProcessor> processor = new AtomicReference<>();
	private final AtomicBoolean locked = new AtomicBoolean(false);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (locked.get()) {
			throw new IllegalStateException("Encode attempted when channel was locked");
		}
		super.write(ctx, msg, promise);
	}

	private void checkForSetupMessage(Object e) {
		if (e instanceof ProcessorSetupMessage) {
			ProcessorSetupMessage setupMessage = (ProcessorSetupMessage) e;
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

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		Object encoded = encodePreProcess(ctx, msg);
		if (encoded == null) {
			// MessageToMessageEncoder will throw an error because there was no encoded message.
			return;
		}
		final ChannelProcessor processor = this.processor.get();
		if (processor != null && encoded instanceof ByteBuf) {
			synchronized (this) {
				encoded = processor.write(ctx, (ByteBuf) encoded);
			}
		}
		checkForSetupMessage(msg);
		out.add(encoded);
	}

	protected abstract Object encodePreProcess(ChannelHandlerContext ctx, Object msg) throws Exception;
}
