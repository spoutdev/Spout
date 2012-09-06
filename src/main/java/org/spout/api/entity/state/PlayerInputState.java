/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.entity.state;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.math.MathHelper;

/**
 * Represents the current player input state
 */
public class PlayerInputState {
	public static final float MOUSE_SENSITIVITY = 0.05f;
	private static final AtomicInteger FLAG_COUNTER = new AtomicInteger(0);

	public static enum Flags {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		JUMP,
		CROUCH,
		SELECT_UP,
		SELECT_DOWN,
		FIRE_1,
		FIRE_2,
		INTERACT;
		private final short bitValue;

		private Flags() {
			int shifts = FLAG_COUNTER.getAndIncrement();
			if (shifts >= 16) {
				throw new IllegalStateException("Input flag" + name() + " exceeded bit count of short!");
			}
			this.bitValue = (short) (1 << shifts);
		}

		public short getBitFlag() {
			return bitValue;
		}

		public boolean has(short bitfield) {
			return (bitfield & bitValue) != 0;
		}
	}

	public static final PlayerInputState DEFAULT_STATE = new PlayerInputState((short) 0, (byte) 0, (byte) 0);
	private final short userCommands;
	private final float pitch;
	private final float yaw;

	public PlayerInputState(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean crouch, boolean selectUp, boolean selectDown, boolean fire1, boolean fire2, boolean interact, float pitch, float yaw) {
		short userCommands = 0;
		userCommands |= (forward ? Flags.FORWARD.getBitFlag() : 0);
		userCommands |= (backward ? Flags.BACKWARD.getBitFlag() : 0);
		userCommands |= (left ? Flags.LEFT.getBitFlag() : 0);
		userCommands |= (right ? Flags.RIGHT.getBitFlag() : 0);
		userCommands |= (jump ? Flags.JUMP.getBitFlag() : 0);
		userCommands |= (crouch ? Flags.CROUCH.getBitFlag() : 0);
		userCommands |= (selectUp ? Flags.SELECT_UP.getBitFlag() : 0);
		userCommands |= (selectDown ? Flags.SELECT_DOWN.getBitFlag() : 0);
		userCommands |= (fire1 ? Flags.FIRE_1.getBitFlag() : 0);
		userCommands |= (fire2 ? Flags.FIRE_2.getBitFlag() : 0);
		userCommands |= (interact ? Flags.INTERACT.getBitFlag() : 0);
		this.userCommands = userCommands;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public PlayerInputState(short userCommands, float pitch, float yaw) {
		this.userCommands = userCommands;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public boolean getForward() {
		return Flags.FORWARD.has(userCommands);
	}

	public boolean getBackward() {
		return Flags.BACKWARD.has(userCommands);
	}

	public boolean getRight() {
		return Flags.RIGHT.has(userCommands);
	}

	public boolean getLeft() {
		return Flags.LEFT.has(userCommands);
	}

	public boolean getJump() {
		return Flags.JUMP.has(userCommands);
	}

	public boolean getCrouch() {
		return Flags.CROUCH.has(userCommands);
	}

	public boolean getSelectUp() {
		return Flags.SELECT_UP.has(userCommands);
	}

	public boolean getSelectDown() {
		return Flags.SELECT_DOWN.has(userCommands);
	}

	public boolean getFire1() {
		return Flags.FIRE_1.has(userCommands);
	}

	public boolean getFire2() {
		return Flags.FIRE_2.has(userCommands);
	}

	public boolean getInteract() {
		return Flags.INTERACT.has(userCommands);
	}

	public float pitch() {
		return pitch;
	}

	public float yaw() {
		return yaw;
	}

	public PlayerInputState withAddedFlag(Flags flag) {
		return new PlayerInputState((short) (userCommands | flag.getBitFlag()), pitch, yaw);
	}

	public PlayerInputState withRemovedFlag(Flags flag) {
		return new PlayerInputState((short) (userCommands & ~flag.getBitFlag()), pitch, yaw);
	}

	public PlayerInputState withAddedPitch(float pitch) {
		return new PlayerInputState(userCommands, MathHelper.wrapAngle(pitch + this.pitch), yaw);
	}

	public PlayerInputState withAddedYaw(float yaw) {
		return new PlayerInputState(userCommands, pitch, MathHelper.wrapAngle(yaw + this.yaw));
	}

	public Set<Flags> getFlagSet() {
		Set<Flags> flags = EnumSet.noneOf(Flags.class);
		for (Flags flag : Flags.values()) {
			if (flag.has(userCommands)) {
				flags.add(flag);
			}
		}
		return flags;
	}
}
