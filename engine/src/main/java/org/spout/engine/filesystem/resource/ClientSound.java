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
package org.spout.engine.filesystem.resource;

import org.spout.api.audio.Sound;

import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGetBufferi;

/**
 * An OpenAL-based implementation of the Sound class.
 */
public class ClientSound extends Sound {
	private boolean disposed;

	public ClientSound(int id) {
		super(id);
	}

	@Override
	public void dispose() {
		assertNotDisposed();
		alDeleteBuffers(id);
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public int getSamplingRate() {
		return getInt(AL_FREQUENCY);
	}

	@Override
	public int getBitDepth() {
		return getInt(AL_BITS);
	}

	@Override
	public int getChannels() {
		return getInt(AL_CHANNELS);
	}

	@Override
	public int getBufferSize() {
		return getInt(AL_SIZE);
	}

	@Override
	protected void finalize() {
		if (!disposed) {
			dispose();
		}
	}

	private int getInt(int property) {
		assertNotDisposed();
		return alGetBufferi(id, property);
	}

	private void assertNotDisposed() {
		if (disposed) {
			throw new IllegalStateException("This sound has already been disposed and cannot be used.");
		}
	}
}
