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
package org.spout.api.datatable;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.junit.Test;
import org.spout.api.datatable.AbstractData;
import org.spout.api.datatable.BooleanData;
import org.spout.api.datatable.FloatData;
import org.spout.api.datatable.IntegerData;
import org.spout.api.datatable.NullData;
import org.spout.api.datatable.SerializableData;

public class DatatableObjectIdsTest {
	Random r = new Random();

	@Test
	public void test() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		testClass(NullData.class, 0);
		testClass(BooleanData.class, 1);
		testClass(FloatData.class, 2);
		testClass(IntegerData.class, 3);
		testClass(SerializableData.class, 4);

		AbstractData o = AbstractData.newInstance(0, 7);
		assertTrue("DatatableNil object not given for id = 0", o instanceof NullData);
	}

	private void testClass(Class<? extends AbstractData> clazz, int expectedId) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor<? extends AbstractData> c = clazz.getConstructor(int.class);
		AbstractData obj = c.newInstance(new Integer(0));
		int id = obj.getObjectTypeId();
		assertTrue(clazz.getSimpleName() + " have wrong object id " + id + ", expected " + expectedId, id == expectedId); 
		int key = r.nextInt();
		AbstractData obj2 = AbstractData.newInstance(expectedId, key);
		assertTrue("Incorrect key value used for static newInstance", obj2.hashCode() == key);
		assertTrue("Wrong class, " + obj2.getClass().getSimpleName() + ", returned for expectedId " + expectedId + ", expected class was " + clazz.getSimpleName(), clazz.isInstance(obj2));
	}
}
