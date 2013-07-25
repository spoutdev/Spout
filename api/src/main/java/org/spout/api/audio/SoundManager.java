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

import java.util.Set;

/**
 * Manages Sounds.
 */
public interface SoundManager {
	/**
	 * Initializes the SoundManager.
	 */
	public void init();

	/**
	 * Creates a new {@link SoundSource} with the specified name.
	 *
	 * @param sound a Sound object for the new SoundSource
	 * @param music true if music
	 * @return the created SoundSource
	 */
	public SoundSource createSource(Sound sound, boolean music);

	/**
	 * Creates a new {@link SoundSource} with the specified name.
	 *
	 * @param sound a Sound object for the new SoundSource
	 * @return the created SoundSource
	 */
	public SoundSource createSource(Sound sound);

	/**
	 * Removes a {@link SoundSource} from the manager.
	 *
	 * @param source a SoundSource object
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
	 * Returns the source with the specified id.
	 *
	 * @param id to get
	 * @return source with id
	 */
	public SoundSource getSource(int id);

	/**
	 * Returns true if the specified id has an associated source.
	 *
	 * @param id to get
	 * @return true if has source
	 */
	public boolean isSource(int id);

	/**
	 * Returns the overall gain for sounds.
	 *
	 * @return overall gain
	 */
	public float getGain();

	/**
	 * Sets the overall gain for sounds.
	 *
	 * @param gain to set
	 */
	public void setGain(float gain);

	/**
	 * Returns the overall gain for music.
	 *
	 * @return gain
	 */
	public float getMusicGain();

	/**
	 * Sets the overall gain for music.
	 *
	 * @param musicGain gain
	 */
	public void setMusicGain(float musicGain);

	/**
	 * Levels off the current sounds and music to set gain levels.
	 */
	public void level();

	/**
	 * Returns a set of all loaded sounds.
	 *
	 * @return loaded sounds
	 */
	public Set<Sound> getSounds();

	/**
	 * Adds a new pre-loaded sound to the manager
	 *
	 * @param sound to add
	 */
	public void addSound(Sound sound);

	/**
	 * Removes and disposes the specified {@link Sound}.
	 *
	 * @param sound to remove and dispose
	 * @see org.spout.api.audio.Sound#dispose()
	 */
	public void removeSound(Sound sound);

	/**
	 * Removes and disposes all registered sounds.
	 */
	public void clearSounds();

	/**
	 * Returns the sound with the specified id.
	 *
	 * @param id to get
	 * @return sound with id
	 */
	public Sound getSound(int id);

	/**
	 * Returns true if there is a sound with the specified id.
	 *
	 * @param id to check
	 * @return true if has associated sound
	 */
	public boolean isSound(int id);

	/**
	 * Returns the active {@link SoundListener}.
	 *
	 * @return active SoundListener
	 */
	public SoundListener getListener();

	/**
	 * Removes and disposes all sounds and sources.
	 */
	public void clear();

	/**
	 * Returns true if the sound system has been destroyed. If so, {@link #init()} will need to be called before performing on this system any further.
	 *
	 * @return true if has been destroyed
	 */
	public boolean isDestroyed();

	/**
	 * De-initializes the sound system and clears all sounds and sources.
	 */
	public void destroy();
}
