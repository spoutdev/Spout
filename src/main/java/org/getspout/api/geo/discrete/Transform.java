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
package org.getspout.api.geo.discrete;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

public class Transform {	
	Point position;
	Quaternion rotation;
	Vector3m scale;	
	
	Transform parent = null;
	
	public Transform() {
	}
	
	public Transform(Point position, Quaternion rotation, Vector3 scale) {
		this.position = new Point(position);
		this.rotation = new Quaternion(rotation);
		this.scale = new Vector3m(scale);
	}
	
	public Point getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position = position;
	}
	public Quaternion getRotation() {
		return rotation;
	}
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
	public Vector3m getScale() {
		return scale;
	}
	public void setScale(Vector3m scale) {
		this.scale = scale;
	}
	public Transform getParent() {
		return parent;
	}
	public void setParent(Transform parent) {
		this.parent = parent;
	}
	
	public Transform add(Transform t){
		Transform r = new Transform();
		r.position = position.add(t.getPosition());
		r.rotation = rotation.multiply(t.getRotation());
		r.scale = (Vector3m) scale.add(t.getScale());
		return r;
	}
	
	public Transform getAbsolutePosition(){
		if(parent == null) return this;
		return this.add(parent.getAbsolutePosition());
		
	}
	
	public Transform copy(){
		Transform t = new Transform();
		t.setPosition(new Point(this.position));
		t.setRotation(new Quaternion(this.rotation));
		t.setScale(new Vector3m(this.scale));
		return t;
	}
	
	public String toString() {
		return getClass().getSimpleName()+ "{" + position + ", "+ rotation + ", " + scale + "}";
	}
}
