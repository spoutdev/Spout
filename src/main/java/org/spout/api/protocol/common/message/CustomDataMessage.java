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
package org.spout.api.protocol.common.message;

import java.io.IOError;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.dynamicid.DynamicWrapperMessage;
import org.spout.api.util.Named;
import org.spout.api.util.SpoutToStringStyle;

public class CustomDataMessage extends Message implements DynamicWrapperMessage {
	private final byte[] data;
	private final String type;

	public CustomDataMessage(String type, byte[] data) {
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("data", data)
				.append("type", type)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(61, 33)
				.append(data)
				.append(type)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		final CustomDataMessage other = (CustomDataMessage) obj;
		return new EqualsBuilder()
				.append(this.data, other.data)
				.append(this.type, other.type)
				.isEquals();
	}

	public Message unwrap(boolean upstream, Protocol activeProtocol) throws IOException {
		ChannelBuffer dataBuf = ChannelBuffers.wrappedBuffer(getData());
		MessageCodec<?> codec = null;
		for (Pair<Integer, String> item : activeProtocol.getDynamicallyRegisteredPackets()) {
			 codec = activeProtocol.getCodecLookupService().find(item.getLeft());
			if (codec instanceof Named && ((Named) codec).getName().equalsIgnoreCase(getType())) {
				break;
			} else if (getType().equalsIgnoreCase("Spout-" + codec.getOpcode())) {
				break;
			}
		}
		if (codec != null) {
			return codec.decode(upstream, dataBuf);
		} else {
			return null;
		}
	}
}
