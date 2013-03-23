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
package org.spout.api.audio;

import java.util.Set;

import org.spout.api.Client;

/**
 * Manages Sounds.
 */
public interface SoundManager {
	
	/**
	 * Initializes the SoundManager.
	 * 
	 * @param client  a Client object
	 */
	public void init(Client client);

	/**
	 * Creates a new {@link SoundSource} with the specified name.
	 *
	 * @param sound a Sound object for the new SoundSource
	 * @param name  a String representing the name of the new SoundSource
	 * @return the created SoundSource
	 */
	public SoundSource createSource(Sound sound, String name);

	/**
	 * Removes a {@link SoundSource} from the manager.
	 *
	 * @param source  a SoundSource object
	 */
	public void removeSource(SoundSource source);

	/**
	 * Removes all {@link SoundSource}s from the manager.
	 */
	public void clearSources();

	/**
	 * Returns all {@link SoundSource}s.
	 *
	 * @return sources
	 */
	public Set<SoundSource> getSources();

	/**
	 * Returns a {@link SoundSource} with the specified name.
	 *
	 * @param name  a String containing the name of a SoundSource
	 * @return source
	 */
	public SoundSource getSource(String name);

	/**
	 * Returns the active {@link SoundListener}.
	 *
	 * @return active SoundListener
	 */
	public SoundListener getListener();
}
