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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;

/**
 * Represents a Triangle for a model face
 * 
 */
public class OrientedMeshFace extends MeshFace {
	
	public final static List<BlockFace> shouldRender = new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.BOTTOM,BlockFace.NORTH,BlockFace.SOUTH,BlockFace.WEST,BlockFace.EAST));

	private final static Map<Vector3,List<BlockFace>> faceMap = new HashMap<Vector3,List<BlockFace>>();
	
	static{
		/*   1--2
		 *  /| /|
		 * 5--6 |   
		 * | 0|-3    Y - Bottom < TOP
		 * |/ |/     |
		 * 4--7      O-- X - North < SOUTH
		 *          /
		 *         Z - East < WEST
		 */
		faceMap.put(new Vector3(-1, -1, -1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.BOTTOM,BlockFace.NORTH,BlockFace.EAST)));
		faceMap.put(new Vector3(-1, 1, -1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.NORTH,BlockFace.EAST)));
		faceMap.put(new Vector3(1, 1, -1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.SOUTH,BlockFace.EAST)));
		faceMap.put(new Vector3(1, -1, -1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.BOTTOM,BlockFace.SOUTH,BlockFace.EAST)));
		faceMap.put(new Vector3(-1, -1, 1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.BOTTOM,BlockFace.NORTH,BlockFace.WEST)));
		faceMap.put(new Vector3(-1, 1, 1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.NORTH,BlockFace.WEST)));
		faceMap.put(new Vector3(1, 1, 1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.SOUTH,BlockFace.WEST)));
		faceMap.put(new Vector3(1, -1, 1).normalize(), new ArrayList<BlockFace>(Arrays.asList(BlockFace.BOTTOM,BlockFace.SOUTH,BlockFace.WEST)));
	}

	private int requireFace = -1;
	private Set<BlockFace> seeFromFace = new HashSet<BlockFace>();
	
	public OrientedMeshFace(Vertex v1, Vertex v2, Vertex v3) {
		super(v1,v2,v3);

		// Calculate two vectors from the three points
		Vector3 vector1 = verts[0].position.subtract(verts[1].position);
		Vector3 vector2 = verts[1].position.subtract(verts[2].position);

		// Take the cross product of the two vectors to get
		// the normal vector which will be stored in out

		Vector3 norm = vector1.cross(vector2).normalize();
		
		//Make the list of face can see this face
		for(Entry<Vector3, List<BlockFace>> entry : faceMap.entrySet()){
			if (norm.distance(entry.getKey()) < 1)
				seeFromFace.addAll(entry.getValue());
		}
		
		
		// Get min max abs xyz of the face
		Vector3 min = new Vector3(Math.abs(Math.min(verts[0].position.getX(), Math.min(verts[1].position.getX(), verts[2].position.getX()))),
				Math.abs(Math.min(verts[0].position.getY(), Math.min(verts[1].position.getY(), verts[2].position.getY()))),
				Math.abs(Math.min(verts[0].position.getZ(), Math.min(verts[1].position.getZ(), verts[2].position.getZ()))));
		Vector3 max = new Vector3(Math.abs(Math.max(verts[0].position.getX(), Math.max(verts[1].position.getX(), verts[2].position.getX()))),
				Math.abs(Math.max(verts[0].position.getY(), Math.max(verts[1].position.getY(), verts[2].position.getY()))),
				Math.abs(Math.max(verts[0].position.getZ(), Math.max(verts[1].position.getZ(), verts[2].position.getZ()))));
		
		//Get the required face not ocluded, a blockface is required if the face is a face on the limit of the cube
		for(int i = 0; i < shouldRender.size(); i++){
			BlockFace face = shouldRender.get(i);
			if( min.getX() >= Math.abs(face.getOffset().getX()) && max.getX() >= Math.abs(face.getOffset().getX()) &&
					min.getY() >= Math.abs(face.getOffset().getY()) && max.getY() >= Math.abs(face.getOffset().getY()) &&
					min.getZ() >= Math.abs(face.getOffset().getZ()) && max.getZ() >= Math.abs(face.getOffset().getZ()) ){
				requireFace = i;
				break;
			}
		}
	}

	public boolean canRender(boolean toRender[],BlockFace face){
		
		//If the required blockface is ocluded, don't render
		if(requireFace != -1 && !toRender[requireFace])
			return false;
		
		/**
		 * For each face :
		 * - Look if a face is see by a block face and this block face isn't occluded
		 *   - If this face is the face what you want to draw, send yes
		 *   - If it's not the face that you want to drawn, send no, because this face will be rended by a other blockface
		 */
		for(int i = 0; i < shouldRender.size(); i++){
			if(seeFromFace.contains(shouldRender.get(i)) && toRender[i]){
				if(shouldRender.get(i) == face)
					return true;
				return false;
			}
		}
		return false;
	}
}
