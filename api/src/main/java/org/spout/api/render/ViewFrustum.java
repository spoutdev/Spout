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
package org.spout.api.render;

import org.spout.api.geo.cuboid.Cuboid;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;

public class ViewFrustum {
	// This is the renderer subsize in bloc scale ( because it is in aggregator scale when it's pass through intersects )
	Vector3 rendererSize = new Vector3(2, 2, 2);
	Vector3 position = null;
	float[][] frustum = new float[6][4];

	public void update(Matrix4 projection, Matrix4 view, Vector3 paramPosition) {

		position = paramPosition;

		// http://www.crownandcutlass.com/features/technicaldetails/frustum.html
		float[] clip = view.mul(projection).toArray(true);

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clip[3] - clip[0];
		frustum[0][1] = clip[7] - clip[4];
		frustum[0][2] = clip[11] - clip[8];
		frustum[0][3] = clip[15] - clip[12];

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clip[3] + clip[0];
		frustum[1][1] = clip[7] + clip[4];
		frustum[1][2] = clip[11] + clip[8];
		frustum[1][3] = clip[15] + clip[12];

		/* Extract the BOTTOM plane */
		frustum[2][0] = clip[3] + clip[1];
		frustum[2][1] = clip[7] + clip[5];
		frustum[2][2] = clip[11] + clip[9];
		frustum[2][3] = clip[15] + clip[13];

		/* Extract the TOP plane */
		frustum[3][0] = clip[3] - clip[1];
		frustum[3][1] = clip[7] - clip[5];
		frustum[3][2] = clip[11] - clip[9];
		frustum[3][3] = clip[15] - clip[13];

		/* Extract the FAR plane */
		frustum[4][0] = clip[3] - clip[2];
		frustum[4][1] = clip[7] - clip[6];
		frustum[4][2] = clip[11] - clip[10];
		frustum[4][3] = clip[15] - clip[14];

		/* Extract the NEAR plane */
		frustum[5][0] = clip[3] + clip[2];
		frustum[5][1] = clip[7] + clip[6];
		frustum[5][2] = clip[11] + clip[10];
		frustum[5][3] = clip[15] + clip[14];
		
		
		
		/* Normalize the result */
		/* You can eliminate all the code that has to
		 * do with normalizing the plane values. This will result in scaled
		 * distances when you compare a point to a plane. The point and box
		 * tests will still work, but the sphere test won't. If you aren't
		 * using bounding spheres at all this will save a few expensive
		 * calculations per frame, but those probably won't be an issue on
		 * most systems.*/
		
		/*for (int i=0 ; i<6 ; i++) {
			double t = sqrt(frustum[i][0] * frustum[i][0] + frustum[i][1] * frustum[i][1] + frustum[i][2] * frustum[i][2]);
			frustum[i][0] /= t;
			frustum[i][1] /= t;
			frustum[i][2] /= t;
			frustum[i][3] /= t;
		}*/

	}

	/**
	 * Compute the distance between a point and the given plane
	 *
	 * @param p The id of the plane
	 * @param v The vector
	 * @return The distance
	 */
	private float distance(int p, Vector3 v) {
		return frustum[p][0] * v.getX() + frustum[p][1] * v.getY() + frustum[p][2] * v.getZ() + frustum[p][3];
	}

	/**
	 * Checks if the frustum of this camera intersects the given Cuboid.
	 *
	 * @param c The cuboid to check the frustum against.
	 * @return True if the frustum intersects the cuboid.
	 */
	public boolean intersects(Cuboid c) {

		Vector3[] vertices = c.getVertices();

		for (int i = 0; i < 6; i++) {

			if (distance(i, vertices[0].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[1].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[2].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[3].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[4].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[5].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[6].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[7].mul(rendererSize).sub(position)) > 0) {
				continue;
			}

			return false;
		}

		return true;
	}

	/**
	 * Checks if the frustum contains the given Vector3.
	 *
	 * @param vec The Vector3 to check the frustum against.
	 * @return True if the frustum contains the Vector3.
	 */
	public boolean contains(Vector3 vec) {
		for (int p = 0; p < 6; p++) {
			if (distance(p, vec) <= 0) {
				return false;
			}
		}
		return true;
	}
}
