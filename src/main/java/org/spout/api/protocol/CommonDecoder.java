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

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.spout.api.Spout;
import org.spout.api.exception.UnknownPacketException;

/**
 * A {@link ReplayingDecoder} which decodes {@link ChannelBuffer}s into
 * Common {@link org.spout.api.protocol.Message}s.
 */
public class CommonDecoder extends PreprocessReplayingDecoder {
	private final int previousMask = 0x1F;
	private int[] previousOpcodes = new int[previousMask + 1];
	private int opcodeCounter = 0;
	private volatile Protocol protocol;
	private final boolean upstream;

	public CommonDecoder(boolean upstream) {
		super(512);
		this.upstream = upstream;
	}

	@Override
	protected Object decodeProcessed(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf) throws Exception {
		if (protocol == null) {
			protocol = Spout.getEngine().getProtocol(c.getLocalAddress());
		}

		MessageCodec<?> codec;
		try {
			codec = protocol.readHeader(buf);
		} catch (UnknownPacketException e) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < previousMask; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(Integer.toHexString(previousOpcodes[(opcodeCounter + i) & previousMask]));
			}
			throw new IOException("Unknown operation code: " + e.getOpcode() + " (previous opcodes: " + sb.toString() + ").");
		}

		if (codec == null) {
			return buf;
		}

		previousOpcodes[(opcodeCounter++) & previousMask] = codec.getOpcode();
		return codec.decode(upstream, buf);
	}

	void setProtocol(Protocol proto) {
		this.protocol = proto;
	}
}
