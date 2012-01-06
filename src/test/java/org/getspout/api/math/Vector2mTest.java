package org.getspout.api.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yetanotherx
 */
public class Vector2mTest {

	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testSetValues() {
		Vector2m x = new Vector2m(0, 4);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);
		
		x.setX(5);
		x.setY(7);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
	}

	@Test
	public void testAdd() {
		Vector2m x = new Vector2m(0, 4);
		Vector2m z = new Vector2m(3, 6);
		x.add(z);
		
		doAssertDouble(x.x, 3);
		doAssertDouble(x.y, 10);
	}
}
