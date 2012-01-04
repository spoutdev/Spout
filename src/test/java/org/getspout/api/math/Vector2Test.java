package org.getspout.api.math;

import static org.junit.Assert.*;

import org.getspout.api.math.Vector2;
import org.junit.Test;

public class Vector2Test {
	public static double eps = 0.001;
	@Test
	public void testVector2DoubleDouble() {
		Vector2 x = new Vector2(1,1);
		if(x.getX() != 1) fail("x.X does not equal 1");
		if(x.getY() != 1) fail("x.Y does not equal 1");
		
		x = new Vector2(2,4);
		if(x.getX() != 2) fail("x.X does not equal 1");
		if(x.getY() != 4) fail("x.Y does not equal 1");
	}

	@Test
	public void testVector2() {
		Vector2 x = new Vector2();
		if(x.getX() != 0) fail("x.X does not equal 0");
		if(x.getY() != 0) fail("x.Y does not equal 0");
	}

	@Test
	public void testAddVector2() {
		Vector2 a = new Vector2(1,-1);
		Vector2 b = new Vector2(2,6);
		Vector2 c = a.add(b);
		if(c.getX() != 3) fail("x.X does not equal 3");
		if(c.getY() != 5) fail("x.Y does not equal 5");
	}

	@Test
	public void testSubtractVector2() {
		Vector2 a = new Vector2(1,-1);
		Vector2 b = new Vector2(2,6);
		Vector2 c = a.subtract(b);
		if(c.getX() != -1) fail("c.X got" + c.getX() + " expected -1");
		if(c.getY() != -7) fail("c.Y got" + c.getY() + " expected -7");
	}

	@Test
	public void testScaleDouble() {
		Vector2 x = new Vector2(1,1);
		x = x.scale(2);
		if(x.getX() != 2) fail("x.X does not equal 2");
		if(x.getY() != 2) fail("x.Y does not equal 2");
	}

	@Test
	public void testDotVector2() {
		Vector2 x = new Vector2(1,0);
		if(x.dot(x) != 1.0) fail("x dot x dot should be 1 got " + x.dot(x) );
		
		x = new Vector2(3,2);
		Vector2 y = new Vector2(4,-1);
		if(x.dot(y) != 10.0) fail("x dot x dot should be 10 got " + x.dot(x) );
	}

	@Test
	public void testCross() {
		Vector2 x = new Vector2(1,0);
		Vector2 y = x.cross();
		if(y.getX() != 0) fail("c.X got" + y.getX() + " expected 0");
		if(y.getY() != -1) fail("c.Y got" + y.getY() + " expected -1");
		
		x = new Vector2(5,-3);
		y = x.cross();
		if(y.getX() != -3) fail("c.X got" + y.getX() + " expected -3");
		if(y.getY() != -5) fail("c.Y got" + y.getY() + " expected 5");
	}

	@Test
	public void testLengthSquared() {
		Vector2 x = new Vector2(1,0);
		if(x.lengthSquared() != 1) fail("lengthSquared is " + x.lengthSquared() + " expected 1");
		
		x = new Vector2(5,3);
		if(x.lengthSquared() != 34) fail("lengthSquared is " + x.lengthSquared() + " expected 34");
	}

	@Test
	public void testLength() {
		Vector2 x = new Vector2(1,0);
		if(x.length() -1 >= eps) fail("lengthSquared is " + x.length() + " expected 1");
		
		x = new Vector2(5,3);
		if(x.length() - Math.sqrt(5*5 + 3*3) >= eps) fail("lengthSquared is " + x.length() + " expected " + Math.sqrt(5*5 + 3*3));
	}

	@Test
	public void testNormalize() {
		Vector2 x = new Vector2(1,0);
		if(x.normalize().length() -1 >= eps) fail("lengthSquared is " + x.normalize().length() + " expected 1");
		
		x = new Vector2(5,3);
		if(x.normalize().length() -1 >= eps) fail("lengthSquared is " + x.normalize().length() + " expected 1");
		
	}

	@Test
	public void testToArray() {
		Vector2 x = new Vector2(5,3);
		float[] r = x.toArray();
		if(r[0] != 5) fail("Expected 5 got " + r[0]);
		if(r[1] != 3) fail("Expected 3 got " + r[1]);
		
	}

	@Test
	public void testCompareToVector2() {
		Vector2 x = new Vector2(0,0);
		Vector2 y = new Vector2(1,1);
		if(x.compareTo(y) >= 0) fail("x >= y expected x < y");
		x = new Vector2(5,3);
		y = new Vector2(-2,5);
		if(x.compareTo(y) <= 0) fail("x <= y expected x > y");
	}

	@Test
	public void testEqualsObject() {
		Vector2 x = new Vector2(1,1);
		Vector2 y = new Vector2(1,1);
		if(! x.equals(y)) fail("Vectors x and y not equal.  Should be equal");
		
	}
	
	@Test
	public void testDistance() {
		Vector2 x = new Vector2(0, 0);
		Vector2 y = new Vector2(3, 4);
		if(Vector2.distance(x, y) != 5) fail("Distance between " + x + " and " + y + " is not 5");
	}
	
	@Test
	public void testPow() {
		Vector2 y = new Vector2(3, 4);
		if(!y.pow(3).equals(new Vector2(27, 64))) fail("New vector is not properly raised to the 3rd power.");
	}

}
