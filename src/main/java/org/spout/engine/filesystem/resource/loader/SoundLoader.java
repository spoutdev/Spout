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
package org.spout.engine.filesystem.resource.loader;

import java.io.InputStream;

import org.lwjgl.util.*;

import org.spout.api.resource.ResourceLoader;

import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.filesystem.resource.ClientSound;

import static org.lwjgl.openal.AL10.*;

public class SoundLoader extends ResourceLoader {
	public SoundLoader() {
		super("sound", null);
	}

	@Override
	public ClientSound load(InputStream in) {
		int id = alGenBuffers(); // generate a new buffer
		SpoutSoundManager.checkErrors();

		// create the sound's metadata and bind it to the new buffer
		WaveData data = WaveData.create(in);
		alBufferData(id, data.format, data.data, data.samplerate);
		data.dispose();

		return new ClientSound(id);
	}
}
