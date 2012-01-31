/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
import org.spout.api.Commons;
import org.spout.api.Spout;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

/**
 * A {@link ReplayingDecoder} which decodes {@link ChannelBuffer}s into
 * Common {@link org.spout.api.protocol.Message}s.
 */
public class CommonDecoder extends ReplayingDecoder<VoidEnum> {
	private volatile CodecLookupService codecLookup = null;
	private int previousOpcode = -1;
	private volatile BootstrapProtocol bootstrapProtocol;
	private final CommonHandler handler;
	private final CommonEncoder encoder;

	public CommonDecoder(CommonHandler handler, CommonEncoder encoder) {
		this.encoder = encoder;
		this.handler = handler;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
		if (codecLookup == null) {
			System.out.println("Setting codec lookup service");
			bootstrapProtocol = Spout.getGame().getBootstrapProtocol(c.getLocalAddress());
			System.out.println("Bootstrap protocol is: " + bootstrapProtocol);
			codecLookup = bootstrapProtocol.getCodecLookupService();
			System.out.println("Codec lookup service is: " + codecLookup);
		}

		int opcode;
		
		try {
			opcode = buf.getUnsignedShort(buf.readerIndex());
		}
		catch (Error e) {
			opcode = buf.getUnsignedByte(buf.readerIndex()) << 8;
		}

		MessageCodec<?> codec = codecLookup.find(opcode);
		if (codec == null) {
			throw new IOException("Unknown operation code: " + opcode + " (previous opcode: " + previousOpcode + ").");
		}

		if (codec.isExpanded()) {
			buf.readShort();
		} else {
			buf.readByte();
		}

		previousOpcode = opcode;

		Message message = codec.decode(buf);

		if (bootstrapProtocol != null && Commons.isSpout) {
			//TODO: Why is this never printed??????
			System.out.println("Checking for protocol definition");
			long id = bootstrapProtocol.detectProtocolDefinition(message);
			if (id != -1L) {
				Protocol protocol = Protocol.getProtocol(id);

				if (protocol != null) {
					codecLookup = protocol.getCodecLookupService();
					encoder.setProtocol(protocol);
					handler.setProtocol(protocol);
					bootstrapProtocol = null;
				} else {
					throw new IllegalStateException("No protocol associated with an id of " + id);
				}
			}
		}

		return message;
	}
}
