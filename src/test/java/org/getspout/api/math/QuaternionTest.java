package org.getspout.api.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuaternionTest {
	final float eps = 0.01f;
	private void testValues(Quaternion q, float x, float y, float z, float w){
		//System.out.println("Testing! Expected: {"+x+","+y+","+z+","+w+"} got " + q);
		if(Math.abs(q.getX() - x) > eps || Math.abs(q.getY() - y) > eps 
				|| Math.abs(q.getZ() - z) > eps || Math.abs(q.getW() - w) > eps){
		
			fail("Quaternion Wrong! Expected: {"+x+","+y+","+z+","+w+"} got " + q);
		}
			
	}

	@Test
	public void testQuaternionDoubleDoubleDoubleDouble() {
		Quaternion q = new Quaternion(1,0,0,0);
		testValues(q, 1, 0,0,0);
		q = new Quaternion(4,2,6,8);
		testValues(q, 4,2,6,8);
	}

	@Test
	public void testQuaternionDoubleVector3() {
		Quaternion rot = new Quaternion(0, new Vector3(1,0,0));
		float qx = 1.f * (float)Math.sin(0);
		float qy = 0.f * (float)Math.sin(0);
		float qz = 0.f * (float)Math.sin(0);
		float qw = (float)Math.cos(0);
		testValues(rot, qx,qy,qz,qw);
		
		rot = new Quaternion(40, new Vector3(3,2,1));
		qx = 3.f * (float)Math.sin((Math.toRadians(40)/2));
		qy = 2.f * (float)Math.sin((Math.toRadians(40)/2));
		qz = 1.f * (float)Math.sin((Math.toRadians(40)/2));
		qw = (float)Math.cos((Math.toRadians(40)/2));
		testValues(rot, qx,qy,qz,qw);
		
		
		rot = new Quaternion(120, new Vector3(6,-3,2));
		qx = 6.f * (float)Math.sin((Math.toRadians(120)/2));
		qy = -3.f * (float)Math.sin((Math.toRadians(120)/2));
		qz = 2.f * (float)Math.sin((Math.toRadians(120)/2));
		qw = (float)Math.cos((Math.toRadians(120)/2));
		testValues(rot, qx,qy,qz,qw);
	}

	@Test
	public void testLengthSquaredQuaternion() {
		Quaternion rot = new Quaternion(1,0,0,0);
		float ls = rot.lengthSquared();
		if(Math.abs(ls - 1.0f) >= eps) fail("Length Squared of " + rot + " Should be 1.f, got " + ls);
		
		rot = new Quaternion(6,4,3,2);
		ls = rot.lengthSquared();
		if(Math.abs(ls - 65.0f) >= eps) fail("Length Squared of " + rot + " Should be 65.f, got " + ls);
		
		rot = new Quaternion(6,-1,0,2);
		ls = rot.lengthSquared();
		if(Math.abs(ls - 41.0f) >= eps) fail("Length Squared of " + rot + " Should be 41.f, got " + ls);
	}

	@Test
	public void testLengthQuaternion() {
		Quaternion rot = new Quaternion(1,0,0,0);
		float ls = rot.length();
		if(Math.abs(ls - 1.0f) >= eps) fail("Length of " + rot + " Should be 1.f, got " + ls);
		
		rot = new Quaternion(6,4,3,2);
		ls = rot.length();
		if(Math.abs(ls - Math.sqrt(65.0f)) >= eps) fail("Length of " + rot + " Should be 65.f, got " + ls);
		
		rot = new Quaternion(6,-1,0,2);
		ls = rot.length();
		if(Math.abs(ls - Math.sqrt(41.0f)) >= eps) fail("Length of " + rot + " Should be 41.f, got " + ls);
	}

	@Test
	public void testNormalizeQuaternion() {
		Quaternion rot = new Quaternion(1,0,0,0);
		Quaternion norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		
		
		rot = new Quaternion(6,4,3,2);
		norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		
		rot = new Quaternion(6,-1,0,2);
		norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		
		
	}

	@Test
	public void testMultiplyQuaternionQuaternion() {
		Quaternion a = new Quaternion(1,0,0,0);
		Quaternion b = new Quaternion(1,0,0,0);
		Quaternion res = a.multiply(b);
		testValues(res, 0, 0, 0, -1);
		
		a = new Quaternion(0,0,0,1);
		b = new Quaternion(0,0,0,1);
		res = a.multiply(b);
		testValues(res, 0, 0, 0, 1);
		
		a = new Quaternion(5,3,1,1);
		b = new Quaternion(0,0,0,1);
		res = a.multiply(b);
		testValues(res, 5, 3, 1, 1);
		
		a = new Quaternion(5,3,1,1);
		b = new Quaternion(-5,2,1,0);
		res = a.multiply(b);
		testValues(res, -4, -8, 26, 18);
		
	}

	@Test
	public void testRotateQuaternionDoubleVector3() {
		float qx,qy,qz,qw;
		float x = 1;
		float y = 0;
		float z = 0;
		float ang = 0;
		
		Quaternion a = new Quaternion(0,0,0,1);
		Quaternion res = a.rotate(ang, new Vector3(x,y,z));
		testValues(res, 0, 0, 0, 1);
		
		
		x = 1;
		ang = 45;
		a = new Quaternion(0,0,0,1);
		res = a.rotate(ang, new Vector3(x,y,z));
		qx = x * (float)Math.sin((Math.toRadians(ang)/2));
		qy = y * (float)Math.sin((Math.toRadians(ang)/2));
		qz = z * (float)Math.sin((Math.toRadians(ang)/2));
		qw = (float)Math.cos((Math.toRadians(ang)/2));
		testValues(res, qx, qy, qz, qw);
		
		
		x = 1.f;
		y = 4.f;
		z = -3.f;
		ang = 120;
		a = new Quaternion(0,0,0,1);
		res = a.rotate(ang, new Vector3(x,y,z));
		qx = x * (float)Math.sin((Math.toRadians(ang)/2));
		qy = y * (float)Math.sin((Math.toRadians(ang)/2));
		qz = z * (float)Math.sin((Math.toRadians(ang)/2));
		qw = (float)Math.cos((Math.toRadians(ang)/2));
		testValues(res, qx, qy, qz, qw);
		
	}
	
	@Test
	public void testGetAxisAngles(){
		Quaternion r;
		Vector3 res;
		float ang = 20;
		r = new Quaternion(ang, new Vector3(1,0,0));
		res = r.getAxisAngles();
		if(res.getX() - ang >= eps) fail("Expected angle" + ang +", got " + res.getX());
		
		ang = 40;
		r = new Quaternion(ang, new Vector3(0,1,0));
		res = r.getAxisAngles();
		if(Math.abs(res.getY() - ang) >= eps) fail("Expected angle" + ang +", got " + res.getY());
		
		ang = 140;
		r = new Quaternion(ang, new Vector3(0,0,1));
		res = r.getAxisAngles();
		if(Math.abs(res.getZ() - ang) >= eps) fail("Expected angle "+ang+", got " + res.getZ());
		
	}

}
