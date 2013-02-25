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
package org.spout.engine.audio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

import org.spout.api.Spout;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundListener;
import org.spout.api.audio.SoundManager;
import org.spout.api.audio.SoundSource;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

public class SpoutSoundManager implements SoundManager {
	private final Set<SoundSource> sources = new HashSet<SoundSource>();
	private final SoundListener listener = new SpoutSoundListener();

	@Override
	public void init() {
		try {
			AL.create();
		} catch (LWJGLException le) {
			Spout.getLogger().log(Level.SEVERE, "Could not initialize OpenAL!", le);
			return;
		}
		checkErrors();

		// Initialize the listener
		listener.setPosition(new Point(Spout.getEngine().getDefaultWorld(), 0, 0, 0));
		listener.setVelocity(Vector3.ZERO);
		listener.setOrientation(Vector3.ZERO, Vector3.ZERO);
	}

	@Override
	public SoundSource createSource(Sound sound, String name) {
		SoundSource source = new SpoutSoundSource(name);
		source.setSound(sound);
		sources.add(source);
		return source;
	}

	@Override
	public void removeSource(SoundSource source) {
		sources.remove(source);
	}

	@Override
	public void clearSources() {
		sources.clear();
	}

	@Override
	public Set<SoundSource> getSources() {
		return Collections.unmodifiableSet(sources);
	}

	@Override
	public SoundSource getSource(String name) {
		for (SoundSource source : sources) {
			if (source.getName().equalsIgnoreCase(name)) {
				return source;
			}
		}
		return null;
	}

	@Override
	public SoundListener getListener() {
		return listener;
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
