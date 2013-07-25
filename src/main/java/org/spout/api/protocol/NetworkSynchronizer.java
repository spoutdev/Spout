/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.exception.EventException;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.IntVector3;
import org.spout.api.protocol.event.ProtocolEvent;
import org.spout.api.protocol.event.ProtocolEventExecutor;
import org.spout.api.protocol.event.ProtocolEventListener;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.OutwardIterator;

public abstract class NetworkSynchronizer {
	protected final Player player;
	protected final Session session;
	protected final AtomicReference<Protocol> protocol = new AtomicReference<Protocol>(null);
	private final Map<Class<? extends ProtocolEvent>, ProtocolEventExecutor> protocolEventMapping = new HashMap<Class<? extends ProtocolEvent>, ProtocolEventExecutor>();
	private final AtomicReference<RepositionManager> rm = new AtomicReference<RepositionManager>(NullRepositionManager.getInstance());

	public NetworkSynchronizer(Session session) {
		this.session = session;
		player = session.getPlayer();
		if (player != null) {
			// TODO this shouldn't be needed because setObserver(true) is in SpoutPlayer; is there a reason?
			player.setObserver(true);
		}
	}

	public Player getPlayer() {
		return player;
	}

	protected void registerProtocolEvents(final ProtocolEventListener listener) {
		for (final Method method : listener.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(EventHandler.class) && method.getParameterTypes().length == 1) {
				Class<?> clazz = method.getParameterTypes()[0];
				if (!ProtocolEvent.class.isAssignableFrom(clazz)) {
					session.getEngine().getLogger().warning("Invalid protocol event handler attempted to be registered for " + player.getName());
					continue;
				}

				Class<?> returnType = method.getReturnType();
				if (returnType == null || returnType.equals(void.class)) {
					session.getEngine().getLogger().warning("Protocol event handler not returning a Message tried to be registered for " + player.getName());
					session.getEngine().getLogger().warning("Please change the return type from 'void' to Message");
					continue;
				} else if (!Message.class.isAssignableFrom(returnType)) {
					Class<?> compType = returnType.getComponentType();
					if (compType == null || !Message.class.isAssignableFrom(compType)) {
						session.getEngine().getLogger().warning("Protocol event handler not returning a Message tried to be registered for " + player.getName());
						continue;
					}
				}

				method.setAccessible(true);
				protocolEventMapping.put(clazz.asSubclass(ProtocolEvent.class), new ProtocolEventExecutor() {
					@Override
					public Message[] execute(ProtocolEvent event) throws EventException {
						try {
							Object obj = method.invoke(listener, event);
							if (obj == null) {
								return null;
							} else if (obj.getClass().isArray()) {
								return (Message[]) obj;
							} else if (Message.class.isAssignableFrom(obj.getClass())) {
								return new Message[] {(Message) obj};
							}
						} catch (InvocationTargetException e) {
							throw new EventException(e.getCause());
						} catch (IllegalAccessException e) {
							throw new EventException(e);
						}
						return null;
					}
				});
			}
		}
	}

	public boolean callProtocolEvent(ProtocolEvent event) {
		ProtocolEventExecutor executor = protocolEventMapping.get(event.getClass());
		if (executor != null) {
			try {
				Message[] messages = executor.execute(event);
				if (messages != null && messages.length > 0) {
					session.sendAll(messages);
					return true;
				}
			} catch (EventException e) {
				if (e.getCause() != null) {
					Throwable t = e.getCause();
					session.getEngine().getLogger().severe("Error occurred while calling protocol event"
							+ event.getClass().getSimpleName() + " for player " + player.getName() + ": " + t.getMessage());
					t.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick() {
	}
	public void preSnapshot() {
	}

	/**
	 * Gets the viewable volume centred on the given chunk coordinates and the given view distance
	 * 
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param viewDistance
	 * @return
	 */
	public Iterator<IntVector3> getViewableVolume(int cx, int cy, int cz, int viewDistance) {
		return new OutwardIterator(cx, cy, cz, viewDistance);
	}
	
	/**
	 * Test if a given chunk base is in the view volume for a given player chunk base point
	 * 
	 * @param playerChunkBase
	 * @param testChunkBase
	 * @return true if in the view volume
	 */
	public boolean isInViewVolume(Point playerChunkBase, Point testChunkBase, int viewDistance) {
		return testChunkBase.getManhattanDistance(playerChunkBase) <= (viewDistance << Chunk.BLOCKS.BITS);
	}

	/**
	 * Sets the protocol associated with this network synchronizer
	 *
	 * @param protocol
	 */
	// TODO simplify this process; shouldn't need to be set
	public void setProtocol(Protocol protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol may not be null");
		} else if (!this.protocol.compareAndSet(null, protocol)) {
			throw new IllegalStateException("Protocol may not be set twice for a network synchronizer");
		}
	}
	
	/**
	 * Gets the reposition manager that converts local coordinates into remote coordinates
	 * 
	 * @return
	 */
	public RepositionManager getRepositionManager() {
		return rm.get();
	}
	
	public void setRepositionManager(RepositionManager rm) {
		if (rm == null) {
			this.rm.set(NullRepositionManager.getInstance());
		} else {
			this.rm.set(rm);
		}
	}
}
