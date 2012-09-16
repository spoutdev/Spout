package org.spout.api.geo.discrete;

import static org.junit.Assert.*;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.spout.api.geo.World;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

public class TransformTest {

	@Test
	public void test() {
		World mock = PowerMockito.mock(World.class);
		Point p = new Point(mock, 0, 0, 0);
		Quaternion q = new Quaternion(1, 0, 0, 0);
		Vector3 s = new Vector3(0, 0, 0);
		Transform transform = new Transform(p, q, s);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		assertEquals("Transform scale values did not match", transform.getScale(), s);
		
		p = p.add(1, 5, -3);
		transform.setPosition(p);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		
		q = q.rotate(45F,  0, 1, 0);
		transform.setRotation(q);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		
		s = s.add(-3, 5, -13);
		transform.setScale(s);
		assertEquals("Transform scale values did not match", transform.getScale(), s);
		
		p = p.multiply(5);
		q = q.rotate(-45, 1, 0, 0);
		s = s.divide(0.85F);
		transform.set(p, q, s);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		assertEquals("Transform scale values did not match", transform.getScale(), s);
		
		Transform copy = new Transform(new Point(p), new Quaternion(q), new Vector3(s));
		assertEquals("Copy of transform failed equals test", transform, copy);
		assertEquals("Copy of transform failed reverse-equals test", copy, transform);
		assertEquals("Copy of transform hashcodes do not match", transform.hashCode(), copy.hashCode());
		
		copy = transform.copy();
		assertEquals("Copy of transform failed equals test", transform, copy);
		assertEquals("Copy of transform failed reverse-equals test", copy, transform);
		assertEquals("Copy of transform hashcodes do not match", transform.hashCode(), copy.hashCode());
	}

}
