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
package org.spout.api.model.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;

/**
 * Represents a Triangle for a model face
 */
public class OrientedMeshFace extends MeshFace {
	public final static BlockFace[] shouldRender = new BlockFace[] {BlockFace.TOP, BlockFace.BOTTOM, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
	private final static Map<BlockFace, List<Vector3>> faceMap = new HashMap<>();
	private static final long serialVersionUID = 1L;

	static {
		/*   1--2
		 *  /| /|
		 * 5--6 |   
		 * | 0|-3    Y - Bottom < TOP
		 * |/ |/     |
		 * 4--7      O-- X - North < SOUTH
		 *          /
		 *         Z - East < WEST
		 */

		//TODO : extract vector in variable

		faceMap.put(BlockFace.TOP, new ArrayList<>(Arrays.asList(new Vector3(-1, 1, -1).normalize(), new Vector3(1, 1, -1).normalize(), new Vector3(-1, 1, 1).normalize(), new Vector3(1, 1, 1).normalize())));
		faceMap.put(BlockFace.BOTTOM, new ArrayList<>(Arrays.asList(new Vector3(-1, -1, -1).normalize(), new Vector3(1, -1, -1).normalize(), new Vector3(-1, -1, 1).normalize(), new Vector3(1, -1, 1).normalize())));
		faceMap.put(BlockFace.NORTH, new ArrayList<>(Arrays.asList(new Vector3(-1, -1, -1).normalize(), new Vector3(-1, 1, -1).normalize(), new Vector3(-1, 1, 1).normalize(), new Vector3(-1, -1, 1).normalize())));
		faceMap.put(BlockFace.SOUTH, new ArrayList<>(Arrays.asList(new Vector3(1, -1, -1).normalize(), new Vector3(1, -1, 1).normalize(), new Vector3(1, 1, -1).normalize(), new Vector3(1, 1, 1).normalize())));
		faceMap.put(BlockFace.WEST, new ArrayList<>(Arrays.asList(new Vector3(-1, 1, 1).normalize(), new Vector3(-1, -1, 1).normalize(), new Vector3(1, -1, 1).normalize(), new Vector3(1, 1, 1).normalize())));
		faceMap.put(BlockFace.EAST, new ArrayList<>(Arrays.asList(new Vector3(-1, -1, -1).normalize(), new Vector3(-1, 1, -1).normalize(), new Vector3(1, -1, -1).normalize(), new Vector3(1, 1, -1).normalize())));
	}

	private boolean[] seeFromFace = new boolean[shouldRender.length];

	public OrientedMeshFace(Vertex v1, Vertex v2, Vertex v3) {
		super(v1, v2, v3);

		// Calculate two vectors from the three points

		Vector3 vector1 = verts[0].position.subtract(verts[1].position);
		Vector3 vector2 = verts[1].position.subtract(verts[2].position);

		// Take the cross product of the two vectors to get
		// the normal vector which will be stored in out

		Vector3 norm = vector1.cross(vector2).normalize();

		for (int i = 0; i < shouldRender.length; i++) {
			for (Vector3 edge : faceMap.get(shouldRender[i])) {
				if (norm.distance(edge) < 1) {
					seeFromFace[i] = true;
				}
			}
		}
	}

	public OrientedMeshFace(Vertex v1, Vertex v2, Vertex v3, Set<BlockFace> requiredFace) {
		super(v1, v2, v3);

		for (int i = 0; i < shouldRender.length; i++) {
			seeFromFace[i] = requiredFace.contains(shouldRender[i]);
		}
	}

	public boolean canRender(boolean toRender[]) {
		/**
		 * For each face :
		 * - Look if a face is see by a block face and this block face isn't occluded
		 *   - If this face is the face what you want to draw, send yes
		 *   - If it's not the face that you want to drawn, send no, because this face will be rended by a other blockface
		 */
		for (int i = 0; i < shouldRender.length; i++) {
			if (seeFromFace[i] && toRender[i]) {
				return true;
			}
		}
		return false;
	}
}
