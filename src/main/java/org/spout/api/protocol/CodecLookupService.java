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
package org.spout.api.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.util.StringMap;

/**
 * A class used to lookup message codecs.
 *
 */
public abstract class CodecLookupService {
	protected final MessageCodec<?>[] opcodeTable = new MessageCodec<?>[65536];

	private int nextId = 0;

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
	protected <T extends Message, C extends MessageCodec<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		bind(clazz, null);
	}

	/**
	 * Binds a codec by adding entries for it to the tables.
	 *
	 * @param clazz The codec's class.
	 * @param dynamicPacketMap The StringMap used to register dynamically allocated packet ids in
	 * @param <T> The type of message.
	 * @param <C> The type of codec.
	 * @throws InstantiationException if the codec could not be instantiated.
	 * @throws IllegalAccessException if the codec could not be instantiated due
	 *             to an access violation.
	 */
	protected <T extends Message, C extends MessageCodec<T>> C bind(Class<C> clazz, StringMap dynamicPacketMap) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		boolean dynamicId = false;
		Constructor<C> constructor;
		try {
			constructor = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			if (dynamicPacketMap != null) {
				try {
					constructor = clazz.getConstructor(int.class);
					dynamicId = true;
				} catch (NoSuchMethodException e1) {
					throw (InstantiationException) new InstantiationException().initCause(e1);
				}
			} else {
				throw new InstantiationException("Packet is dynamic and no dynamic constructor string map was provided!");
			}
		}

		C codec;
		if (dynamicId) {
			int id;
			if (dynamicPacketMap.getKeys().contains(clazz.getName())) {
				id = dynamicPacketMap.register(clazz.getName());
			} else {
				id = getNextId();
				dynamicPacketMap.register(clazz.getName(), id);
			}
			codec = constructor.newInstance(id);
			codec.setDynamic(true);
		} else {
			codec = constructor.newInstance();
			nextId = nextId > codec.getOpcode() ? nextId : codec.getOpcode() + 1;
		}

		opcodeTable[codec.getOpcode()] = codec;
		classTable.put(codec.getType(), codec);
		return codec;
	}

	private int getNextId() {
		while (opcodeTable[nextId] != null) {
			nextId++;
		}
		return nextId;
	}

	public MessageCodec<?> find(int opcode) {
		if (opcode > -1 && opcode < opcodeTable.length) {
			return opcodeTable[opcode];
		}
		return null;
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

	public Collection<MessageCodec<?>> getCodecs() {
		return Collections.unmodifiableCollection(classTable.values());
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	protected CodecLookupService() {
	}
}
