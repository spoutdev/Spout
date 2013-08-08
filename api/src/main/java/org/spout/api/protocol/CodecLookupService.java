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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;

import org.spout.api.Spout;
import org.spout.api.event.object.EventableListener;
import org.spout.api.util.SyncedMapEvent;
import org.spout.api.util.SyncedStringMap;

/**
 * A class used to lookup message codecs.
 */
public class CodecLookupService implements EventableListener<SyncedMapEvent> {
	/**
	 * A lookup table for the Message classes mapped to their Codec.
	 */
	private final ConcurrentMap<Class<? extends Message>, MessageCodec<?>> classTable;
	/**
	 * A synced map for the dynamic packets.
	 */
	private final SyncedStringMap dynamicPacketMap;
	/**
	 * Lookup table for opcodes mapped to their codecs.
	 */
	private final MessageCodec<?>[] opcodeTable;
	/**
	 * Stores the next opcode available.
	 */
	private final AtomicInteger nextId;
	/**
	 * The {@link ClassLoader} for the codecs.
	 */
	private final ClassLoader loader;

	/**
	 * The {@link CodecLookupService} stores the codecs available in the protocol. Codecs can be found using either the class of the message they represent or their message's opcode.
	 *
	 * @param loader The class loader for the codecs
	 * @param dynamicPacketMap - The dynamic opcode map
	 * @param size The maximum number of message types
	 */
	protected CodecLookupService(ClassLoader loader, SyncedStringMap dynamicPacketMap, int size) {
		this.classTable = new ConcurrentHashMap<>(size, 1.0f);
		this.opcodeTable = new MessageCodec<?>[size];
		this.dynamicPacketMap = dynamicPacketMap;
		this.nextId = new AtomicInteger(0);
		this.loader = loader;
	}

	/**
	 * Binds a codec by adding entries for it to the tables. TODO: if a dynamic opcode is registered then a static opcode tries to register, reassign dynamic. TODO: if a static opcode is registered then
	 * a static opcode tries to register, throw exception
	 *
	 * @param clazz The codec's class.
	 * @param <T> The type of message.
	 * @param <C> The type of codec.
	 * @throws InstantiationException if the codec could not be instantiated.
	 * @throws IllegalAccessException if the codec could not be instantiated due to an access violation.
	 */
	@SuppressWarnings ("unchecked")
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
		if (dynamicPacketMap.getKeys().contains(clazz.getName())) {
			//Already bound, return codec
			return (C) find(dynamicPacketMap.register(clazz.getName()));
		}
		final C codec;
		if (dynamicId) {
			int id;
			do {
				id = nextId.getAndIncrement();
			} while (find(id) != null);
			codec = constructor.newInstance(id);
			codec.setDynamic(true);
		} else {
			//Codec is static
			codec = constructor.newInstance();
			final MessageCodec<?> prevCodec = find(codec.getOpcode());
			if (prevCodec != null) {
				throw new IllegalStateException("Trying to bind a static opcode where one already exists. Static: " + clazz.getSimpleName() + " Other: " + prevCodec.getClass().getSimpleName());
			}
		}
		register(codec);
		dynamicPacketMap.register(clazz.getName(), codec.getOpcode());
		return codec;
	}

	/**
	 * Registers the provided codec with the lookup service, allowing it to be looked up later on in the future.
	 *
	 * @param <T> The type of message the codec represents
	 * @param <C> The type of codec
	 * @param codec The codec to be registered
	 */
	private <T extends Message, C extends MessageCodec<T>> void register(C codec) {
		opcodeTable[codec.getOpcode()] = codec;
		classTable.put(codec.getType(), codec);
	}

	/**
	 * Retrieves the {@link MessageCodec} from the lookup table
	 *
	 * @param opcode The opcode which the codec uses
	 * @return The codec, null if not found.
	 */
	public MessageCodec<?> find(int opcode) {
		if (opcode < 0 || opcode >= opcodeTable.length) {
			throw new IllegalArgumentException("Opcode " + opcode + " is out of bounds");
		}
		return opcodeTable[opcode];
	}

	/**
	 * Finds a codec by message class.
	 *
	 * @param clazz The message class.
	 * @param <T> The type of message.
	 * @return The codec, or {@code null} if it could not be found.
	 */
	@SuppressWarnings ("unchecked")
	public <T extends Message> MessageCodec<T> find(Class<T> clazz) {
		return (MessageCodec<T>) classTable.get(clazz);
	}

	/**
	 * Returns A collection of all the codecs which have been registered so far.
	 *
	 * @return Collection of codecs
	 */
	public Collection<MessageCodec<?>> getCodecs() {
		return Collections.unmodifiableCollection(classTable.values());
	}

	/**
	 * Event is called when a {@link SyncedMapEvent} is fired.
	 *
	 * @param event The event which was fired
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public void onEvent(SyncedMapEvent event) {
		switch (event.getAction()) {
			// TODO: Only reassign opcodes for mis-id'd packets that are already created (w/o calling Class.forName)
			case SET:
				// Keep packet registrations around until they're overwritten, so do nothing here anyway
			case ADD:
				for (Pair<Integer, String> item : event.getModifiedElements()) {
					final int id = item.getLeft();
					final String className = item.getRight();
					if (id >= opcodeTable.length) {
						throw new IllegalStateException("Server sent a packet id which is greater than the client has allowed.");
					}
					MessageCodec<?> codec = opcodeTable[id];
					if (codec != null) {
						if (codec.getClass().getName().equals(className)) {
							continue;
						}
						throw new IllegalArgumentException("Trying to register a codec where one already exists: " + className);
					}
					try {
						final Class<? extends MessageCodec> clazz = Class.forName(className, true, loader).asSubclass(MessageCodec.class);
						final Constructor<? extends MessageCodec> constr = clazz.getConstructor(int.class);
						codec = constr.newInstance(id);
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
