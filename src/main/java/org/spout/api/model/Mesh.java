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

import org.spout.api.math.Vector3;

public class Mesh {
	static final PositionNormalTexture cubeVerts[] = new PositionNormalTexture[] {
		new PositionNormalTexture(Vector3.ZERO), new PositionNormalTexture(Vector3.UNIT_Y), new PositionNormalTexture(new Vector3(0,1,1)), new PositionNormalTexture(Vector3.UNIT_Z),
		new PositionNormalTexture(Vector3.UNIT_X), new PositionNormalTexture(new Vector3(1,1,0)), new PositionNormalTexture(Vector3.ONE), new PositionNormalTexture(new Vector3(1, 0, 1))
	};

	
	
	ModelFace[] faces;
	
	
	protected Mesh(ModelFace[] faces) {
		this.faces = faces;
	}
	
	
	public static Mesh createCubeMesh() {
		return createCubeMesh(Vector3.ONE);
	}
	
	public static Mesh createCubeMesh(Vector3 scale) {

		ModelFace[] faces = new ModelFace[] {
				new ModelFace(cubeVerts[0], cubeVerts[1], cubeVerts[2]),
				new ModelFace(cubeVerts[3], cubeVerts[1], cubeVerts[4]),
				
				new ModelFace(cubeVerts[7], cubeVerts[6], cubeVerts[5]),
				new ModelFace(cubeVerts[6], cubeVerts[7], cubeVerts[4]),
				
				new ModelFace(cubeVerts[3], cubeVerts[2], cubeVerts[6]),
				new ModelFace(cubeVerts[6], cubeVerts[3], cubeVerts[7]),
				
				new ModelFace(cubeVerts[4], cubeVerts[5], cubeVerts[1]),
				new ModelFace(cubeVerts[1], cubeVerts[4], cubeVerts[0]),
				
				new ModelFace(cubeVerts[1], cubeVerts[5], cubeVerts[6]),
				new ModelFace(cubeVerts[6], cubeVerts[1], cubeVerts[2]),
				
				new ModelFace(cubeVerts[4], cubeVerts[0], cubeVerts[3]),
				new ModelFace(cubeVerts[3], cubeVerts[4], cubeVerts[7]),
		};
		
		for (ModelFace face : faces) {
			face.doRecalculateNormals();
		}
		
		return new Mesh(faces);
		
	}
}
