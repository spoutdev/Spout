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
package org.spout.engine.audio;

import java.nio.FloatBuffer;

import org.spout.api.audio.SoundListener;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.math.vector.Vector3;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alGetListener;
import static org.lwjgl.openal.AL10.alListener;

public class SpoutSoundListener implements SoundListener {
	private World world;

	@Override
	public void setPosition(Point pos) {
		world = pos.getWorld();
		setVector3(AL_POSITION, pos);
	}

	@Override
	public Point getPosition() {
		return new Point(getVector3(AL_POSITION), world);
	}

	@Override
	public void setVelocity(Vector3 vec) {
		setVector3(AL_VELOCITY, vec);
	}

	@Override
	public Vector3 getVelocity() {
		return getVector3(AL_VELOCITY);
	}

	@Override
	public void setOrientation(Vector3 at, Vector3 up) {
		setFloatArray(AL_ORIENTATION, new float[] {
				at.getX(), at.getY(), at.getZ(), up.getX(), up.getY(), up.getZ()
		});
	}

	@Override
	public void setOrientationAt(Vector3 at) {
		float[] o = getFloatArray(AL_ORIENTATION, 6);
		o[0] = at.getX();
		o[1] = at.getY();
		o[2] = at.getZ();
		setFloatArray(AL_ORIENTATION, o);
	}

	@Override
	public void setOrientationUp(Vector3 up) {
		float[] o = getFloatArray(AL_ORIENTATION, 6);
		o[3] = up.getX();
		o[4] = up.getY();
		o[5] = up.getZ();
		setFloatArray(AL_ORIENTATION, o);
	}

	@Override
	public Vector3 getOrientationAt() {
		float[] o = getFloatArray(AL_ORIENTATION, 6);
		return new Vector3(o[0], o[1], o[2]);
	}

	@Override
	public Vector3 getOrientationUp() {
		float[] o = getFloatArray(AL_ORIENTATION, 6);
		return new Vector3(o[3], o[4], o[5]);
	}

	private void setVector3(int k, Vector3 v) {
		alListener(k, (FloatBuffer) createFloatBuffer(3).put(v.toArray()).flip());
	}

	private Vector3 getVector3(int k) {
		FloatBuffer buff = createFloatBuffer(3);
		alGetListener(k, buff);
		return new Vector3(buff.get(0), buff.get(1), buff.get(2));
	}

	private void setFloatArray(int k, float[] v) {
		alListener(k, (FloatBuffer) createFloatBuffer(v.length).put(v).flip());
	}

	private float[] getFloatArray(int k, int bufferSize) {
		FloatBuffer buff = createFloatBuffer(bufferSize);
		alGetListener(k, buff);
		return buff.array();
	}
}
