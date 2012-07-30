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
package org.spout.api.protocol.builder;

import org.jboss.netty.buffer.ChannelBuffer;


public class FixedMessageField extends MessageFieldImpl {
	
	protected final int length;
	
	protected FixedMessageField(int length) {
		this.length = length;
	}
	
	@Override
	public MessageField getCompressed() {
		return null;
	}
	
	@Override
	public int getLength(ChannelBuffer buffer) {
		return length;
	}
	
	@Override
	public int getFixedLength() {
		return length;
	}

	@Override
	public int skip(ChannelBuffer buffer) {
		buffer.skipBytes(length);
		return length;
	}

	@Override
	public Object read(ChannelBuffer buffer) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot decode byte arrays");
	}

	@Override
	public void write(ChannelBuffer buffer, Object value) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot encode byte arrays");		
	}

	@Override
	public void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer) {
		sourceBuffer.readBytes(targetBuffer, length);
	}

}
