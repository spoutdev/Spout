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
package org.spout.api.component.entity;

import java.util.List;

import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.protocol.Message;

/**
 * The networking behind {@link org.spout.api.entity.Entity}s.
 */
public abstract class NetworkComponent extends EntityComponent {
	//TODO: Move all observer code to NetworkComponent
	public final DefaultedKey<Boolean> IS_OBSERVER = new DefaultedKeyImpl("IS_OBSERVER", false);
	public final DefaultedKey<Integer> SYNC_DISTANCE = new DefaultedKeyImpl("SYNC_DISTANCE", 0);

	@Override
	public final boolean isDetachable() {
		return false;
	}

	@Override
	public final boolean canTick() {
		return false;
	}

	/**
	 * Returns if the owning {@link org.spout.api.entity.Entity} is an observer.
	 * <p/>
	 * Observer means the Entity can trigger network updates (such as chunk creation) within its sync distance.
	 *
	 * @return True if observer, false if not
	 */
	public boolean isObserver() {
		return getData().get(IS_OBSERVER);
	}

	/**
	 * Sets the observer status for the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param observer True if observer, false if not
	 */
	public void setObserver(final boolean observer) {
		getData().put(IS_OBSERVER, observer);
	}

	/**
	 * Gets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 * </p>
	 * Sync distance is a value indicating the radius outwards from the entity where network updates (such as chunk creation) will be triggered.
	 *
	 * @return The current sync distance
	 */
	public int getSyncDistance() {
		return getData().get(SYNC_DISTANCE);
	}

	/**
	 * Sets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param syncDistance The new sync distance
	 */
	public void setSyncDistance(final int syncDistance) {
		//TODO: Enforce server maximum (but that is set in Spout...)
		getData().put(SYNC_DISTANCE, syncDistance);
	}

	/**
	 * Called when the owner is set to be synchronized to other NetworkComponents.
	 *
	 * TODO: Common logic between Spout and a plugin needing to implement this component?
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	public void finalizeRun(final Transform live) {

	}

	/**
	 * Called just before a snapshot is taken of the owner.
	 *
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	public void preSnapshot(final Transform live) {

	}

	/**
	 * Calls a {@link org.spout.api.event.ProtocolEvent} for all {@link org.spout.api.entity.Player}s in-which the owning {@link org.spout.api.entity.Entity} is within their sync distance
	 * <p/>
	 * If the owning Entity is a Player, it will receive the event as well.
	 *
	 * @param event to send
	 */
	public final void callProtocolEvent(final ProtocolEvent event) {
		callProtocolEvent(event, false);
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link org.spout.api.entity.Player}s in-which the owning {@link org.spout.api.entity.Entity} is within their sync distance
	 *
	 * @param event to send
	 * @param ignoreOwner True to ignore the owning Entity, false to also send it to the Entity (if the Entity is also a Player)
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final boolean ignoreOwner) {
		final List<Player> players = getOwner().getWorld().getPlayers();
		final Point position = getOwner().getPhysics().getPosition();
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();

		for (final Player player : players) {
			if (ignoreOwner && getOwner() == player) {
				continue;
			}
			final Point otherPosition = player.getPhysics().getPosition();
			//TODO: Verify this math
			if (position.subtract(otherPosition).fastLength() > player.getNetwork().getSyncDistance()) {
				continue;
			}
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link Player}s provided.
	 *
	 * @param event to send
	 * @param players to send to
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final Player... players) {
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();
		for (final Player player : players) {
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}
}
