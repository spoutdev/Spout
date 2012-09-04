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

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;

public abstract class GenericMessage<T extends Message> extends MessageCodec<T> implements Message {
	
	protected ChannelBuffer buffer;

	public GenericMessage(Class<T> clazz, int opcode) {
		super(clazz, opcode);
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * Gets the field root for this message.  This should be a static final unchanging array.
	 *
	 * @return
	 */
	public abstract CompoundMessageField getFieldRoot();
	
	public CompoundMessageField getToClientFieldRoot() {
		return getFieldRoot();
	}
	
	public CompoundMessageField getToServerFieldRoot() {
		return getFieldRoot();
	}
	
	/**
	 * Gets the field loop up table for the message
	 *
	 * @return
	 */
	public abstract int[] getFieldLoopup();
	
	@SuppressWarnings("unchecked")
	public <T> T get(FieldRef<T> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return (T) f.read(this.buffer);	
	}
	
	public long getLong(FieldRef<Long> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return f.readLong(this.buffer);
	}
	
	public int getInt(FieldRef<Integer> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return f.readInt(this.buffer);
	}
	
	public short getShort(FieldRef<Integer> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return f.readShort(this.buffer);
	}
	
	public byte getByte(FieldRef<Byte> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return f.readByte(this.buffer);
	}
	
	public short getUnsignedByte(FieldRef<Short> ref) {
		setupBuffer(ref);
		
		CompoundMessageField f = getFieldRoot();
		
		return f.readUnsignedByte(this.buffer);
	}
	
	private void setupBuffer(FieldRef<?> ref) {
		int index = ref.getIndex();
		this.buffer.readerIndex(getFieldLoopup()[index]);
	}
	
	@Override
	public ChannelBuffer encode(boolean upstream, T message) throws IOException {
		return this.buffer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T decode(boolean upstream, ChannelBuffer b) throws IOException {
		CompoundMessageField root = upstream ? getToClientFieldRoot() : getToServerFieldRoot();
		int start = b.readerIndex();
		int fieldCount = root.getSubFieldCount();
		int[] indexArray = new int[fieldCount];
		int length = root.skip(b, indexArray);
		this.buffer = ChannelBuffers.buffer(length);
		b.getBytes(start, this.buffer, 0, length);
		return (T) this;
	}

}
