package org.getspout.api.math;

import static org.junit.Assert.*;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.junit.Assert;
import org.junit.Test;

public class Vector3Test {
	public static double eps = 0.1;
	
	private void testValue(Vector3 v, float x, float y, float z){
		if(Math.abs(v.getX() - x ) >= eps || Math.abs(v.getY() - y) >= eps || Math.abs(v.getZ() - z) >= eps){
			fail("Test Fail! Expected {" + x + "," + y +"," + z +"} but got "+ v);
		}
	}
	
	@Test
	public void testVector3DoubleDoubleDouble() {
		Vector3 v = new Vector3(2.0f, 4.0f, -1.0f);
		testValue(v, 2.f, 4.f, -1.f);
	}

	@Test
	public void testVector3() {
		Vector3 v = new Vector3();
		testValue(v, 0, 0, 0);
		
	}

	@Test
	public void testVector3Vector3() {
		Vector3 v = new Vector3(new Vector3(-1.0f, 3.0f, 2.0f));
		testValue(v, -1.f, 3.f, 2.f);
	}

	@Test
	public void testVector3Vector2Double() {
		Vector3 v = new Vector3(new Vector2(-1.0f, 3.0f), 1.0f);
		testValue(v, -1.f, 3.f, 1.f);
	}

	@Test
	public void testVector3Vector2() {
		Vector3 v = new Vector3(new Vector2(-1.0f, 3.0f));
		testValue(v, -1.f, 3.f, 0);
	}

	@Test
	public void testLengthVector3() {
		Vector3 x = new Vector3(1, 0, 0);
		if(Math.abs(x.length() - 1.0) >= eps) fail("Vector" + x.toString() + " expected Lenght 1, got " + x.length());
		Vector3 y = new Vector3(2, 4, 4);
		if(Math.abs(y.length() - Math.sqrt((2*2 + 4*4 + 4*4))) >= eps) fail("Vector" + y.toString() + " expected Lenght"+ Math.sqrt((2*2 + 4*4 + 4*4))+", got " + y.length() );
		
	}

	@Test
	public void testLengthSquaredVector3() {
		Vector3 x = new Vector3(1, 0, 0);
		if(Math.abs(x.lengthSquared() - 1.0) >= eps) fail("Vector" + x.toString() + " expected Lenght 1, got " + x.length());
		Vector3 y = new Vector3(2, 4, 2);
		if(Math.abs(y.lengthSquared() - (2*2 + 4*4 + 2*2)) >= eps) fail("Vector" + y.toString() + " expected Lenght"+ (2*2 + 4*4 + 2*2)+", got " + y.lengthSquared() );
		
	}

	@Test
	public void testNormalizeVector3() {
		Vector3 x = new Vector3(1, 0, 0);
		if(Math.abs(x.normalize().length() - 1.0) >= eps) fail("Vector" + x.toString() + " Normalized not length 1, got " + x.normalize().length());
		Vector3 y = new Vector3(2, 4, 0);
		if(Math.abs(y.normalize().length() - 1.0) >= eps) fail("Vector" + y.toString() + " Normalized not length 1, got " + y.normalize().length());
		
	}

	@Test
	public void testSubtractVector3Vector3() {
		Vector3 x = new Vector3(1,0, 0);
		Vector3 z = x.subtract(x);
		testValue(z, 0, 0, 0);
		
		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.subtract(new Vector3(5, 2, 5));
		testValue(v, -4, 1, 5);
	}

	@Test
	public void testAddVector3Vector3() {
		Vector3 x = new Vector3(1,0, 0);
		Vector3 z = x.add(x);
		testValue(z, 2.f, 0.f, 0.f);

		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.add(new Vector3(5, 2, 5));
		testValue(v, 6.f, 5.f, 15.f);
	}

	@Test
	public void testScaleVector3Double() {
		Vector3 s = new Vector3(1,0,0);
		Vector3 z = s.scale(5);
		testValue(z, 5, 0, 0);
		
		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.scale(5);
		testValue(v, 5.f, 15.f, 50.f);
	}

	@Test
	public void testDotVector3Vector3() {
		Vector3 a = new Vector3(1,1,1);
		Vector3 b = new Vector3(1,0,0);
		if(a.dot(b) - 1.0 >= eps) fail("Dot of " + a.toString() + " and "+ b.toString() + " should be 1, got " + a.dot(b));
		
		Vector3 x = new Vector3(2,3,5);
		Vector3 y = new Vector3(4,1,3);
		if(x.dot(y) - 26 >= eps) fail("Dot of " + x.toString() + " and "+ y.toString() + " should be 26, got " + x.dot(y));
		
	}

	@Test
	public void testCrossVector3Vector3() {
		Vector3 x = new Vector3(1,0,0);
		Vector3 y = new Vector3(0,1,0);
		Vector3 z = x.cross(y);
		testValue(z, 0, 0, 1.f);

		Vector3 a = new Vector3(2,5,3);
		Vector3 b = new Vector3(3, -1,8);
		Vector3 c = a.cross(b);
		testValue(c, 43.f, -7.f, -17.f);		
	}

	@Test
	public void testToArrayVector3() {
		Vector3 x = new Vector3(1,0,0);
		float[] y = x.toArray();
		testValue(x, y[0], y[1], y[2]);
		
	}

	@Test
	public void testTransformVector3Matrix() {
		Vector3 x = new Vector3(1,0,0);
		Vector3 u = x.transform(Matrix.rotateY(90));
		testValue(u, 0, 0, -1);
		
		Vector3 y = new Vector3(2,4,5);
		Vector3 v = y.transform(Matrix.rotateX(30));
		testValue(v, 2,.964f,6.33f);
		
	}

	@Test
	public void testTransformVector3Quaternion() {
		Vector3 x = new Vector3(1,0,0);	
		Vector3 u = x.transform(new Quaternion(90, new Vector3(0,1,0)));
		testValue(u, 0, 0,-1);
		
		Vector3 y = new Vector3(2,4,5);
		Vector3 v = y.transform(new Quaternion(30, new Vector3(1,0,0)));
		testValue(v, 2,.964f,6.33f);
		
		
	}

	@Test
	public void testCompareToVector3Vector3() {
		Vector3 x = new Vector3(1,0,0);
		Vector3 y = new Vector3(2,0,0);
		if(x.compareTo(y)>= 0) fail("x is greater than y but ended up smaller");
	}

	@Test
	public void testEqualsObjectObject() {
		Vector3 x = new Vector3(1,0,0);
		Vector3 y = new Vector3(1,0,0);
		if(!x.equals(y))fail("x should be equal to y");
		
		Vector3 a = new Vector3(1,0,0);
		Vector3 b = (new Vector3(2,0,0)).subtract(a);
		if(!a.equals(b))fail("a should be equal to b");

		a = Vector3.Up;
		b = Vector3.Right;

		boolean result = a.equals(b);

		assertFalse(result);
	}
	
	@Test
	public void testDistance() {
		Vector3 x = new Vector3(0, 0, 0);
		Vector3 y = new Vector3(3, 12, 4);
		if(Vector3.distance(x, y) != 13) fail("Distance between " + x + " and " + y + " is not 13");
	}
	
	@Test
	public void testPow() {
		Vector3 y = new Vector3(3, 12, 4);
		if(!y.pow(3).equals(new Vector3(27, 1728, 64))) fail("New vector is not properly raised to the 3rd power.");
	}

}
