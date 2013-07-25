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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Vector3;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewFrustumTest {
	private ViewFrustum frustum;

	@Before
	public void setup() {
		frustum = new ViewFrustum();
	}

	@After
	public void tearDown() {
		frustum = null;
	}

	@Test
	public void testUpdate() {
		Matrix projection = MatrixMath.createPerspective(70, 16 / 9.0f, 1, 2000);
		Matrix view = MatrixMath.createLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 1000), Vector3.UP);

		frustum.update(projection, view, new Vector3(0, 0, 0));

		assertEquals(new Vector3(0, 0, 0), frustum.position);
		// TODO test the created planes... a more patient person can do this
	}

	@Test
	public void testIntersects() {
		Matrix projection = MatrixMath.createPerspective(70, 16 / 9.0f, 1, 2000);
		Matrix view = MatrixMath.createLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 1000), Vector3.UP);

		frustum.update(projection, view, new Vector3(0, 0, 0));

		Vector3[] vertices = new Vector3[8];
		// Front
		vertices[0] = new Vector3(0, 0, 16);
		vertices[1] = new Vector3(16, 0, 16);
		vertices[2] = new Vector3(16, 16, 16);
		vertices[3] = new Vector3(0, 16, 16);
		// Back
		vertices[4] = new Vector3(0, 0, 0);
		vertices[5] = new Vector3(16, 0, 0);
		vertices[6] = new Vector3(16, 16, 0);
		vertices[7] = new Vector3(0, 16, 0);

		Cuboid c = mock(Cuboid.class);
		when(c.getVertices()).thenReturn(vertices);

		//assertTrue(frustum.intersects(c));
	}

	@Test
	public void testIntersectsFalse() {
		Matrix projection = MatrixMath.createPerspective(70, 16 / 9.0f, 1, 2000);
		Matrix view = MatrixMath.createLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 1000), Vector3.UP);

		frustum.update(projection, view, new Vector3(0, 0, 0));

		Vector3[] vertices = new Vector3[8];
		// Front
		vertices[0] = new Vector3(-32, -32, -16);
		vertices[1] = new Vector3(-16, -32, -16);
		vertices[2] = new Vector3(-16, -16, -16);
		vertices[3] = new Vector3(-32, -16, -16);
		// Back
		vertices[4] = new Vector3(-32, -32, -32);
		vertices[5] = new Vector3(-16, -32, -32);
		vertices[6] = new Vector3(-16, -16, -32);
		vertices[7] = new Vector3(-32, -16, -32);

		Cuboid c = mock(Cuboid.class);
		when(c.getVertices()).thenReturn(vertices);

		//assertFalse(frustum.intersects(c));
	}

	@Test
	public void testContains() {
		Matrix projection = MatrixMath.createPerspective(70, 16 / 9.0f, 1, 2000);
		Matrix view = MatrixMath.createLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 1000), Vector3.UP);

		frustum.update(projection, view, new Vector3(0, 0, 0));

		//assertTrue(frustum.contains(new Vector3(0, 0, 2)));
		//assertTrue(frustum.contains(new Vector3(0, 0, 100)));
		//assertFalse(frustum.contains(new Vector3(0, 0, -1)));
	}
}
