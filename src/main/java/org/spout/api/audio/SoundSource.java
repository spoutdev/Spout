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
package org.spout.api.audio;

import org.spout.api.math.Vector3;

/**
 * Represents a source of sound in the game.
 */
public interface SoundSource {
	/**
	 * Gets the current state of the SoundSource.
	 * 
	 * @return
	 */
	public SoundSourceState getState();

	/**
	 * Gets the Sound associated with this SoundSource.
	 * 
	 * @return
	 */
	public Sound getSound();

	/**
	 * Sets the sound of this SoundSource.
	 * 
	 * @param sound
	 */
	public void setSound(Sound sound);

	/**
	 * Resets this SoundSource to the default state.
	 */
	public void reset();

	/**
	 * Plays this SoundSource.
	 */
	public void play();

	/**
	 * Pauses this SoundSource.
	 */
	public void pause();

	/**
	 * Stops this SoundSource.
	 */
	public void stop();

	/**
	 * Rewinds this SoundSource back to the beginning. This stops the
	 * SoundSource if it is playing.
	 */
	public void rewind();

	/**
	 * Gets the current pitch of the SoundSource. This is a multiplier.
	 * 
	 * @return the current pitch of the SoundSource
	 */
	public float getPitch();

	/**
	 * Sets the pitch of the SoundSource to the given number. This should always
	 * be positive.
	 * 
	 * @param pitch
	 */
	public void setPitch(float pitch);

	/**
	 * Gets the gain of the SoundSource.
	 * 
	 * @return the gain of the SoundSource
	 */
	public float getGain();

	/**
	 * Sets the gain of the SoundSource. This should always be positive.
	 * 
	 * @param gain
	 */
	public void setGain(float gain);

	/**
	 * Gets if this sound source is looping.
	 * 
	 * @return whether the SoundSource is looping
	 */
	public boolean isLooping();

	/**
	 * Sets if this sound source should be looping.
	 * 
	 * @param looping
	 */
	public void setLooping(boolean looping);

	/**
	 * Gets the playback position of the sound source in seconds.
	 * 
	 * @return the playback position of the SoundSource in seconds
	 */
	public float getPlaybackPosition();

	/**
	 * Sets the playback position of the sound source to the given time in
	 * seconds.
	 * 
	 * @param seconds
	 */
	public void setPlaybackPosition(float seconds);

	/**
	 * Gets the current position of the SoundSource.
	 * 
	 * @return the Vector3 position of the SoundSource
	 */
	public Vector3 getPosition();

	/**
	 * Sets the position of the SoundSource to the given location.
	 * 
	 * @param position
	 */
	public void setPosition(Vector3 position);

	/**
	 * Gets the velocity of the SoundSource.
	 * 
	 * @return the Vector3 velocity of the SoundSource
	 */
	public Vector3 getVelocity();

	/**
	 * Sets the velocity of the SoundSource to the given vector.
	 * 
	 * @param velocity
	 */
	public void setVelocity(Vector3 velocity);

	/**
	 * Gets the direction of the SoundSource.
	 * 
	 * @return the Vector3 direction of the SoundSource
	 */
	public Vector3 getDirection();

	/**
	 * Sets the direction of the SoundSource to the given vector.
	 * 
	 * @param direction
	 */
	public void setDirection(Vector3 direction);
}
