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
package org.spout.api.model.animation;

import org.spout.api.math.Vector3;

public class BoneTransform {

	private Vector3 head;
	private Vector3 tail;

	public BoneTransform(Vector3 head, Vector3 tail){
		this.setHead(head);
		this.setTail(tail);
	}

	public Vector3 getHead() {
		return head;
	}

	public void setHead(Vector3 head) {
		this.head = head;
	}

	public Vector3 getTail() {
		return tail;
	}

	public void setTail(Vector3 tail) {
		this.tail = tail;
	}

	public String toString(){
		return "Head : " + head.getX() + " / " + head.getY() + " / " + head.getZ() +
				" Tail " + tail.getX() + " / " + tail.getY() + " / " + tail.getZ();
	}
}
