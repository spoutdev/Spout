/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
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
package org.spout.api.protocol.common.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.common.CommonBootstrapProtocol;
import org.spout.api.protocol.common.message.CustomDataMessage;

public class CustomDataCodec extends MessageCodec<CustomDataMessage> {
	public CustomDataCodec() {
		super(CustomDataMessage.class, 0xFA);
	}

	@Override
	public CustomDataMessage decode(ChannelBuffer buffer) throws IOException {
		String type = CommonBootstrapProtocol.readString(buffer, 16);
		int length = buffer.readUnsignedShort();
		byte[] data = new byte[length];
		buffer.readBytes(data);
		return new CustomDataMessage(type, data);
	}

	@Override
	public ChannelBuffer encode(CustomDataMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		CommonBootstrapProtocol.writeString(buffer, message.getType());
		buffer.writeShort(message.getData().length);
		buffer.writeBytes(message.getData());
		return buffer;
	}
}