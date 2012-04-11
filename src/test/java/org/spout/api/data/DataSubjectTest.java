/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.data;

import org.junit.Test;
import org.spout.api.event.server.data.RetrieveDataEvent;

import static org.junit.Assert.*;

public class DataSubjectTest implements DataSubject {
	private final RetrieveDataEvent event = new RetrieveDataEvent(this, "foo.bar");

	@Test
	public void testDataSubject() {
		String node = "foo.bar";
		event.setResult(20);
		assertEquals(getData(node).toInteger(), 20);
		event.setResult(20L);
		assertEquals(getData(node).toLong(), 20L);
		event.setResult(20.0f);
		assertEquals(getData(node).toFloat(), 20.0, 0f);
		event.setResult(20.0d);
		assertEquals(getData(node).toDouble(), 20.0, 0d);
		event.setResult(true);
		assertEquals(getData(node).toBoolean(), true);
		event.setResult("baz");
		assertEquals(getData(node).toString(), "baz");
	}

	@Override
	public DataValue getData(String node) {
		return event.getResult();
	}

	@Override
	public String getName() {
		return "TestSubject";
	}
}
