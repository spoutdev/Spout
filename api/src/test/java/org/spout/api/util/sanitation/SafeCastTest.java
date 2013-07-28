/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.sanitation;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SafeCastTest {
	@Test
	public void testLong() {
		assertTrue("Unable to process valid cast", SafeCast.toLong(new Long(22), 7L) == 22L);
		assertTrue("Default did not work (wrong type)", SafeCast.toLong(new Integer(22), 7L) == 7L);
		assertTrue("Default did not work (null)", SafeCast.toLong(null, 9L) == 9L);
	}

	@Test
	public void testInt() {
		assertTrue("Unable to process valid cast", SafeCast.toInt(new Integer(22), 7) == 22);
		assertTrue("Default did not work (wrong type)", SafeCast.toInt(new Long(22), 7) == 7);
		assertTrue("Default did not work (null)", SafeCast.toInt(null, 9) == 9);
	}

	@Test
	public void testByte() {
		assertTrue("Unable to process valid cast", SafeCast.toByte(new Byte((byte) 22), (byte) 7) == (byte) 22);
		assertTrue("Default did not work (wrong type)", SafeCast.toByte(new Integer(22), (byte) 7) == (byte) 7);
		assertTrue("Default did not work (null)", SafeCast.toByte(null, (byte) 9) == (byte) 9);
	}

	@Test
	public void testFloat() {
		assertTrue("Unable to process valid cast", SafeCast.toFloat(new Byte((byte) 22), 111.1F) == 111.1F);
		assertTrue("Default did not work (wrong type)", SafeCast.toFloat(new Integer(22), 9.2F) == 9.2F);
		assertTrue("Default did not work (null)", SafeCast.toFloat(null, 9.2F) == 9.2F);
	}

	@Test
	public void testByteArray() {
		byte[] array = new byte[10];
		byte[] backup = new byte[10];
		assertTrue("Unable to process valid cast", SafeCast.toByteArray(array, backup) == array);
		assertTrue("Default did not work (wrong type)", SafeCast.toByteArray(new Integer(22), backup) == backup);
		assertTrue("Default did not work (null)", SafeCast.toByteArray(null, backup) == backup);
	}

	@Test
	public void testShortArray() {
		short[] array = new short[10];
		short[] backup = new short[10];
		assertTrue("Unable to process valid cast", SafeCast.toShortArray(array, backup) == array);
		assertTrue("Default did not work (wrong type)", SafeCast.toShortArray(new Integer(22), backup) == backup);
		assertTrue("Default did not work (null)", SafeCast.toShortArray(null, backup) == backup);
	}

	@Test
	public void testString() {
		String string = "Valid string";
		String backup = "Backup";
		assertTrue("Unable to process valid cast", (SafeCast.toString(string, backup) == null ? string == null : SafeCast.toString(string, backup).equals(string)));
		assertTrue("Default did not work (wrong type)", (SafeCast.toString(new Integer(22), backup) == null ? backup == null : SafeCast.toString(new Integer(22), backup).equals(backup)));
		assertTrue("Default did not work (null)", (SafeCast.toString(null, backup) == null ? backup == null : SafeCast.toString(null, backup).equals(backup)));
	}

	@Test
	public void testGeneric() {
		String string = "Valid string";
		String backup = "Backup";
		assertTrue("Unable to process valid cast", (SafeCast.toGeneric(string, backup, String.class) == null ? string == null : SafeCast.toGeneric(string, backup, String.class).equals(string)));
		assertTrue("Default did not work (wrong type)", (SafeCast.toGeneric(new Integer(22), backup, String.class) == null ? backup == null : SafeCast.toGeneric(new Integer(22), backup, String.class).equals(backup)));
		assertTrue("Default did not work (null)", (SafeCast.toGeneric(null, backup, String.class) == null ? backup == null : SafeCast.toGeneric(null, backup, String.class).equals(backup)));
	}
}
