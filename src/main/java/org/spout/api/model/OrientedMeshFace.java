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
package org.spout.api.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;

/**
 * Represents a Triangle for a model face
 * 
 */
public class OrientedMeshFace extends MeshFace {
	
	private Set<BlockFace> faces = new HashSet<BlockFace>();
	
	public OrientedMeshFace(Vertex v1, Vertex v2, Vertex v3) {
		super(v1,v2,v3);
		
		// Calculate two vectors from the three points
		Vector3 vector1 = verts[0].position.subtract(verts[1].position);
		Vector3 vector2 = verts[1].position.subtract(verts[2].position);

		// Take the cross product of the two vectors to get
		// the normal vector which will be stored in out

		Vector3 norm = vector1.cross(vector2).normalize();

		for (BlockFace b : BlockFace.values())
			if (norm.distance(b.getOffset()) < 1)
				faces.add(b);
	}
	
	public boolean canRender(Collection<BlockFace> shouldRender){
		for(BlockFace face : shouldRender)
			if(faces.contains(face))
				return true;
		return false;
	}
}
