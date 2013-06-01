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

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a Sound.
 */
public abstract class Sound {
	protected final int id;
	
	/**
	 * Constructs a Sound object.
	 *
	 * @param id unique identifier
	 */
	public Sound(int id) {
		this.id = id;
	}

	/**
	 * Returns the unique identifier of this Sound.
	 *
	 * @return an int specifying the id of this Sound object
	 */
	public int getId() {
		return id;
	}

	/**
	 * Cleans up and deletes the sound from the system.
	 */
	public abstract void dispose();

	/**
	 * Gets the sampling rate (sampling frequency) of this Sound.
	 * 
	 * @return an int representing the sampling rate of this Sound
	 */
	public abstract int getSamplingRate();

	/**
	 * Gets the bit depth of the buffer.
	 * 
	 * @return an int representing the buffer's bit depth
	 */
	public abstract int getBitDepth();

	/**
	 * Gets the number of channels of this Sound.
	 * 
	 * @return an int representing the number of sound channels
	 */
	public abstract int getChannels();

	/**
	 * Gets the size of the buffer.
	 * 
	 * @return an int representing the buffer size
	 */
	public abstract int getBufferSize();

	/**
	 * Gets the bit rate of the sample in bits per second.
	 * 
	 * @return an int representing the bit rate in bits per second
	 */
	public int getBitRate() {
		return getSamplingRate() * getBitDepth() * getChannels();
	}

	/**
	 * Returns the length of the Sound sample in seconds.
	 * 
	 * @return a float representing the length of the sound in seconds
	 */
	public float getLength() {
		return getBufferSize() / (float) (getBitRate() / 8);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Sound && ((Sound) obj).id == id;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).build();
	}
}
