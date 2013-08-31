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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import org.spout.api.Spout;
import org.spout.api.protocol.dynamicid.DynamicMessageDecoder;
import org.spout.api.protocol.dynamicid.DynamicMessageEncoder;

/**
 * A common {@link ChannelPipelineFactory}
 */
public final class CommonChannelInitializer extends ChannelInitializer<SocketChannel> {
	/**
	 * Indicates if the channel is an upstream channel
	 */
	private final boolean onClient;

	/**
	 * Creates a new Common pipeline factory.
	 *
	 * @param engine The engine
	 */
	public CommonChannelInitializer() {
		switch (Spout.getPlatform()) {
			case CLIENT:
				this.onClient = true;
				break;
			case PROXY:
			case SERVER:
				this.onClient = false;
				break;
			default:
				throw new IllegalStateException("Unknown platform!");
		}
	}

	@Override
	protected void initChannel(SocketChannel c) throws Exception {
		// Up for encoding/sending/outbound; Down for decoding/receiving/inbound
		CommonEncoder encoder = new CommonEncoder(onClient);
		CommonDecoder decoder = new CommonDecoder(onClient);
		DynamicMessageDecoder dynamicDecoder = new DynamicMessageDecoder();
		DynamicMessageEncoder dynamicEncoder = new DynamicMessageEncoder();
		CommonHandler handler = new CommonHandler(encoder, decoder);

		c.pipeline().addLast(decoder, encoder, dynamicDecoder, dynamicEncoder, handler);
	}
}
