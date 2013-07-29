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
package org.spout.api.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.spout.api.Spout;
import org.spout.api.event.object.EventableListener;
import org.spout.api.util.SyncedMapEvent;
import org.spout.api.util.SyncedStringMap;

/**
 * A class used to lookup message codecs.
 */
public class CodecLookupService implements EventableListener<SyncedMapEvent> {
	protected final MessageCodec<?>[] opcodeTable;
	private final ClassLoader loader;
	private final SyncedStringMap dynamicPacketMap;

	protected CodecLookupService(ClassLoader loader, SyncedStringMap dynamicPacketMap, int size) {
		this.dynamicPacketMap = dynamicPacketMap;
		this.loader = loader;
		dynamicPacketMap.registerListener(this);
		opcodeTable = new MessageCodec<?>[size];
	}

	private int nextId = 0;
	/**
	 * A table which maps messages to codecs. This is generally used to map outgoing packets to a codec.
	 */
	protected final Map<Class<? extends Message>, MessageCodec<?>> classTable = new ConcurrentHashMap<>();

	/**
	 * Binds a codec by adding entries for it to the tables.
	 *
	 * @param clazz The codec's class.
	 * @param <T> The type of message.
	 * @param <C> The type of codec.
	 * @throws InstantiationException if the codec could not be instantiated.
	 * @throws IllegalAccessException if the codec could not be instantiated due to an access violation.
	 */
	protected <T extends Message, C extends MessageCodec<T>> C bind(Class<C> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		boolean dynamicId = false;
		Constructor<C> constructor;
		try {
			constructor = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			try {
				constructor = clazz.getConstructor(int.class);
				dynamicId = true;
			} catch (NoSuchMethodException e1) {
				throw (InstantiationException) new InstantiationException().initCause(e1);
			}
		}

		C codec;
		if (dynamicId) {
			int id;
			if (dynamicPacketMap.getKeys().contains(clazz.getName())) {
				id = dynamicPacketMap.register(clazz.getName());
			} else {
				id = getNextId();
			}
			codec = constructor.newInstance(id);
			codec.setDynamic(true);
		} else {
			codec = constructor.newInstance();
			nextId = nextId > codec.getOpcode() ? nextId : codec.getOpcode() + 1;
		}

		codec = register(codec);

		dynamicPacketMap.register(clazz.getName(), codec.getOpcode());
		return codec;
	}

	private <T extends Message, C extends MessageCodec<T>> C register(C codec) {
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

	@Override
	public void onEvent(SyncedMapEvent event) {
		switch (event.getAction()) {
			// TODO: Only reassign opcodes for mis-id'd packets that are already created (w/o calling Class.forName)
			case SET:
				// Keep packet registrations around until they're overwritten, so do nothing here anyway
			case ADD:
				for (Pair<Integer, String> item : event.getModifiedElements()) {
					final int id = item.getLeft();
					final String clazzName = item.getRight();
					if (id >= opcodeTable.length) {
						throw new IllegalStateException("Server sent a packet id which is greater than the client has allowed.");
					}
					 // If the packet has already been registered non-dynamically, don't override
					if ((opcodeTable[id] != null && opcodeTable[id].getClass().getName().equals(clazzName))) {
						continue;
					}
					try {
						// new type(id);
						Class<? extends MessageCodec> clazz = Class.forName(clazzName, true, loader).asSubclass(MessageCodec.class);
						Constructor<? extends MessageCodec> constr = clazz.getConstructor(int.class);
						MessageCodec<?> codec = constr.newInstance(id);

						codec.setDynamic(true);
						register(codec);
					} catch (ClassCastException | ClassNotFoundException | NoSuchMethodException // Squash everything unless debug mode, since the server is *supposed* to send correct data
							| IllegalAccessException | InstantiationException | InvocationTargetException e) { // We may want to print errors for missing packets -- could indicate missing plugins
						if (Spout.debugMode()) {
							e.printStackTrace();
						}
					}
				}
				break;
			case REMOVE:
				for (Pair<Integer, String> item : event.getModifiedElements()) {
					MessageCodec<?> codec = find(item.getLeft());
					if (codec != null && codec.getClass().getName().equals(item.getRight())) {
						opcodeTable[codec.getOpcode()] = null;
						classTable.remove(codec.getType());
					}
				}
				break;
		}
	}
}
