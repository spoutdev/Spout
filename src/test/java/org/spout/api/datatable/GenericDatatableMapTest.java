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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

import org.spout.api.datatable.value.DatatableBool;
import org.spout.api.datatable.value.DatatableFloat;
import org.spout.api.datatable.value.DatatableInt;
import org.spout.api.datatable.value.DatatableSerializable;

public class GenericDatatableMapTest {
	String intString = "Int Value";
	int intValue = 7;

	String floatString = "Float Value";
	float floatValue = 1.234F;

	String boolString = "Bool Value";
	boolean boolValue = true;

	String serialString = "Serial Value";
	Serializable serialValue = new Integer(22);

	@Test
	public void test() throws IOException {
		DatatableMap map1 = new GenericDatatableMap();

		DatatableInt i = new DatatableInt(24);
		i.set(7);

		boolean exceptionThrown = false;
		try {
			map1.set(i);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}

		assertTrue("Invalid key did not cause an exception to be thrown", exceptionThrown);

		int intKey = map1.getIntKey(intString);

		assertTrue("Key was not correctly mapped to \"" + intString + "\"", intString.equals(map1.getStringKey(intKey)));

		i.setKey(intKey);

		map1.set(i);

		DatatableFloat f = new DatatableFloat(0, 1.234F);

		map1.set(floatString, f);

		DatatableBool b = new DatatableBool(0, true);

		map1.set(map1.getIntKey(boolString), b);

		DatatableSerializable s = new DatatableSerializable(map1.getIntKey(serialString), serialValue);

		map1.set(s);

		checkMap(map1);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		map1.output(out);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

		DatatableMap map2 = GenericDatatableMap.readMap(in);

		checkMap(map2);
	}

	private void checkMap(DatatableMap map) {
		DatatableInt io = (DatatableInt)map.get(intString);
		assertTrue("Incorrect integer value stored", io.asInt() == intValue);
		assertTrue("Key mismatch between DatatableObject and map key", map.getIntKey(intString) == io.hashCode());

		DatatableFloat fo = (DatatableFloat)map.get(floatString);
		assertTrue("Incorrect float value stored", fo.asFloat() == floatValue);
		assertTrue("Key mismatch between DatatableObject and map key", map.getIntKey(floatString) == fo.hashCode());

		DatatableBool bo = (DatatableBool)map.get(boolString);
		assertTrue("Incorrect bool value stored", bo.asBool() == boolValue);
		assertTrue("Key mismatch between DatatableObject and map key", map.getIntKey(boolString) == bo.hashCode());

		DatatableSerializable so = (DatatableSerializable)map.get(serialString);
		assertTrue("Incorrect serializable value stored", so.get().equals(serialValue));
		assertTrue("Key mismatch between DatatableObject and map key", map.getIntKey(serialString) == so.hashCode());
	}
}
