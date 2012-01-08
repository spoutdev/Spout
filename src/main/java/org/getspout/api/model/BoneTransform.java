/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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
package org.getspout.api.model;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3;

public class BoneTransform {
	Vector3 position = Vector3.ZERO;
	Quaternion rotation = Quaternion.identity;
	Vector3 scale = Vector3.ONE;
	
	BoneTransform parent;

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public BoneTransform getParent() {
		return parent;
	}

	public void setParent(BoneTransform parent) {
		this.parent = parent;
	}
	
	private BoneTransform add(BoneTransform other){
		BoneTransform t = new BoneTransform();
		t.position = this.position.add(other.position);
		t.rotation = this.rotation.multiply(other.rotation);
		t.scale = this.scale.add(other.scale);
		return t;
	}
	
	public BoneTransform getAbsolutePosition(){
		if(parent == null) return this;
		return this.add(parent.getAbsolutePosition());
	}
	
}
