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

import static org.jboss.netty.channel.Channels.write;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public abstract class PostprocessEncoder extends OneToOneEncoder implements ProcessorHandler {
	
	private final AtomicReference<ChannelProcessor> processor = new AtomicReference<ChannelProcessor>();
	private final AtomicBoolean locked = new AtomicBoolean(false);

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
		
		if (locked.get()) {
			throw new IllegalStateException("Encode attempted when channel was locked");
		}
		
		ChannelProcessor processor = this.processor.get();
		if (processor == null || !(evt instanceof MessageEvent)) {
			super.handleDownstream(ctx, evt);
		} else {
			MessageEvent e = (MessageEvent) evt;
			Object originalMessage = e.getMessage();
			Object encodedMessage = encode(ctx, e.getChannel(), originalMessage);
			if (originalMessage == encodedMessage) {
				ctx.sendDownstream(evt);
			} else if (encodedMessage != null) {
				if (encodedMessage instanceof ChannelBuffer) {
					encodedMessage = processor.write(ctx, (ChannelBuffer)encodedMessage);
				}
				write(ctx, e.getFuture(), encodedMessage, e.getRemoteAddress());
			}
			if (originalMessage instanceof ProcessorSetupMessage) {
				ProcessorSetupMessage setupMessage = (ProcessorSetupMessage) originalMessage;
				ChannelProcessor newProcessor = setupMessage.getProcessor();
				if (newProcessor != null) {
					setProcessor(newProcessor);
				}
				if (setupMessage.isChannelLocking()) {
					locked.set(true);
				}
				setupMessage.setProcessorHandler(this);
			}
		}
    }
	
	public void setProcessor(ChannelProcessor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("Processor may not be set to null");
		} else if (!this.processor.compareAndSet(null, processor)){
			throw new IllegalArgumentException("Processor may only be set once");
		}
		locked.set(false);
	}
	
}
