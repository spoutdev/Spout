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
package org.spout.api.protocol.builder;

import io.netty.buffer.ByteBuf;

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
	public int getLength(ByteBuf buffer) {
		return length;
	}

	@Override
	public int getFixedLength() {
		return length;
	}

	@Override
	public int skip(ByteBuf buffer) {
		buffer.skipBytes(length);
		return length;
	}

	@Override
	public Object read(ByteBuf buffer) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot decode byte arrays");
	}

	@Override
	public void write(ByteBuf buffer, Object value) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot encode byte arrays");
	}

	@Override
	public void transfer(ByteBuf sourceBuffer, ByteBuf targetBuffer) {
		sourceBuffer.readBytes(targetBuffer, length);
	}
}
