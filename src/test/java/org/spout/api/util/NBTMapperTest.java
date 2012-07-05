/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.util;

import java.util.ArrayList;

import org.junit.Test;

import org.spout.api.util.sanitation.SafeCast;

import org.spout.nbt.FloatTag;
import org.spout.nbt.ListTag;

import static junit.framework.Assert.assertEquals;

public class NBTMapperTest {
	@Test
	public void transformTest() {
		ArrayList<FloatTag> list = new ArrayList<FloatTag>();
		FloatTag test = new FloatTag("test", 1f);
		FloatTag test2 = new FloatTag("test2", 2f);
		list.add(test);
		list.add(test2);
		ListTag<FloatTag> testList = new ListTag<FloatTag>("test_transform", FloatTag.class, list);
		float ftest = SafeCast.toFloat(testList.getValue().get(0).getValue(), 3f);
		float ftest2 = SafeCast.toFloat(testList.getValue().get(1).getValue(), 4f);
		System.out.println("Safe cast of first test tag returned: " + ftest);
		System.out.println("Safe cast of second test tag returned: " + ftest2);
		assertEquals(test.getValue(), ftest);
		assertEquals(test2.getValue(), ftest2);

	}
}
