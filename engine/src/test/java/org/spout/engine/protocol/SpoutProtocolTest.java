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
package org.spout.engine.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.delta.DeltaMap;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.math.vector.Vector3;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.util.SyncedMapEvent;
import org.spout.engine.faker.EngineFaker;
import org.spout.engine.faker.WorldFaker;
import org.spout.engine.protocol.builtin.SpoutProtocol;
import org.spout.engine.protocol.builtin.message.BlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;
import org.spout.engine.protocol.builtin.message.ChunkDatatableMessage;
import org.spout.engine.protocol.builtin.message.ClickRequestMessage;
import org.spout.engine.protocol.builtin.message.ClickResponseMessage;
import org.spout.engine.protocol.builtin.message.CommandMessage;
import org.spout.engine.protocol.builtin.message.CuboidBlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.EntityDatatableMessage;
import org.spout.engine.protocol.builtin.message.LoginMessage;
import org.spout.engine.protocol.builtin.message.SyncedMapMessage;
import org.spout.engine.protocol.builtin.message.UpdateEntityMessage;
import org.spout.engine.protocol.builtin.message.WorldChangeMessage;
import org.spout.math.imaginary.Quaternion;

public class SpoutProtocolTest extends BaseProtocolTest {
	static {
		EngineFaker.setupEngine();
	}

	private static final SpoutProtocol PROTOCOL = new SpoutProtocol();
	static final boolean[] allFalse = new boolean[16];
	static final byte[][] columnData = new byte[16][10240];
	static final short[] chunkData = new short[16 * 16 * 16];

	static {
		Arrays.fill(chunkData, (short) 0);
	}

	static final byte[] biomeData1 = new byte[256];
	static final byte[] biomeData2 = new byte[256];
	private static final World TEST_WORLD = WorldFaker.setupWorld();
	private static final Point TEST_POINT = new Point(TEST_WORLD, 0, 0, 0);
	private static final Transform TEST_TRANSFORM = new Transform(TEST_POINT, Quaternion.IDENTITY, Vector3.ZERO);
	static final byte[] TEST_SERIALIZED_DATA = new ManagedHashMap().serialize();
	private static final Message[] TEST_MESSAGES = new Message[] {
			new BlockUpdateMessage(0, 0, 0, (short) 0, (short) 0),
			new ChunkDataMessage(0, 0, 0, chunkData, chunkData, null, null, new HashMap<Short, byte[]>()),
			new ClickRequestMessage((byte) 0, (byte) 0, ClickRequestMessage.Action.LEFT),
			new ClickResponseMessage((byte) 0, (byte) 0, ClickResponseMessage.Response.ALLOW),
			new CommandMessage("test", "hi"),
			new CuboidBlockUpdateMessage(TEST_WORLD.getUID(), Vector3.ZERO, Vector3.UP, new short[0], new short[0], new byte[0], new byte[0]),
			new EntityDatatableMessage(0, TEST_SERIALIZED_DATA, DeltaMap.DeltaType.SET),
			new LoginMessage("Spouty", 0),
			new SyncedMapMessage(0, SyncedMapEvent.Action.ADD, new ArrayList<Pair<Integer, String>>()),
			new WorldChangeMessage("world", EngineFaker.TEST_UUID, TEST_TRANSFORM, TEST_SERIALIZED_DATA, DeltaMap.DeltaType.SET),
			new UpdateEntityMessage(0, TEST_TRANSFORM, UpdateEntityMessage.UpdateAction.TRANSFORM, new NullRepositionManager()),
			new ChunkDatatableMessage("Blank", 0, 0, 0, biomeData1, DeltaMap.DeltaType.SET)
	};

	static {
		Random r = new Random();
		for (int i = 0; i < columnData.length; i++) {
			byte[] data = columnData[i];
			for (int j = 0; j < data.length; j++) {
				data[j] = (byte) r.nextInt();
			}
		}
		for (int i = 0; i < biomeData1.length; i++) {
			biomeData1[i] = (byte) r.nextInt();
			biomeData2[i] = (byte) r.nextInt();
		}
	}

	public SpoutProtocolTest() {
		super(PROTOCOL.getCodecLookupService(), TEST_MESSAGES);
	}
}
