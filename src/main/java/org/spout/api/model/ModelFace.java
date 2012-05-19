/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
/**
 * Represents a Triangle for a model face
 * 
 */
public class ModelFace {
	PositionNormalTexture v1, v2, v3;
	
	
	
	public ModelFace(PositionNormalTexture v1, PositionNormalTexture v2, PositionNormalTexture v3 ){
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	/**
	 * Recalculates the normals for this triangle.  All points must be 0'd before this.
	 */
	protected void doRecalculateNormals(){
		Vector3 trinormal = MathHelper.cross(v1.position.subtract(v2.position), v2.position.subtract(v3.position)).normalize();
		v1.normal = v1.normal.add(trinormal).normalize();
		v2.normal = v2.normal.add(trinormal).normalize();
		v3.normal = v3.normal.add(trinormal).normalize();
		
	}
	
	Vector3[] getPositions(){
		return new Vector3[] {v1.position, v2.position, v3.position };		
	}
	
	Vector3[] getNormals(){
		return new Vector3[] {v1.normal, v2.normal, v3.normal };		
	}
	
	Vector2[] getUVs(){
		return new Vector2[] { v1.uv, v2.uv, v3.uv };
	}
	
}
