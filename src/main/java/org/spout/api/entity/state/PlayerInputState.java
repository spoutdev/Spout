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

/**
 * Represents the current player input state
 */
public class PlayerInputState {
	public static final int FORWARD = 0x01;
	public static final int BACKWARD = 0x02;
	public static final int LEFT = 0x04;
	public static final int RIGHT = 0x08;
	public static final int JUMP = 0x10;
	public static final int CROUCH = 0x20;
	public static final int SELECTUP = 0x40;
	public static final int SELECTDOWN = 0x80;
	public static final int FIRE1 = 0x0100;
	public static final int FIRE2 = 0x0200;
	public static final int INTERACT = 0400;
	short userCommands = 0;
	byte mouse_dx;
	byte mouse_dy;

	public PlayerInputState(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean crouch, boolean selectUp, boolean selectDown, boolean fire1, boolean fire2, boolean interact, byte mdx, byte mdy) {
		userCommands = 0;
		userCommands |= (forward ? FORWARD : 0);
		userCommands |= (backward ? BACKWARD : 0);
		userCommands |= (left ? LEFT : 0);
		userCommands |= (right ? RIGHT : 0);
		userCommands |= (jump ? JUMP : 0);
		userCommands |= (crouch ? CROUCH : 0);
		userCommands |= (selectUp ? SELECTUP : 0);
		userCommands |= (selectDown ? SELECTDOWN : 0);
		userCommands |= (fire1 ? FIRE1 : 0);
		userCommands |= (fire2 ? FIRE2 : 0);
		userCommands |= (interact ? INTERACT : 0);
		mouse_dx = mdx;
		mouse_dy = mdy;
	}

	public PlayerInputState(short userCommands, byte mdx, byte mdy) {
		this.userCommands = userCommands;
		this.mouse_dx = mdx;
		this.mouse_dy = mdy;
	}

	public boolean getForward() {
		return (userCommands & FORWARD) == 1;
	}

	public boolean getBackward() {
		return (userCommands & BACKWARD) == 1;
	}

	public boolean getRight() {
		return (userCommands & RIGHT) == 1;
	}

	public boolean getLeft() {
		return (userCommands & LEFT) == 1;
	}

	public boolean getJump() {
		return (userCommands & JUMP) == 1;
	}

	public boolean getCrouch() {
		return (userCommands & CROUCH) == 1;
	}

	public boolean getSelectUp() {
		return (userCommands & SELECTUP) == 1;
	}

	public boolean getSelectDown() {
		return (userCommands & SELECTDOWN) == 1;
	}

	public boolean getFire1() {
		return (userCommands & FIRE1) == 1;
	}

	public boolean getFire2() {
		return (userCommands & FIRE2) == 1;
	}

	public boolean getInteract() {
		return (userCommands & INTERACT) == 1;
	}
}
