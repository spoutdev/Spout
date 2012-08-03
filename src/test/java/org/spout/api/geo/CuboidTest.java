package org.spout.api.geo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

public class CuboidTest {
	private World world;
	private Cuboid instance;
	
	@Before
	public void setup() {
		world = mock(World.class);
		instance = new Cuboid(new Point(world, 0, 0, 0), Vector3.ONE.multiply(16.0));
	}
	
	@After
	public void tearDown() {
		world = null;
		instance = null;
	}
	
	@Test
	public void testContains() {
		// test out
		Vector3 vec = new Vector3(-1, -0.5, -1);
		assertFalse(instance.contains(vec));
		
		// test in
		vec = new Vector3(1, 1, 1);
		assertTrue(instance.contains(vec));
		
		// Exclusive max
		vec = new Vector3(16, 16, 16);
		assertFalse(instance.contains(vec));
		
		// Exclusive max
		vec = new Vector3(16, 14, 5);
		assertFalse(instance.contains(vec));
		
		// Inclusive min
		vec = new Vector3(0, 0, 0);
		assertTrue(instance.contains(vec));
		
		// Inclusive min
		vec = new Vector3(0, 14, 5);
		assertTrue(instance.contains(vec));
	}
}
