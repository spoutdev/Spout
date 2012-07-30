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
package org.spout.api.protocol.builtin;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

/**
 * Various utility methods to help with writing objects to a ChannelBuffer
 */
public class ChannelBufferUtils {
	public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

	private ChannelBufferUtils() {
	}

	public static String readString(ChannelBuffer buffer) {
		int length = buffer.readInt();
		byte[] stringBytes = new byte[length];
		buffer.readBytes(stringBytes);
		return new String(stringBytes, UTF_8_CHARSET);
	}

	public static void writeString(ChannelBuffer buffer, String str) {
		byte[] stringBytes = str.getBytes(UTF_8_CHARSET);
		buffer.writeInt(stringBytes.length);
		buffer.writeBytes(stringBytes);
	}

	public static final int UUID_SIZE = 16;

	public static UUID readUUID(ChannelBuffer buffer) {
		final long lsb = buffer.readLong();
		final long msb = buffer.readLong();
		return new UUID(lsb, msb);
	}

	public static void writeUUID(ChannelBuffer buffer, UUID uuid) {
		buffer.writeLong(uuid.getLeastSignificantBits());
		buffer.writeLong(uuid.getMostSignificantBits());
	}

	public static final int VECTOR3_SIZE = 12;
	private static final int POINT_SIZE = VECTOR3_SIZE + UUID_SIZE;
	public static final int QUATERNINON_SIZE = 16;
	public static final int TRANSFORM_SIZE = POINT_SIZE + QUATERNINON_SIZE + VECTOR3_SIZE;

	public static Transform readTransform(ChannelBuffer buffer) {
		Point position = readPoint(buffer);
		Quaternion rotation = readQuaternion(buffer);
		Vector3 scale = readVector3(buffer);
		return new Transform(position, rotation, scale);
	}

	public static void writeTransform(ChannelBuffer buffer, Transform transform) {
		writePoint(buffer, transform.getPosition());
		writeQuaternion(buffer, transform.getRotation());
		writeVector3(buffer, transform.getScale());
	}

	public static Vector3 readVector3(ChannelBuffer buffer) {
		final float x = buffer.readFloat();
		final float y = buffer.readFloat();
		final float z = buffer.readFloat();
		return new Vector3(x, y, z);
	}

	public static void writeVector3(ChannelBuffer buffer, Vector3 vec) {
		buffer.writeFloat(vec.getX());
		buffer.writeFloat(vec.getY());
		buffer.writeFloat(vec.getZ());
	}

	public static Point readPoint(ChannelBuffer buffer) {
		UUID uuid = readUUID(buffer);
		World world = Spout.getEngine().getWorld(uuid);
		if (world == null) {
			throw new IllegalArgumentException("Unknown world with UUID " + uuid);
		}

		final float x = buffer.readFloat();
		final float y = buffer.readFloat();
		final float z = buffer.readFloat();
		return new Point(world, x, y, z);
	}

	public static void writePoint(ChannelBuffer buffer, Point vec) {
		writeUUID(buffer, vec.getWorld().getUID());
		buffer.writeFloat(vec.getX());
		buffer.writeFloat(vec.getY());
		buffer.writeFloat(vec.getZ());
	}

	public static Quaternion readQuaternion(ChannelBuffer buffer) {
		final float x = buffer.readFloat();
		final float y = buffer.readFloat();
		final float z = buffer.readFloat();
		final float w = buffer.readFloat();
		return new Quaternion(x, y, z, w, true);
	}

	public static void writeQuaternion(ChannelBuffer buffer, Quaternion quaternion) {
		buffer.writeFloat(quaternion.getX());
		buffer.writeFloat(quaternion.getY());
		buffer.writeFloat(quaternion.getZ());
		buffer.writeFloat(quaternion.getW());
	}

	private static final byte ARGUMENT_TYPE_STYLE = 0x0, ARGUMENT_TYPE_STRING = 0x1;

	public static List<Object> readCommandArguments(ChannelBuffer buffer) {
		final int length = buffer.readShort();
		List<Object> arguments = new ArrayList<Object>(length);
		for (int i = 0; i < length; ++i) {
			switch (buffer.readByte()) {
				case ARGUMENT_TYPE_STYLE:
					short id = buffer.readShort();
					arguments.add(ChatStyle.byId(id));
					break;
				case ARGUMENT_TYPE_STRING:
					arguments.add(readString(buffer));
			}
		}
		return arguments;
	}

	public static void writeCommandArguments(ChannelBuffer buffer, List<Object> arguments) {
		buffer.writeShort(arguments.size());
		for (Object obj : arguments) {
			if (obj instanceof ChatStyle) {
				buffer.writeByte(ARGUMENT_TYPE_STYLE);
				buffer.writeShort(((ChatStyle) obj).getId());
			} else {
				buffer.writeByte(ARGUMENT_TYPE_STRING);
				writeString(buffer, String.valueOf(obj));
			}
		}
	}
}
