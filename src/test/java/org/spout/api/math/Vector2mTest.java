package org.spout.api.math;

import org.junit.Test;
import static org.spout.api.math.TestUtils.*;

/**
 *
 * @author yetanotherx
 */
public class Vector2mTest {
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
