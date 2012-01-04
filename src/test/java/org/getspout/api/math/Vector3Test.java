package org.getspout.api.math;

import static org.junit.Assert.*;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.junit.Assert;
import org.junit.Test;

public class Vector3Test {
	public static double eps = 0.01;
	@Test
	public void testVector3DoubleDoubleDouble() {
		Vector3 v = new Vector3(2.0f, 4.0f, -1.0f);
		if(v.getX() != 2.0) fail("X does not equal 2");
		if(v.getY() != 4.0) fail("Y does not equal 4");
		if(v.getZ() != -1.0) fail("Z does not equal -1");
	}

	@Test
	public void testVector3() {
		Vector3 v = new Vector3();
		if(v.getX() != 0.0) fail("X does not equal 0");
		if(v.getY() != 0.0) fail("Y does not equal 0");
		if(v.getZ() != 0.0) fail("Z does not equal 0");
		
	}

	@Test
	public void testVector3Vector3() {
		Vector3 v = new Vector3(new Vector3(-1.0f, 3.0f, 2.0f));
		if(v.getX() != -1.0) fail("X does not equal -1.0");
		if(v.getY() != 3.0) fail("Y does not equal 3.0");
		if(v.getZ() != 2.0) fail("Z does not equal 2.0");
	}

	@Test
	public void testVector3Vector2Double() {
		Vector3 v = new Vector3(new Vector2(-1.0f, 3.0f), 1.0f);
		if(v.getX() != -1.0) fail("X does not equal -1.0");
		if(v.getY() != 3.0) fail("Y does not equal 3.0");
		if(v.getZ() != 1.0) fail("Z does not equal 2.0");
	}

	@Test
	public void testVector3Vector2() {
		Vector3 v = new Vector3(new Vector2(-1.0f, 3.0f));
		if(v.getX() != -1.0) fail("X does not equal -1.0");
		if(v.getY() != 3.0) fail("Y does not equal 3.0");
		if(v.getZ() != 0.0) fail("Z does not equal 2.0");
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
		if(z.getX() != 0.0) fail("z.X does not equal 0");
		if(z.getY() != 0.0) fail("z.Y does not equal 0");
		if(z.getZ() != 0.0) fail("z.Z does not equal 0");
		
		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.subtract(new Vector3(5, 2, 5));
		if(v.getX() != -4.0) fail("v.X does not equal -4");
		if(v.getY() != 1.0) fail("v.Y does not equal 1");
		if(v.getZ() != 5.0) fail("v.Z does not equal 5");
	}

	@Test
	public void testAddVector3Vector3() {
		Vector3 x = new Vector3(1,0, 0);
		Vector3 z = x.add(x);
		if(z.getX() != 2.0) fail("z.X does not equal 2");
		if(z.getY() != 0.0) fail("z.Y does not equal 0");
		if(z.getZ() != 0.0) fail("z.Z does not equal 0");
		
		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.add(new Vector3(5, 2, 5));
		if(v.getX() != 6.0) fail("v.X does not equal 6");
		if(v.getY() != 5.0) fail("v.Y does not equal 5");
		if(v.getZ() != 15.0) fail("v.Z does not equal 15");
	}

	@Test
	public void testScaleVector3Double() {
		Vector3 s = new Vector3(1,0,0);
		Vector3 z = s.scale(5);
		if(z.getX() != 5.0) fail("z.X does not equal 5");
		if(z.getY() != 0.0) fail("z.Y does not equal 0");
		if(z.getZ() != 0.0) fail("z.Z does not equal 0");
		
		Vector3 u = new Vector3(1,3, 10);
		Vector3 v = u.scale(5);
		if(v.getX() != 5.0) fail("v.X does not equal 5");
		if(v.getY() != 15.0) fail("v.Y does not equal 15");
		if(v.getZ() != 50.0) fail("v.Z does not equal 50");
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
		if(z.getX() != 0.0) fail("z.X does not equal 0, it's " + z.getX());
		if(z.getY() != 0.0) fail("z.Y does not equal 0, it's " + z.getY());
		if(z.getZ() != 1.0) fail("z.Z does not equal 1, it's " + z.getZ());
		
		Vector3 a = new Vector3(2,5,3);
		Vector3 b = new Vector3(3, -1,8);
		Vector3 c = a.cross(b);
		if(c.getX() != 43) fail("z.X does not equal 43, it's " + c.getX());
		if(c.getY() != -7) fail("z.Y does not equal -7, it's " + c.getY());
		if(c.getZ() != -17) fail("z.Z does not equal -17, it's " + c.getZ());
		
	}

	@Test
	public void testToArrayVector3() {
		Vector3 x = new Vector3(1,0,0);
		float[] y = x.toArray();
		if(y[0] != 1.0) fail("z.X does not equal 1, it's " + y[0]);
		if(y[1] != 0.0) fail("z.Y does not equal 0, it's " + y[1]);
		if(y[2] != 0.0) fail("z.Z does not equal 0, it's " + y[2]);
		
	}

	@Test
	public void testTransformVector3Matrix() {
		Vector3 x = new Vector3(1,0,0);
		Vector3 u = x.transform(Matrix.rotateY(90));
		if(! u.equals(new Vector3(0,0,-1))) fail("{1,0,0} rotated about Y ");
		
		Vector3 y = new Vector3(2,4,5);
		Vector3 v = y.transform(Matrix.rotateX(30));
		Vector3 res = new Vector3(2,.964f,6.33f);
		if(! v.equals(res)) fail("{2,4,5} rotated about X, expected" + res + " Got "+ v );
		
	}

	@Test
	public void testTransformVector3Quaternion() {
		Vector3 x = new Vector3(1,0,0);	
		Vector3 u = x.transform(new Quaternion(90, new Vector3(0,1,0)));
		if(! u.equals(new Vector3(0,0,-1))) fail("{1,0,0} rotated about Y ");
		
		Vector3 y = new Vector3(2,4,5);
		Vector3 v = y.transform(new Quaternion(30, new Vector3(1,0,0)));
		Vector3 res = new Vector3(2,.964f,6.33f);
		if(! v.equals(res)) fail("{2,4,5} rotated about X, expected" + res + " Got "+ v );
		
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
