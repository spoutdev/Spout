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
package org.spout.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import org.spout.api.util.list.ByteCircularBufferFIFO;

import static org.junit.Assert.assertTrue;

public class VarIntTest {
	private static int LENGTH = 65536;
	private static int threshold1 = 0x00000080;
	private static int threshold2 = 0x00007F00;

	@Test
	public void test() throws IOException {
		int[] ints = new int[LENGTH];

		Random r = new Random();

		for (int i = 0; i < ints.length; i++) {
			ints[i] = r.nextInt();
		}

		// Guarantees ints near the threshold are included
		for (int i = 0; i < 2048; i++) {
			ints[2048 + i] = threshold1 - 1024 + i;
		}

		// Guarantees all ints from 0 to 255 are included
		for (int i = 0; i < 2048; i++) {
			ints[4096 + i] = threshold2 - 1024 + i;
		}

		ByteCircularBufferFIFO buf = new ByteCircularBufferFIFO();

		for (int i = 0; i < LENGTH; i++) {
			VarInt.writeInt(buf, ints[i]);
		}

		for (int i = 0; i < LENGTH; i++) {
			int decodedInt = VarInt.readInt(buf);
			assertTrue("Mismatch for int " + ints[i] + ", decoded = " + decodedInt, ints[i] == decodedInt);
		}

		boolean thrown = false;

		try {
			VarInt.readInt(buf);
		} catch (IllegalStateException ise) {
			thrown = true;
		}

		assertTrue("Reading an int from an empty buffer did not throw an exception", thrown);
		String s1;
		String s2;
		String s3;
		String s4;
		byte[] outputArray;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			s1 = "String 1";
			s2 = null;
			s3 = "String - alternative length";
			s4 = "";
			VarInt.writeString(out, s1);
			VarInt.writeString(out, s2);
			VarInt.writeString(out, s3);
			VarInt.writeString(out, s4);
			outputArray = out.toByteArray();
		}
		try (ByteArrayInputStream in = new ByteArrayInputStream(outputArray)) {
			matchString("String mismatch with read/write string " + s1, s1, VarInt.readString(in));

			matchString("String mismatch with read/write string " + s2, s2, VarInt.readString(in));

			matchString("String mismatch with read/write string " + s3, s3, VarInt.readString(in));

			matchString("String mismatch with read/write string " + s4, s4, VarInt.readString(in));
		}
	}

	private void matchString(String message, String s1, String s2) {
		boolean match = (s1 == null ? s2 == null : s1.equals(s2)) || (s1 != null && s1.equals(s2));
		assertTrue(message, match);
	}
}
