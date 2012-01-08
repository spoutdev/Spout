package org.spout.api.math;

import org.junit.*;

import static org.spout.api.math.TestUtils.*;

/**
 *
 * @author yetanotherx
 */
public class Vector3mTest {
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
