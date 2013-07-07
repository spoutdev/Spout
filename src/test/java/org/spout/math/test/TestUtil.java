package org.spout.math.test;

import org.junit.Assert;

public class TestUtil {

	private final static float DEFAULT_EPSILON_FLOAT = 0.001f;
	private final static double DEFAULT_EPSILON_DOUBLE = 0.001;
	
	public static void assertEquals(float value1, float value2){
		Assert.assertEquals(value1, value2, DEFAULT_EPSILON_FLOAT);
	}
	
	public static void assertEquals(double value1, double value2){
		Assert.assertEquals(value1, value2, DEFAULT_EPSILON_DOUBLE);
	}
}
