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

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

public class CompoundMessageField extends MessageFieldImpl {
	
	private final MessageField[] fields;
	private final MessageField[] fieldsCompressed;
	private final int fixedLength;
	
	public CompoundMessageField(MessageField[] fields) {
		this.fields = fields;
		this.fieldsCompressed = compressFields(fields);
		if (fieldsCompressed.length == 0) {
			fixedLength = 0;
		} else if (fieldsCompressed.length == 1) {
			fixedLength = fieldsCompressed[0].getFixedLength();
		} else {
			fixedLength = -1;
		}
	}
	
	@Override
	public int getFixedLength() {
		return fixedLength;
	}
	
	@Override
	public MessageField getCompressed() {
		if (fieldsCompressed.length == 1) {
			MessageField compressed = fieldsCompressed[0].getCompressed();
			if (compressed != null) {
				return compressed;
			} else {
				return fieldsCompressed[0];
			}
		} else {
			return null;
		}
	}

	@Override
	public int skip(ChannelBuffer buffer) {
		int length = 0;
		for (int i = 0; i < fieldsCompressed.length; i++) {
			length += fieldsCompressed[i].skip(buffer);
		}
		return length;
	}
	
	public int skip(ChannelBuffer buffer, int[] indexArray) {
		int length = 0;
		int j = 0;
		for (int i = 0; i < fields.length; i++) {
			indexArray[j++] = length;
			length += fields[i].skip(buffer);
		}
		return length;
	}
	
	@Override
	public int getLength(ChannelBuffer buffer) {
		int startPosition = buffer.readerIndex();
		int length = skip(buffer);
		buffer.readerIndex(startPosition);
		return length;
	}

	@Override
	public Object[] read(ChannelBuffer buffer) {
		Object[] array = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			array[i] = fields[i].read(buffer);
		}
		return array;
	}

	@Override
	public void write(ChannelBuffer buffer, Object value) {
		Object[] array = (Object[]) value;
		if (array.length != fields.length) {
			throw new IllegalArgumentException("Number of elements in the value array does not match the number of fields");
		}
		for (int i = 0; i < fields.length; i++) {
			fields[i].write(buffer, array[i]);
		}
	}

	@Override
	public void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer) {
		getLength(sourceBuffer);
		for (int i = 0; i < fieldsCompressed.length; i++) {
			fields[i].transfer(sourceBuffer, targetBuffer);
		}
	}
	
	public int getSubFieldCount() {
		return fields.length;
	}
	
	public static MessageField[] compressFields(MessageField[] fields) {
		List<MessageField> compressedArray = new ArrayList<MessageField>();
		int i = 0;
		while (i < fields.length) {
			int fixedLength = 0;
			while (i < fields.length && fields[i].getFixedLength() >= 0) {
				fixedLength += fields[i].getFixedLength();
				i++;
			}
			if (fixedLength > 0) {
				compressedArray.add(new FixedMessageField(fixedLength));
			}
			if (i < fields.length) {
				MessageField compressed = fields[i].getCompressed();
				if (compressed != null) {
					compressedArray.add(compressed);
				} else {
					compressedArray.add(fields[i]);
				}
				i++;
			}
		}
		return compressedArray.toArray(new MessageField[0]);
	}

}
