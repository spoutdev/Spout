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

import org.spout.api.math.Vector3;
import org.spout.api.util.Named;

/**
 * Represents a source of sound in the game.
 */
public interface SoundSource extends Named {
	
	/**
	 * Gets the current state of the SoundSource.
	 * 
	 * @return the sound state
	 */
	public SoundState getState();

	/**
	 * Gets the Sound associated with this SoundSource.
	 * 
	 * @return the Sound object
	 * @see #setSound
	 */
	public Sound getSound();

	/**
	 * Sets the sound of this SoundSource.
	 * 
	 * @param sound  a Sound object to be set
	 * @see #getSound
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
	 * @return a float specifying the current pitch of the SoundSource
	 * @see #setPitch
	 */
	public float getPitch();

	/**
	 * Sets the pitch of the SoundSource to the given number. This should always
	 * be positive.
	 * 
	 * @param pitch  a float specifying the pitch value
	 * @see #getPitch
	 */
	public void setPitch(float pitch);

	/**
	 * Gets the gain of the SoundSource.
	 * 
	 * @return a float specifying the gain of the SoundSource
	 * @see #setGain
	 */
	public float getGain();

	/**
	 * Sets the gain of the SoundSource. This should always be positive.
	 * 
	 * @param gain  a float specifying the gain value
	 * @see #getGain
	 */
	public void setGain(float gain);

	/**
	 * Gets if this SoundSource is looping.
	 * 
	 * @return whether the SoundSource is looping
	 */
	public boolean isLooping();

	/**
	 * Sets if this SoundSource should be looping.
	 * 
	 * @param looping  a boolean value for if the SoundSource should be looping
	 */
	public void setLooping(boolean looping);

	/**
	 * Gets the playback position of the SoundSource in seconds.
	 * 
	 * @return the playback position of the SoundSource in seconds
	 * @see #setPlaybackPosition
	 */
	public float getPlaybackPosition();

	/**
	 * Sets the playback position of the SoundSource to the given time in
	 * seconds.
	 * 
	 * @param seconds  time in seconds
	 * @see #getPlaybackPosition
	 */
	public void setPlaybackPosition(float seconds);

	/**
	 * Gets the current position of the SoundSource.
	 * 
	 * @return the Vector3 position of the SoundSource
	 * @see #setPosition
	 */
	public Vector3 getPosition();

	/**
	 * Sets the position of the SoundSource to the given location.
	 * 
	 * @param position  position value
	 * @see #getPosition
	 */
	public void setPosition(Vector3 position);

	/**
	 * Gets the velocity of the SoundSource. Used for doppler effects.
	 * 
	 * @return the Vector3 velocity of the SoundSource
	 * @see #setVelocity
	 */
	public Vector3 getVelocity();

	/**
	 * Sets the velocity of the SoundSource to the given vector.
	 * Used for doppler effects.
	 * 
	 * @param velocity  velocity value
	 * @see #getVelocity
	 */
	public void setVelocity(Vector3 velocity);

	/**
	 * Gets the direction of the SoundSource.
	 * 
	 * @return the Vector3 direction of the SoundSource
	 * @see #setDirection
	 */
	public Vector3 getDirection();

	/**
	 * Sets the direction of the SoundSource to the given vector.
	 * 
	 * @param direction  direction value
	 * @see #getDirection
	 */
	public void setDirection(Vector3 direction);
}
