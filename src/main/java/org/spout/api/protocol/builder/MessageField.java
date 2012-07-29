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

public interface MessageField {
	
	/**
	 * Gets a compressed version of this field.  The compressed version of a field 
	 * does not need to handle the read and write methods.
	 * 
	 * @return the compressed field, or null if none
	 */
	public MessageField getCompressed();
	
	/**
	 * Gets the fixed length of the field, or -1 for variable length fields
	 * 
	 * @return
	 */
	public int getFixedLength();
	
	/**
	 * Gets the length of the field without moving the read pointer
	 * 
	 * @return
	 */
	public int getLength(ChannelBuffer buffer);
	
	/**
	 * Gets the length of the field and moves the read pointer to the start of the next field
	 * 
	 * @param buffer
	 * @return the length of the field
	 */
	public abstract int skip(ChannelBuffer buffer);

	/**
	 * Reads a field from the buffer and moves the read pointer to the start of the next field
	 * 
	 * @param buffer
	 * @return
	 */
	public abstract Object read(ChannelBuffer buffer);
	
	/**
	 * Writes a field to the buffer
	 * 
	 * @param buffer
	 * @param value
	 */
	public abstract void write(ChannelBuffer buffer, Object value);
	
	/**
	 * Reads an object from the source buffer and writes it to the target buffer doing minimal decoding
	 * 
	 * @param sourceBuffer
	 */
	public abstract void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer);

}
