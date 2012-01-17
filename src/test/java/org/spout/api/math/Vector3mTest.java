/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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
