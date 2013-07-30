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

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.audio.SoundSource;
import org.spout.api.audio.SoundState;
import org.spout.api.event.audio.SoundBindEvent;
import org.spout.api.event.audio.SoundDisposeEvent;
import org.spout.api.event.audio.SoundStateChangeEvent;
import org.spout.api.geo.discrete.Point;
import org.spout.math.vector.Vector3;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_DIRECTION;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_PITCH;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGetSource;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSource;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceRewind;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;

public class SpoutSoundSource extends SoundSource {
	private boolean disposed;

	protected SpoutSoundSource(int id) {
		super(id);
	}

	@Override
	public void init() {
		// cast is safe because of protected constructor and sound manager is only available on client
		setPosition(((Client) Spout.getEngine()).getPlayer().getPhysics().getPosition());
		setVelocity(Vector3.ZERO);
		setPitch(1);
		setGain(1);
	}

	@Override
	public void bind() {
		SoundBindEvent event = Spout.getEventManager().callEvent(new SoundBindEvent(this, sound));
		sound = event.getSound();
		setInteger(AL_BUFFER, sound.getId());
	}

	private boolean callStateEvent(SoundState state) {
		return Spout.getEventManager().callEvent(new SoundStateChangeEvent(this, state)).isCancelled();
	}

	@Override
	protected void doPlay() {
		if (callStateEvent(SoundState.PLAYING)) {
			return;
		}
		assertNotDisposed();
		alSourcePlay(id);
	}

	@Override
	public void pause() {
		if (callStateEvent(SoundState.PAUSED)) {
			return;
		}
		assertNotDisposed();
		alSourcePause(id);
	}

	@Override
	public void stop() {
		if (callStateEvent(SoundState.STOPPED)) {
			return;
		}
		assertNotDisposed();
		alSourceStop(id);
	}

	@Override
	public void rewind() {
		if (callStateEvent(SoundState.STOPPED)) {
			return;
		}
		assertNotDisposed();
		alSourceRewind(id);
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void dispose() {
		if (Spout.getEventManager().callEvent(new SoundDisposeEvent(this)).isCancelled()) {
			return;
		}
		assertNotDisposed();
		alDeleteSources(id);
		disposed = true;
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

	@Override
	protected void finalize() {
		if (!disposed) {
			dispose();
		}
	}

	private void setInteger(int k, int v) {
		assertNotDisposed();
		alSourcei(id, k, v);
	}

	private int getInteger(int k) {
		assertNotDisposed();
		return alGetSourcei(id, k);
	}

	private float getFloat(int k) {
		assertNotDisposed();
		return alGetSourcef(id, k);
	}

	private void setFloat(int k, float v) {
		assertNotDisposed();
		alSourcef(id, k, v);
	}

	private boolean getBoolean(int k) {
		return getInteger(k) == AL_TRUE;
	}

	private void setBoolean(int k, boolean v) {
		setInteger(k, v ? AL_TRUE : AL_FALSE);
	}

	private Vector3 getVector3(int k) {
		assertNotDisposed();
		FloatBuffer buff = createFloatBuffer(3);
		alGetSource(id, k, buff);
		return new Vector3(buff.get(0), buff.get(1), buff.get(2));
	}

	private void setVector3(int k, Vector3 v) {
		assertNotDisposed();
		alSource(id, k, (FloatBuffer) createFloatBuffer(3).put(v.toArray()).flip());
	}

	private void assertNotDisposed() {
		if (disposed) {
			throw new IllegalStateException("This source has already been disposed and cannot be used.");
		}
	}
}
