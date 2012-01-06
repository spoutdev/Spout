package org.getspout.api.math;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yetanotherx
 */
public class Vector3mTest {
	
	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testSetValues() {
		Vector3m x = new Vector3m(0, 4, 5);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);
		doAssertDouble(x.z, 5);
		
		x.setX(5);
		x.setY(7);
		x.setZ(6);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
		doAssertDouble(x.z, 6);
	}

	@Test
	public void testAdd() {
		Vector3m x = new Vector3m(0, 4, 3);
		Vector3m z = new Vector3m(3, 6, -1);
		x.add(z);
		
		doAssertDouble(x.x, 3);
		doAssertDouble(x.y, 10);
		doAssertDouble(x.z, 2);
	}
}
