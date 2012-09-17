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

import org.spout.api.resource.Resource;

/**
 * Represents a sound.
 */
public abstract class Sound extends Resource {
	/**
	 * Gets the sampling rate (sampling frequency) of the sound.
	 * 
	 * @return the sampling rate
	 */
	public abstract int getSamplingRate();

	/**
	 * Gets the bit depth of the buffer.
	 * 
	 * @return the buffer's bit depth
	 */
	public abstract int getBitDepth();

	/**
	 * Gets the number of channels of the sound.
	 * 
	 * @return the number of sound channels
	 */
	public abstract int getChannels();

	/**
	 * Gets the size of the buffer.
	 * 
	 * @return the buffer size
	 */
	public abstract int getBufferSize();

	/**
	 * Gets the bit rate of the sample in bits per second.
	 * 
	 * @return the bit rate in bits/second
	 */
	public int getBitRate() {
		return getSamplingRate() * getBitDepth() * getChannels();
	}

	/**
	 * Returns the length of the sound sample in seconds.
	 * 
	 * @return the length of the sound in seconds
	 */
	public float getLength() {
		return getBufferSize() / (float) (getBitRate() / 8);
	}
}
