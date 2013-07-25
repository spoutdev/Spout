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

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * Represents a source of sound in the game.
 */
public abstract class SoundSource {
	protected final int id;
	protected Sound sound;
	protected boolean music;
	protected World world;

	protected SoundSource(int id) {
		this.id = id;
	}

	/**
	 * Returns the unique identifier for this source.
	 *
	 * @return source
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns true if this source is categorized as a music source.
	 *
	 * @return true if music
	 */
	public boolean isMusic() {
		return music;
	}

	/**
	 * Sets if this source is categorized as a music source.
	 *
	 * @param music true if music
	 */
	public void setMusic(boolean music) {
		this.music = music;
	}

	/**
	 * Initializes the sound to it's initial state.
	 */
	public abstract void init();

	/**
	 * Gets the Sound associated with this SoundSource.
	 *
	 * @return the Sound object
	 */
	public Sound getSound() {
		return sound;
	}

	/**
	 * Binds the specified sound to this source.
	 *
	 * @param sound to bind
	 */
	public final void setSound(Sound sound) {
		this.sound = sound;
		bind();
	}

	protected abstract void bind();

	/**
	 * Plays this SoundSource.
	 */
	public final void play() {
		// make sure the client's listener is in the same world as the source
		// TODO: Needs further world handling on client
		//if (!((Client) Spout.getEngine()).getSoundManager().getListener().getPosition().getWorld().equals(world)) {
		//return;
		//}
		doPlay();
	}

	protected abstract void doPlay();

	/**
	 * Pauses this SoundSource.
	 */
	public abstract void pause();

	/**
	 * Stops this SoundSource.
	 */
	public abstract void stop();

	/**
	 * Rewinds this SoundSource back to the beginning. This stops the SoundSource if it is playing.
	 */
	public abstract void rewind();

	/**
	 * Returns true if the source has been disposed and cannot be used anymore.
	 *
	 * @return true if disposed
	 */
	public abstract boolean isDisposed();

	/**
	 * Disposes the sound and does cleanup.
	 */
	public abstract void dispose();

	/**
	 * Gets the current pitch of the SoundSource. This is a multiplier.
	 *
	 * @return a float specifying the current pitch of the SoundSource
	 * @see #setPitch
	 */
	public abstract float getPitch();

	/**
	 * Sets the pitch of the SoundSource to the given number. This should always be positive.
	 *
	 * @param pitch a float specifying the pitch value
	 * @see #getPitch
	 */
	public abstract void setPitch(float pitch);

	/**
	 * Gets the gain of the SoundSource.
	 *
	 * @return a float specifying the gain of the SoundSource
	 * @see #setGain
	 */
	public abstract float getGain();

	/**
	 * Sets the gain of the SoundSource. This should always be positive.
	 *
	 * @param gain a float specifying the gain value
	 * @see #getGain
	 */
	public abstract void setGain(float gain);

	/**
	 * Gets if this SoundSource is looping.
	 *
	 * @return whether the SoundSource is looping
	 */
	public abstract boolean isLooping();

	/**
	 * Sets if this SoundSource should be looping.
	 *
	 * @param looping a boolean value for if the SoundSource should be looping
	 */
	public abstract void setLooping(boolean looping);

	/**
	 * Gets the playback position of the SoundSource in seconds.
	 *
	 * @return the playback position of the SoundSource in seconds
	 * @see #setPlaybackPosition
	 */
	public abstract float getPlaybackPosition();

	/**
	 * Sets the playback position of the SoundSource to the given time in seconds.
	 *
	 * @param seconds time in seconds
	 * @see #getPlaybackPosition
	 */
	public abstract void setPlaybackPosition(float seconds);

	/**
	 * Gets the current position of the SoundSource.
	 *
	 * @return the Vector3 position of the SoundSource
	 * @see #setPosition
	 */
	public abstract Point getPosition();

	/**
	 * Sets the position of the SoundSource to the given location.
	 *
	 * @param position position value
	 * @see #getPosition
	 */
	public abstract void setPosition(Point position);

	/**
	 * Gets the velocity of the SoundSource. Used for doppler effects.
	 *
	 * @return the Vector3 velocity of the SoundSource
	 * @see #setVelocity
	 */
	public abstract Vector3 getVelocity();

	/**
	 * Sets the velocity of the SoundSource to the given vector. Used for doppler effects.
	 *
	 * @param velocity velocity value
	 * @see #getVelocity
	 */
	public abstract void setVelocity(Vector3 velocity);

	/**
	 * Gets the direction of the SoundSource.
	 *
	 * @return the Vector3 direction of the SoundSource
	 * @see #setDirection
	 */
	public abstract Vector3 getDirection();

	/**
	 * Sets the direction of the SoundSource to the given vector.
	 *
	 * @param direction direction value
	 * @see #getDirection
	 */
	public abstract void setDirection(Vector3 direction);

	/**
	 * Gets the current state of the SoundSource.
	 *
	 * @return the sound state
	 */
	public abstract SoundState getState();

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SoundSource && ((SoundSource) obj).id == id;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).build();
	}
}
