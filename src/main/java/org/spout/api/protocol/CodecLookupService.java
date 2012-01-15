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
package org.spout.api.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * A class used to lookup message codecs.
 *
 * @author Graham Edgecombe
 */
public abstract class CodecLookupService {
	/**
	 * A table which maps opcodes to codecs. This is generally used to map
	 * incoming packets to a codec.
	 */
	protected final MessageCodec<?>[] opcodeTable = new MessageCodec<?>[256];

	protected final MessageCodec<?>[] expandedOpcodeTable = new MessageCodec<?>[65536];

	/**
	 * A table which maps messages to codecs. This is generally used to map
	 * outgoing packets to a codec.
	 */
	protected final Map<Class<? extends Message>, MessageCodec<?>> classTable = new HashMap<Class<? extends Message>, MessageCodec<?>>();

	/**
	 * Binds a codec by adding entries for it to the tables.
	 *
	 * @param clazz The codec's class.
	 * @param <T> The type of message.
	 * @param <C> The type of codec.
	 * @throws InstantiationException if the codec could not be instantiated.
	 * @throws IllegalAccessException if the codec could not be instantiated due
	 *             to an access violation.
	 */
	protected <T extends Message, C extends MessageCodec<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException {
		MessageCodec<T> codec = clazz.newInstance();

		if (codec.isExpanded()) {
			expandedOpcodeTable[codec.getOpcode()] = codec;
		} else {
			opcodeTable[codec.getOpcode()] = codec;
		}
		classTable.put(codec.getType(), codec);
	}
	
	/**
	 * Finds a codec by short opcode.
	 *
	 * @param opcode The opcode.
	 * @return The codec, or {@code null} if it could not be found.
	 */
	public MessageCodec<?> find(int opcode) {
		if (opcode < 0 || opcode >= opcodeTable.length) {
			return null;
		} else {
			return opcodeTable[opcode];
		}
	}
	
	public MessageCodec<?> findExpanded(int opcode) {
		MessageCodec<?> codec = find(opcode >> 8);

		if (codec != null) {
			return codec;
		} else {
			return expandedOpcodeTable[opcode];
		}
	}

	/**
	 * Finds a codec by message class.
	 *
	 * @param clazz The message class.
	 * @param <T> The type of message.
	 * @return The codec, or {@code null} if it could not be found.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> MessageCodec<T> find(Class<T> clazz) {
		return (MessageCodec<T>) classTable.get(clazz);
	}

	/**
	 * Default private constructor to prevent insantiation.
	 */
	protected CodecLookupService() {
	}
}
