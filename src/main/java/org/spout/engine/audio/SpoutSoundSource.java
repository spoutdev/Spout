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

import static org.spout.engine.audio.SpoutSoundManager.checkErrors;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundSource;
import org.spout.api.audio.SoundSourceState;
import org.spout.api.math.Vector3;
import org.spout.engine.resources.ClientSound;

/**
 * Represents a source of sound in the game backed by OpenAL.
 */
public class SpoutSoundSource implements SoundSource {
	private final int sourceId;
	private Sound sound = null;

	public SpoutSoundSource() {
		this.sourceId = AL10.alGenSources();
		reset();
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		if (sound == null) {
			throw new IllegalArgumentException("Sound cannot be null!");
		}

		if (!(sound instanceof ClientSound)) {
			throw new IllegalArgumentException("Sound is not a ClientSound!");
		}

		this.sound = sound;

		bindSound();
	}

	public void reset() {
		setPitch(1f);
		setGain(1f);
		setLooping(false);
		setPlaybackPosition(0f);

		setPosition(Vector3.ZERO);
		setVelocity(Vector3.ZERO);
		setDirection(Vector3.ZERO);

		setFloat(AL10.AL_MAX_DISTANCE, 50f); // Should be in a config
		setFloat(AL10.AL_REFERENCE_DISTANCE, 10f);
	}

	/**
	 * Binds the current sound to the SoundSource.
	 */
	private void bindSound() {
		stop(); // Ensure that the sound has come to a complete stop.

		ClientSound cs = (ClientSound) sound;
		setInt(AL10.AL_BUFFER, cs.getBufferId());
	}

	public SoundSourceState getState() {
		int state = getInt(AL10.AL_SOURCE_STATE);
		switch (state) {
		case AL10.AL_PLAYING:
			return SoundSourceState.PLAYING;
		case AL10.AL_PAUSED:
			return SoundSourceState.PAUSED;
		case AL10.AL_STOPPED:
			return SoundSourceState.STOPPED;
		case AL10.AL_INITIAL:
			return SoundSourceState.INITIAL;
		default:
			return SoundSourceState.UNKNOWN;
		}
	}

	@Override
	public void play() {
		if (!getState().equals(SoundSourceState.PLAYING)) {
			AL10.alSourcePlay(sourceId);
		}
	}

	@Override
	public void pause() {
		if (getState().equals(SoundSourceState.PLAYING)) {
			AL10.alSourcePause(sourceId);
		}
	}

	@Override
	public void stop() {
		// People should just use rewind.
		if (getState().equals(SoundSourceState.PLAYING)) {
			AL10.alSourceStop(sourceId);
		}
	}

	@Override
	public void rewind() {
		AL10.alSourceRewind(sourceId);
	}

	@Override
	public float getPitch() {
		return getFloat(AL10.AL_PITCH);
	}

	@Override
	public void setPitch(float pitch) {
		setFloat(AL10.AL_PITCH, Math.max(pitch, 0f));
	}

	@Override
	public float getGain() {
		return getFloat(AL10.AL_GAIN);
	}

	@Override
	public void setGain(float gain) {
		setFloat(AL10.AL_GAIN, Math.max(gain, 0f));
	}

	@Override
	public boolean isLooping() {
		return getInt(AL10.AL_LOOPING) == AL10.AL_TRUE;
	}

	@Override
	public void setLooping(boolean looping) {
		setInt(AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	@Override
	public float getPlaybackPosition() {
		return getFloat(AL11.AL_SEC_OFFSET);
	}

	@Override
	public void setPlaybackPosition(float seconds) {
		boolean playing = getState().equals(SoundSourceState.PLAYING);

		rewind();
		setFloat(AL11.AL_SEC_OFFSET, seconds);

		if (playing) {
			play();
		}
	}

	@Override
	public Vector3 getPosition() {
		return getVector3(AL10.AL_POSITION);
	}

	@Override
	public void setPosition(Vector3 position) {
		setVector3(AL10.AL_POSITION, position);
	}

	@Override
	public Vector3 getVelocity() {
		return getVector3(AL10.AL_VELOCITY);
	}

	@Override
	public void setVelocity(Vector3 velocity) {
		setVector3(AL10.AL_VELOCITY, velocity);
	}

	@Override
	public Vector3 getDirection() {
		return getVector3(AL10.AL_DIRECTION);
	}

	@Override
	public void setDirection(Vector3 direction) {
		setVector3(AL10.AL_DIRECTION, direction);
	}

	private float getFloat(int property) {
		return AL10.alGetSourcef(sourceId, property);
	}

	private void setFloat(int property, float value) {
		AL10.alSourcef(sourceId, property, value);
		checkErrors();
	}

	private int getInt(int property) {
		return AL10.alGetSourcei(sourceId, property);
	}

	private void setInt(int property, int value) {
		AL10.alSourcei(sourceId, property, value);
		checkErrors();
	}

	private Vector3 getVector3(int property) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		AL10.alGetSource(sourceId, property, buffer);
		return new Vector3(buffer.get(0), buffer.get(1), buffer.get(2));
	}

	private void setVector3(int property, Vector3 value) {
		AL10.alSource3f(sourceId, property, value.getX(), value.getY(), value.getZ());
		checkErrors();
	}
}
