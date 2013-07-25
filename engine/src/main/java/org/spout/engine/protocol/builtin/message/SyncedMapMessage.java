/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.protocol.builtin.message;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.spout.api.util.SpoutToStringStyle;
import org.spout.api.util.SyncedMapEvent;

public class SyncedMapMessage extends SpoutMessage {
	private final int map;
	private final SyncedMapEvent.Action action;
	private final List<Pair<Integer, String>> elements;

	public SyncedMapMessage(int map, SyncedMapEvent.Action action, List<Pair<Integer, String>> elements) {
		this.map = map;
		this.action = action;
		this.elements = Collections.unmodifiableList(elements);
	}

	public SyncedMapMessage(int map, int action, List<Pair<Integer, String>> elements) {
		if (action < 0 || action >= SyncedMapEvent.Action.values().length) {
			throw new IllegalArgumentException("Unknown action ID " + action);
		}
		this.action = SyncedMapEvent.Action.values()[action];
		this.map = map;
		this.elements = Collections.unmodifiableList(elements);
	}

	public int getMap() {
		return map;
	}

	public SyncedMapEvent.Action getAction() {
		return action;
	}

	public List<Pair<Integer, String>> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("map", map)
				.append("action", action)
				.append("elements", elements)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(97, 73)
				.append(map)
				.append(action)
				.append(elements)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SyncedMapMessage) {
			final SyncedMapMessage other = (SyncedMapMessage) obj;
			return new EqualsBuilder()
					.append(map, other.map)
					.append(action, other.action)
					.append(elements, other.elements)
					.isEquals();
		} else {
			return false;
		}
	}
}
