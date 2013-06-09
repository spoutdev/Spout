/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.protocol;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.util.Parameter;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;
import org.spout.engine.EngineFaker;
import static org.spout.engine.EngineFaker.TEST_UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.engine.WorldFaker;
import org.spout.engine.protocol.builtin.ChannelBufferUtils;

import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readString;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeString;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readUUID;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeUUID;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readTransform;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeTransform;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readVector3;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeVector3;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readPoint;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writePoint;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readQuaternion;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeQuaternion;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.readStringArray;
import static org.spout.engine.protocol.builtin.ChannelBufferUtils.writeStringArray;
import org.spout.engine.world.SpoutClientWorld;

public class ChannelBufferUtilsTest {

	static {
		EngineFaker.setupEngine();
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
}
