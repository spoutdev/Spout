/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.protocol.bootstrap.codec;

import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.bootstrap.msg.BootstrapPingMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class BootstrapPingCodec extends MessageCodec<BootstrapPingMessage> {
	public BootstrapPingCodec() {
		super(BootstrapPingMessage.class, 0xFE);
	}

	@Override
	public BootstrapPingMessage decode(ChannelBuffer buffer) {
		int id = buffer.readInt();
		return new BootstrapPingMessage(id);
	}

	@Override
	public ChannelBuffer encode(BootstrapPingMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(5);
		buffer.writeInt(message.getPingId());
		return buffer;
	}
}
