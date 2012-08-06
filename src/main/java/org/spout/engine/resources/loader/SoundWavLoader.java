/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.resources.loader;

import java.io.InputStream;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.resources.ClientSound;

public class SoundWavLoader extends BasicResourceLoader<ClientSound> {

	@Override
	public String getFallbackResourceName() {
		return "sound_wav://Spout/resources/fallbacks/silence.wav";
	}

	@Override
	public ClientSound getResource(InputStream stream) {
		int buffer = AL10.alGenBuffers();
		SpoutSoundManager.checkErrors();

		WaveData waveFile = WaveData.create(stream);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();

		return new ClientSound(buffer);
	}
}
