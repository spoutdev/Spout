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
package org.spout.api.player;

import org.spout.api.math.MathHelper;

/**
 *  Represents the current player input state
 *
 */
public class PlayerInputState {
	
	static final int NUM_AXIS = 6;
	
	static final float MAX_AXIS = 1.f;
	static final float MIN_AXIS = -1.f;
	
	
	float[] axis = new float[NUM_AXIS];
	/**
	 * Gets the current depression of the forward buttons.
	 * 1.0 reprensents full force forward
	 * -1.0 represents full force backward
	 * @return
	 */
	public float getForward() {
		return axis[0];
	}
	
	public float getHorizantal() {
		return axis[1];
	}
	
	public float getJump() {
		return axis[2];
	}
	
	public float getLookX() {
		return axis[3];
	}
	
	public float getLookY() {
		return axis[4];
	}
	
	public float getSprint() {
		return axis[5];
	}
	
	public void setForward(float value) {
		axis[0] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setHorizantal(float value) {
		axis[1] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setJump(float value) {
		axis[2] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setLookX(float value) {
		axis[3] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setLookY(float value) {
		axis[4] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setSprint(float value) {
		axis[5] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}

}
