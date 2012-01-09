/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.entity;

import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

public abstract class Controller {
	protected Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	
	/**
	 * Called when the entity dies.
	 * 
	 * Called just before the snapshotStart method.
	 */
	public void onDeath() {
	}
	/**
	 * 
	 * @param dt the number of seconds since last update
	 */
	public abstract void onTick(float dt);
	
	/**
	 * Called just before a snapshot update.  
	 * 
	 * This is intended purely as a monitor based step.  
	 * 
	 * NO updates should be made to the entity at this stage.
	 * 
	 * It can be used to send packets for network update.
	 */
	public void snapshotStart() {
	}
	
	/**
	 * Checks if this entity has moved this cycle.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasMoved() {
		Transform old = parent.getTransform();
		Transform current= parent.getLiveTransform();
		if (current == null) {
			return false;
		} else if (old == null) {
			return true;
		} else {
			return Math.abs(old.getPosition().getMahattanDistance(current.getPosition())) > 0.01D;
		}
	}
	
	/**
	 * Checks if this entity has moved farther than 128 blocks in this cycle.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasTeleported() {
		Transform old = parent.getTransform();
		Transform current= parent.getLiveTransform();
		if (current == null) {
			return false;
		} else if (old == null) {
			return true;
		} else {
			return Math.abs(old.getPosition().getMahattanDistance(current.getPosition())) > 128D;
		}
	}

	/**
	 * Checks if this entity has rotated this cycle.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasRotated() {
		Transform old = parent.getTransform();
		Transform current= parent.getLiveTransform();
		if (current == null) {
			return false;
		} else if (old == null) {
			return false;
		} else {
			Quaternion oldQ = old.getRotation();
			Quaternion currentQ = current.getRotation();
			Vector3 oldAngles = oldQ.getAxisAngles();
			Vector3 currentAngles = currentQ.getAxisAngles();
			return Math.abs(oldAngles.getX() - currentAngles.getX()) + Math.abs(oldAngles.getY() - currentAngles.getY()) + Math.abs(oldAngles.getZ() - currentAngles.getZ()) > 0.01D;
		}
	}
}
