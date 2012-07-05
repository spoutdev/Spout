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
package org.spout.api.protocol.builtin.codec;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.message.WorldChangeMessage;

public class WorldChangeCodec extends MessageCodec<WorldChangeMessage> {
	public WorldChangeCodec() {
		super(WorldChangeMessage.class, 0x02, true);
	}

	@Override
	public ChannelBuffer encode(WorldChangeMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(8 + 4 + message.getCompressedData().length);
		buffer.writeLong(message.getWorldUUID().getLeastSignificantBits());
		buffer.writeLong(message.getWorldUUID().getMostSignificantBits());
		buffer.writeInt(message.getCompressedData().length);
		buffer.writeBytes(message.getCompressedData());
		return buffer;
	}

	@Override
	public WorldChangeMessage decode(ChannelBuffer buffer) {
		final long uuidLSB = buffer.readLong();
		final long uuidMSB = buffer.readLong();
		final byte[] compressedData = new byte[buffer.readInt()];
		buffer.readBytes(compressedData);
		return new WorldChangeMessage(new UUID(uuidLSB, uuidMSB), compressedData);
	}
}
