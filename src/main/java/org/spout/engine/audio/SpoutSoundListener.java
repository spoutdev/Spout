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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import org.spout.api.audio.SoundListener;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

public class SpoutSoundListener implements SoundListener {
	private World world;

	@Override
	public void setPosition(Point pos) {
		world = pos.getWorld();
		setVector3(AL10.AL_POSITION, pos);
	}

	@Override
	public Point getPosition() {
		Vector3 v = getVector3(AL10.AL_POSITION);
		return new Point(world, v.getX(), v.getY(), v.getZ());
	}

	@Override
	public void setVelocity(Vector3 vec) {
		setVector3(AL10.AL_VELOCITY, vec);
	}

	@Override
	public Vector3 getVelocity() {
		return getVector3(AL10.AL_VELOCITY);
	}

	@Override
	public void setOrientation(Vector3 at, Vector3 up) {
		setFloatArray(AL10.AL_ORIENTATION, getOrientationArray(at, up));
	}

	@Override
	public void setOrientationAt(Vector3 at) {
		Vector3 up = getOrientationUp();
		setFloatArray(AL10.AL_ORIENTATION, getOrientationArray(at, up));
	}

	@Override
	public void setOrientationUp(Vector3 up) {
		Vector3 at = getOrientationAt();
		setFloatArray(AL10.AL_ORIENTATION, getOrientationArray(at, up));
	}

	@Override
	public Vector3 getOrientationAt() {
		FloatBuffer buffer = getFloatBuffer(AL10.AL_ORIENTATION, 6);
		return new Vector3(buffer.get(0), buffer.get(1), buffer.get(2));
	}

	@Override
	public Vector3 getOrientationUp() {
		FloatBuffer buffer = getFloatBuffer(AL10.AL_ORIENTATION, 6);
		return new Vector3(buffer.get(3), buffer.get(4), buffer.get(5));
	}

	private float[] getOrientationArray(Vector3 at, Vector3 up) {
		return new float[] {at.getX(), at.getY(), at.getZ(), up.getX(), up.getY(), up.getZ()};
	}

	private void setVector3(int prop, Vector3 vec) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3).put(vec.toArray());
		buffer.flip();
		AL10.alListener(prop, buffer);
	}

	private Vector3 getVector3(int prop) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		AL10.alGetListener(prop, buffer);
		return new Vector3(buffer.get(0), buffer.get(1), buffer.get(2));
	}

	private void setFloatArray(int prop, float[] a) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(a.length).put(a);
		buffer.flip();
		AL10.alListener(prop, buffer);
	}

	private FloatBuffer getFloatBuffer(int prop, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		AL10.alGetListener(prop, buffer);
		return buffer;
	}
}
