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

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundSource;
import org.spout.api.audio.SoundState;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.BufferUtils.*;

public class SpoutSoundSource extends SoundSource {
	protected SpoutSoundSource(String name) {
		super(alGenSources(), name);
	}

	@Override
	public void init() {
		// cast is safe because of protected constructor and sound manager is only available on client
		setPosition(((Client) Spout.getEngine()).getPlayer().getScene().getPosition());
		setVelocity(Vector3.ZERO);
		setPitch(1);
		setGain(1);
	}

	@Override
	public void bind() {
		setInteger(AL_BUFFER, sound.getId());
	}

	@Override
	public void doPlay() {
		alSourcePlay(id);
	}

	@Override
	public void pause() {
		alSourcePause(id);
	}

	@Override
	public void stop() {
		alSourceStop(id);
	}

	@Override
	public void rewind() {
		alSourceRewind(id);
	}

	@Override
	public void dispose() {
		alDeleteSources(id);
	}

	@Override
	public float getPitch() {
		return getFloat(AL_PITCH);
	}

	@Override
	public void setPitch(float pitch) {
		setFloat(AL_PITCH, pitch);
	}

	@Override
	public float getGain() {
		return getFloat(AL_GAIN);
	}

	@Override
	public void setGain(float gain) {
		setFloat(AL_GAIN, gain);
	}

	@Override
	public boolean isLooping() {
		return getBoolean(AL_LOOPING);
	}

	@Override
	public void setLooping(boolean looping) {
		setBoolean(AL_LOOPING, looping);
	}

	@Override
	public float getPlaybackPosition() {
		return getFloat(AL_SEC_OFFSET);
	}

	@Override
	public void setPlaybackPosition(float seconds) {
		setFloat(AL_SEC_OFFSET, seconds);
	}

	@Override
	public Point getPosition() {
		return new Point(getVector3(AL_POSITION), world);
	}

	@Override
	public void setPosition(Point position) {
		world = position.getWorld();
		setVector3(AL_POSITION, position);
	}

	@Override
	public Vector3 getVelocity() {
		return getVector3(AL_VELOCITY);
	}

	@Override
	public void setVelocity(Vector3 velocity) {
		setVector3(AL_VELOCITY, velocity);
	}

	@Override
	public Vector3 getDirection() {
		return getVector3(AL_DIRECTION);
	}

	@Override
	public void setDirection(Vector3 direction) {
		setVector3(AL_DIRECTION, direction);
	}

	@Override
	public SoundState getState() {
		return SoundState.get(getInteger(AL_SOURCE_STATE));
	}

	private void setInteger(int k, int v) {
		alSourcei(id, k, v);
	}

	private int getInteger(int k) {
		return alGetSourcei(id, k);
	}

	private float getFloat(int k) {
		return alGetSourcef(id, k);
	}

	private void setFloat(int k, float v) {
		alSourcef(id, k, v);
	}

	private boolean getBoolean(int k) {
		return getInteger(k) == AL_TRUE;
	}

	private void setBoolean(int k, boolean v) {
		setInteger(k, v ? AL_TRUE : AL_FALSE);
	}

	private Vector3 getVector3(int k) {
		FloatBuffer buff = createFloatBuffer(3);
		alGetSource(id, k, buff);
		return new Vector3(buff.get(0), buff.get(1), buff.get(2));
	}

	private void setVector3(int k, Vector3 v) {
		alSource(id, k, (FloatBuffer) createFloatBuffer(3).put(v.toArray()).flip());
	}
}
