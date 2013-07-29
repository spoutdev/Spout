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

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import org.spout.api.faker.EngineFaker;
import org.spout.api.faker.WorldFaker;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static org.spout.api.faker.EngineFaker.TEST_UUID;
import static org.spout.api.util.ChannelBufferUtils.getExpandedHeight;
import static org.spout.api.util.ChannelBufferUtils.getShifts;
import static org.spout.api.util.ChannelBufferUtils.readColor;
import static org.spout.api.util.ChannelBufferUtils.readParameters;
import static org.spout.api.util.ChannelBufferUtils.readPoint;
import static org.spout.api.util.ChannelBufferUtils.readQuaternion;
import static org.spout.api.util.ChannelBufferUtils.readString;
import static org.spout.api.util.ChannelBufferUtils.readStringArray;
import static org.spout.api.util.ChannelBufferUtils.readTransform;
import static org.spout.api.util.ChannelBufferUtils.readUUID;
import static org.spout.api.util.ChannelBufferUtils.readVector2;
import static org.spout.api.util.ChannelBufferUtils.readVector3;
import static org.spout.api.util.ChannelBufferUtils.writeColor;
import static org.spout.api.util.ChannelBufferUtils.writeParameters;
import static org.spout.api.util.ChannelBufferUtils.writePoint;
import static org.spout.api.util.ChannelBufferUtils.writeQuaternion;
import static org.spout.api.util.ChannelBufferUtils.writeString;
import static org.spout.api.util.ChannelBufferUtils.writeStringArray;
import static org.spout.api.util.ChannelBufferUtils.writeTransform;
import static org.spout.api.util.ChannelBufferUtils.writeUUID;
import static org.spout.api.util.ChannelBufferUtils.writeVector2;
import static org.spout.api.util.ChannelBufferUtils.writeVector3;

public class ChannelBufferUtilsTest {
	public static final List<Parameter<?>> TEST_PARAMS = new ArrayList<>();

	static {
		EngineFaker.setupEngine();

		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_BYTE, 1, (byte) 33));
		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_SHORT, 2, (short) 333));
		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_INT, 3, 22));
		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_FLOAT, 4, 1.23F));
		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_STRING, 5, "Hello World"));
		TEST_PARAMS.add(new Parameter<>(Parameter.TYPE_ITEM, 6, new ItemStack(BlockMaterial.SOLID_SKYBLUE, 5)));
	}

	@Test
	public void testParameters() throws Exception {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeParameters(buf, TEST_PARAMS);
		assertEquals(TEST_PARAMS, readParameters(buf));
	}

	private static final String TEST_STRING = "This is a test String \u007Aawith symbols";

	@Test
	public void testString() throws Exception {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeString(buf, TEST_STRING);
		assertEquals(TEST_STRING, readString(buf));
	}

	@Test
	public void testUUID() throws Exception {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeUUID(buf, TEST_UUID);
		assertEquals(TEST_UUID, readUUID(buf));
	}

	@Test
	public void testVector3() throws IllegalAccessException {
		for (Field field : Vector3.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Vector2.class.isAssignableFrom(field.getType())) {
				Vector3 vec = (Vector3) field.get(null);
				ChannelBuffer buf = ChannelBuffers.buffer(12);
				writeVector3(buf, vec);
				assertEquals(vec, readVector3(buf));
			}
		}
	}

	private static final World TEST_WORLD = WorldFaker.setupWorld();
	private static final Point TEST_POINT = new Point(TEST_WORLD, 0, 0, 0);
	private static final Transform TEST_TRANSFORM = new Transform(TEST_POINT, Quaternion.IDENTITY, Vector3.ZERO);

	@Test
	public void testTransform() throws IllegalAccessException {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeTransform(buf, TEST_TRANSFORM);
		assertEquals(TEST_TRANSFORM, readTransform(buf));
	}

	@Test
	public void testPoint() throws IllegalAccessException {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writePoint(buf, TEST_POINT);
		assertEquals(TEST_POINT, readPoint(buf));
	}

	@Test
	public void testQuaternion() throws IllegalAccessException {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeQuaternion(buf, Quaternion.IDENTITY);
		assertEquals(Quaternion.IDENTITY, readQuaternion(buf));
	}

	private static final String[] TEST_STRING_ARRAY = {"One", "Two", "Three"};

	@Test
	public void testStringArray() throws IllegalAccessException {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		writeStringArray(buf, TEST_STRING_ARRAY);
		assertArrayEquals(TEST_STRING_ARRAY, readStringArray(buf));
	}

	@Test
	public void testShifts() {
		for (int i = 2; i < 12; ++i) {
			final int origHeight = (int) Math.pow(2, i);
			assertEquals(getExpandedHeight(getShifts(origHeight) - 1), origHeight);
		}
	}

	@Test
	public void testVector2() throws IllegalAccessException {
		for (Field field : Vector2.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Vector2.class.isAssignableFrom(field.getType())) {
				Vector2 vec = (Vector2) field.get(null);
				ChannelBuffer buf = ChannelBuffers.buffer(8);
				writeVector2(buf, vec);
				assertEquals(vec, readVector2(buf));
			}
		}
	}

	@Test
	public void testColor() throws IllegalAccessException {
		for (Field field : Color.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Color.class.isAssignableFrom(field.getType())) {
				Color color = (Color) field.get(null);
				ChannelBuffer buf = ChannelBuffers.buffer(4);
				writeColor(color, buf);
				assertEquals(color, readColor(buf));
			}
		}
	}
}
