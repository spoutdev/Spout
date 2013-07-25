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

import org.jboss.netty.buffer.ChannelBuffer;

public interface MessageField {
	/**
	 * Gets a compressed version of this field.  The compressed version of a field does not need to handle the read and write methods.
	 *
	 * @return the compressed field, or null if none
	 */
	public MessageField getCompressed();

	/**
	 * Gets the fixed length of the field, or -1 for variable length fields
	 */
	public int getFixedLength();

	/**
	 * Gets the length of the field without moving the read pointer
	 */
	public int getLength(ChannelBuffer buffer);

	/**
	 * Gets the length of the field and moves the read pointer to the start of the next field. This method will throw an out of bounds exception if the entire field is not present
	 *
	 * @return the length of the field
	 */
	public abstract int skip(ChannelBuffer buffer);

	/**
	 * Reads a field from the buffer and moves the read pointer to the start of the next field
	 */
	public abstract Object read(ChannelBuffer buffer);

	/**
	 * Reads a long field from the buffer and moves the read pointer to the start of the next field
	 */
	public long readLong(ChannelBuffer buffer);

	/**
	 * Reads am int field from the buffer and moves the read pointer to the start of the next field
	 */
	public int readInt(ChannelBuffer buffer);

	/**
	 * Reads a short field from the buffer and moves the read pointer to the start of the next field
	 */
	public short readShort(ChannelBuffer buffer);

	/**
	 * Reads a byte field from the buffer and moves the read pointer to the start of the next field
	 */
	public byte readByte(ChannelBuffer buffer);

	/**
	 * Reads am unsigned byte field from the buffer and moves the read pointer to the start of the next field
	 */
	public short readUnsignedByte(ChannelBuffer buffer);

	/**
	 * Writes a field to the buffer
	 */
	public abstract void write(ChannelBuffer buffer, Object value);

	/**
	 * Writes a long field to the buffer
	 */
	public abstract void writeLong(ChannelBuffer buffer, long value);

	/**
	 * Writes an int field to the buffer
	 */
	public abstract void writeInt(ChannelBuffer buffer, int value);

	/**
	 * Writes a short field to the buffer
	 */
	public abstract void writeShort(ChannelBuffer buffer, short value);

	/**
	 * Writes a byte field to the buffer
	 */
	public abstract void writeByte(ChannelBuffer buffer, byte value);

	/**
	 * Writes an unsigned byte field to the buffer
	 */
	public abstract void writeUnsignedByte(ChannelBuffer buffer, short value);

	/**
	 * Reads an object from the source buffer and writes it to the target buffer doing minimal decoding
	 */
	public abstract void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer);
}
