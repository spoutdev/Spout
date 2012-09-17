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
package org.spout.engine.audio;

import java.util.logging.Level;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

import org.spout.api.Spout;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundManager;
import org.spout.api.audio.SoundSource;

/**
 * {@link SoundManager} Spout implementation
 */
public class SpoutSoundManager implements SoundManager {
	@Override
	public void init() {
		try {
			AL.create();
		} catch (LWJGLException le) {
			Spout.getLogger().log(Level.SEVERE, "Could not initialize OpenAL!", le);
			return;
		}
		checkErrors();
	}

	@Override
	public SoundSource createSource(Sound sound) {
		SoundSource source = new SpoutSoundSource();
		source.setSound(sound);
		return source;
	}

	/**
	 * Checks for any pending OpenAL errors.
	 */
	public static void checkErrors() {
		int error = AL10.alGetError();
		if (error != AL10.AL_NO_ERROR) {
			throw new OpenALException(error);
		}
	}
}
