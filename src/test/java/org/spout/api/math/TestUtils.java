package org.spout.api.math;

import static org.junit.Assert.assertEquals;

public final class TestUtils {
	public static final double eps = 0.001;

	private TestUtils() {
	}

	public static void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	public static void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}
}