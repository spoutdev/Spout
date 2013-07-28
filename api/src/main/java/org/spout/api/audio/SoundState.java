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
package org.spout.api.audio;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents the state of a SoundSource.
 */
public enum SoundState {
	/**
	 * The sound has not been played before.
	 */
	INITIAL(0),
	/**
	 * The sound is currently playing.
	 */
	PLAYING(1),
	/**
	 * The sound is currently paused.
	 */
	PAUSED(2),
	/**
	 * The sound is stopped.
	 */
	STOPPED(3);
	private final int id;
	private static final TIntObjectMap<SoundState> idMap = new TIntObjectHashMap<>();

	static {
		for (SoundState state : SoundState.values()) {
			idMap.put(state.getId(), state);
		}
	}

	/**
	 * Returns a SoundState object.
	 *
	 * @param id an int representing the id of the SoundState object
	 * @return a SoundState object
	 */
	public static SoundState get(int id) {
		return idMap.get(id);
	}

	private SoundState(int id) {
		this.id = id;
	}

	/**
	 * Returns the id value.
	 *
	 * @return an int representing the id value
	 */
	public int getId() {
		return id;
	}
}
