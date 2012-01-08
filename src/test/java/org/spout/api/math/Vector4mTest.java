package org.spout.api.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yetanotherx
 */
public class Vector4mTest {

	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testSetValues() {
		Vector4m x = new Vector4m(0, 4, 3, 4);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);
		doAssertDouble(x.z, 3);
		doAssertDouble(x.w, 4);
		
		x.setX(5);
		x.setY(7);
		x.setZ(6);
		x.setW(0);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
		doAssertDouble(x.z, 6);
		doAssertDouble(x.w, 0);
	}

	@Test
	public void testAdd() {
		Vector4m x = new Vector4m(0, 4, 4, 3);
		Vector4m z = new Vector4m(3, 6, -1, 3);
		x.add(z);
		
		doAssertDouble(x.x, 3);
		doAssertDouble(x.y, 10);
		doAssertDouble(x.z, 3);
		doAssertDouble(x.w, 6);
	}
}
