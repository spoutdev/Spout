package org.spout.api.render;

import static org.spout.api.math.MathHelper.sqrt;

import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector3;

public class ViewFrustum {
	Vector3 position = null;
	float[][] frustum = new float[6][4];

	public void update(Matrix projection, Matrix view) {
		// http://www.crownandcutlass.com/features/technicaldetails/frustum.html
		double t;
		float[] clip = view.multiply(projection).toArray();

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clip[3] - clip[0];
		frustum[0][1] = clip[7] - clip[4];
		frustum[0][2] = clip[11] - clip[8];
		frustum[0][3] = clip[15] - clip[12];

		/* Normalize the result */
		t = sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]);
		frustum[0][0] /= t;
		frustum[0][1] /= t;
		frustum[0][2] /= t;
		frustum[0][3] /= t;

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clip[3] + clip[0];
		frustum[1][1] = clip[7] + clip[4];
		frustum[1][2] = clip[11] + clip[8];
		frustum[1][3] = clip[15] + clip[12];

		/* Normalize the result */
		t = sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]);
		frustum[1][0] /= t;
		frustum[1][1] /= t;
		frustum[1][2] /= t;
		frustum[1][3] /= t;

		/* Extract the BOTTOM plane */
		frustum[2][0] = clip[3] + clip[1];
		frustum[2][1] = clip[7] + clip[5];
		frustum[2][2] = clip[11] + clip[9];
		frustum[2][3] = clip[15] + clip[13];

		/* Normalize the result */
		t = sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]);
		frustum[2][0] /= t;
		frustum[2][1] /= t;
		frustum[2][2] /= t;
		frustum[2][3] /= t;

		/* Extract the TOP plane */
		frustum[3][0] = clip[3] - clip[1];
		frustum[3][1] = clip[7] - clip[5];
		frustum[3][2] = clip[11] - clip[9];
		frustum[3][3] = clip[15] - clip[13];

		/* Normalize the result */
		t = sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]);
		frustum[3][0] /= t;
		frustum[3][1] /= t;
		frustum[3][2] /= t;
		frustum[3][3] /= t;

		/* Extract the FAR plane */
		frustum[4][0] = clip[3] - clip[2];
		frustum[4][1] = clip[7] - clip[6];
		frustum[4][2] = clip[11] - clip[10];
		frustum[4][3] = clip[15] - clip[14];

		/* Normalize the result */
		t = sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]);
		frustum[4][0] /= t;
		frustum[4][1] /= t;
		frustum[4][2] /= t;
		frustum[4][3] /= t;

		/* Extract the NEAR plane */
		frustum[5][0] = clip[3] + clip[2];
		frustum[5][1] = clip[7] + clip[6];
		frustum[5][2] = clip[11] + clip[10];
		frustum[5][3] = clip[15] + clip[14];

		position = new Vector3(view.get(0, 3), view.get(1, 3), view.get(2, 3));
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
			if (frustum[i][0] * (vertices[0].getX() - position.getX()) + frustum[i][1] * (vertices[0].getY() - position.getY()) + frustum[i][2] * (vertices[0].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[1].getX() - position.getX()) + frustum[i][1] * (vertices[1].getY() - position.getY()) + frustum[i][2] * (vertices[1].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[2].getX() - position.getX()) + frustum[i][1] * (vertices[2].getY() - position.getY()) + frustum[i][2] * (vertices[2].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[3].getX() - position.getX()) + frustum[i][1] * (vertices[3].getY() - position.getY()) + frustum[i][2] * (vertices[3].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[4].getX() - position.getX()) + frustum[i][1] * (vertices[4].getY() - position.getY()) + frustum[i][2] * (vertices[4].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[5].getX() - position.getX()) + frustum[i][1] * (vertices[5].getY() - position.getY()) + frustum[i][2] * (vertices[5].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[6].getX() - position.getX()) + frustum[i][1] * (vertices[6].getY() - position.getY()) + frustum[i][2] * (vertices[6].getZ() - position.getZ()) + frustum[i][3] > 0) {
				continue;
			}

			if (frustum[i][0] * (vertices[7].getX() - position.getX()) + frustum[i][1] * (vertices[7].getY() - position.getY()) + frustum[i][2] * (vertices[7].getZ() - position.getZ()) + frustum[i][3] > 0) {
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
			if (frustum[p][0] * vec.getX() + frustum[p][1] * vec.getY() + frustum[p][2] * vec.getZ() + frustum[p][3] <= 0) {
				return false;
			}
		}
		return true;
	}
}
