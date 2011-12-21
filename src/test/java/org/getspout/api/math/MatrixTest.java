package org.getspout.api.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class MatrixTest {

	@Test
	public void testMatrix() {
		Matrix m = new Matrix();
		if(m.getDimension() != 4) fail("Default Constructor should make 4x4, got" + m.getDimension());
		for(int x = 0; x < 4; x++){
			for(int y = 0; y < 4; y++){
				if(x == y && m.get(x, y) != 1) fail(x + "," + y + "Should be 1, got " + m.get(x,y));
				if(x != y && m.get(x,y) != 0 ) fail(x + "," + y + "Should be 0, got " + m.get(x,y));
			}
		}
		
		
	}

	@Test
	public void testMatrixInt() {
		for(int i = 2; i <= 4; i++){			
		
			Matrix m = new Matrix(i);
			if(m.getDimension() != i) fail("deminsion should be " + i+"x"+i+" , got" + m.getDimension());
			for(int x = 0; x < i; x++){
				for(int y = 0; y < i; y++){
					if(x == y && m.get(x, y) != 1) fail(x + "," + y + "Should be 1, got " + m.get(x,y));
					if(x != y && m.get(x,y) != 0 ) fail(x + "," + y + "Should be 0, got " + m.get(x,y));
				}
			}
		}
		
	}

	@Test
	public void testGetAndSet() {
		Matrix m = new Matrix();
		m.set(0, 0, 12);
		m.set(1, 3, 2);
		if(m.get(0, 0) != 12) fail(0 + "," + 0 + "Should be 12, got " + m.get(0,0));
		if(m.get(1, 3) != 2) fail(1 + "," + 3 + "Should be 12, got " + m.get(1,3));
		
	}

	@Test
	public void testMultiplyMatrix() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddMatrix() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateIdentity() {
		fail("Not yet implemented");
	}

	@Test
	public void testTranslate() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleVector3() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotateX() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotateY() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotateZ() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotate() {
		fail("Not yet implemented");
	}

}
