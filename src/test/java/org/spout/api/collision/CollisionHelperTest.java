/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spout.api.collision;

import org.spout.api.math.Vector3;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simplyianm
 */
public class CollisionHelperTest {
	public CollisionHelperTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_BoundingBox() {
		BoundingBox a = new BoundingBox(
			new Vector3(1, 1, 1),
			new Vector3(3, 3, 3));
		BoundingBox b = new BoundingBox(
			new Vector3(2, 2, 2),
			new Vector3(4, 4, 4));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check shared points

		a = new BoundingBox(
			new Vector3(4, 4, 4),
			new Vector3(6, 6, 6));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		a = new BoundingBox(
			new Vector3(6, 6, 6),
			new Vector3(10, 10, 10));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_BoundingSphere() {
		BoundingBox a = new BoundingBox(
			new Vector3(1, 1, 1),
			new Vector3(3, 3, 3));

		BoundingSphere b = new BoundingSphere(
			new Vector3(2, 2, 2),
			15);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new BoundingSphere(
			new Vector3(20, 20, 20),
			2);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_Segment() {
		BoundingBox a = new BoundingBox(
			new Vector3(-2, -2, -2),
			new Vector3(0, 0, 0));

		Segment b = new Segment(
			new Vector3(-1, 5, -1),
			new Vector3(-1, -5, -1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check shared endpoint

		b = new Segment(
			new Vector3(0, 0, 0),
			new Vector3(2, 2, 2));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check outside

		b = new Segment(
			new Vector3(1, 1, 1),
			new Vector3(2, 2, 2));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_Plane() {
		BoundingBox a = new BoundingBox(
			new Vector3(-1, -1, -1),
			new Vector3(1, 1, 1));

		Plane b = new Plane(
			new Vector3(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check slanted plane

		b = new Plane(
			new Vector3(0, 0, 0),
			new Vector3(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check non intersecting

		b = new Plane(
			new Vector3(-2, -2, -2),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_BoundingSphere() {
		
		BoundingSphere a = new BoundingSphere(
			new Vector3(0, 0, 0),
			1);

		BoundingSphere b = new BoundingSphere(
			new Vector3(0, 1, 0),
			1);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check overlapping edges

		b = new BoundingSphere(
			new Vector3(0, 2, 0),
			1);

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new BoundingSphere(
			new Vector3(0, 3, 0),
			1);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Segment() {
		BoundingSphere a = new BoundingSphere(
			new Vector3(1, 1, 1),
			1);

		Segment b = new Segment(
			new Vector3(1, 2, 1),
			new Vector3(1, 0, 1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check tangent

		b = new Segment(
			new Vector3(-1, 0, 1),
			new Vector3(1, 0, 1));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new Segment(
			new Vector3(20, 20, 20),
			new Vector3(21, 21, 21));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Ray() {
		BoundingSphere a = new BoundingSphere(
			new Vector3(0, 0, 0),
			10);

		Ray b = new Ray(
			new Vector3(0, 10, 0),
			Vector3.Up.scale(-1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Start within the sphere

		b = new Ray(
			new Vector3(0, 0, 0),
			new Vector3(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Not colliding

		b = new Ray(
			new Vector3(20, 20, 20),
			new Vector3(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Plane() {
		//Edge collision

		BoundingSphere a = new BoundingSphere(
			new Vector3(1, 1, 1),
			1);

		Plane b = new Plane(
			new Vector3(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non collision

		b = new Plane(
			new Vector3(-1, -1, -1),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Segment_Segment() {
			Segment a = new Segment(
			new Vector3(0, 0, 0),
			new Vector3(0, 10, 0));

		Segment b = new Segment(
			new Vector3(-5, 5, 0),
			new Vector3(5, 5, 0));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non collision

		b = new Segment(
			new Vector3(-10, -10, -10),
			new Vector3(-20, -20, -20));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Segment_Plane() {
		Segment a = new Segment(
			new Vector3(0, -1, 0),
			new Vector3(0, 1, 0));

		Plane b = new Plane(
			new Vector3(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non intersection

		b = new Plane(
			new Vector3(0, 2, 0),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Plane_Plane() {
		Plane a = new Plane(
			new Vector3(0, 0, 0),
			Vector3.Up);

		Plane b = new Plane(
			new Vector3(1, 1, 1),
			Vector3.Right);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Not intersecting (parallel)

		b = new Plane(
			new Vector3(1, 1, 1),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

}
