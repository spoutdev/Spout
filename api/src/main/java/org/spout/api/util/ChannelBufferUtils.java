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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.CharsetUtil;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

/**
 * Contains several {@link ChannelBuffer}-related utility methods.
 */
public final class ChannelBufferUtils {
	public static final int VECTOR3_SIZE = 12;
	public static final int UUID_SIZE = 16;
	public static final int POINT_SIZE = VECTOR3_SIZE + UUID_SIZE;
	public static final int QUATERNINON_SIZE = 16;
	public static final int TRANSFORM_SIZE = POINT_SIZE + QUATERNINON_SIZE + VECTOR3_SIZE;

	/**
	 * Writes a list of parameters (e.g. mob metadata) to the buffer.
	 *
	 * @param buf The buffer.
	 * @param parameters The parameters.
	 */
	@SuppressWarnings ("unchecked")
	public static void writeParameters(ChannelBuffer buf, List<Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			int type = parameter.getType();
			int index = parameter.getIndex();
			if (index > 0x1F) {
				throw new IllegalArgumentException("Index has a maximum of 0x1F!");
			}

			buf.writeByte(type << 5 | index & 0x1F);

			switch (type) {
				case Parameter.TYPE_BYTE:
					buf.writeByte(((Parameter<Byte>) parameter).getValue());
					break;
				case Parameter.TYPE_SHORT:
					buf.writeShort(((Parameter<Short>) parameter).getValue());
					break;
				case Parameter.TYPE_INT:
					buf.writeInt(((Parameter<Integer>) parameter).getValue());
					break;
				case Parameter.TYPE_FLOAT:
					buf.writeFloat(((Parameter<Float>) parameter).getValue());
					break;
				case Parameter.TYPE_STRING:
					writeString(buf, ((Parameter<String>) parameter).getValue());
					break;
				case Parameter.TYPE_ITEM:
					ItemStack item = ((Parameter<ItemStack>) parameter).getValue();
					buf.writeShort(item.getMaterial().getId());
					buf.writeByte(item.getAmount());
					buf.writeShort(item.getData());
					break;
			}
		}

		buf.writeByte(127);
	}

	/**
	 * Reads a list of parameters from the buffer.
	 *
	 * @param buf The buffer.
	 * @return The parameters.
	 */
	public static List<Parameter<?>> readParameters(ChannelBuffer buf) throws IOException {
		List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

		for (int b = buf.readUnsignedByte(); b != 127; b = buf.readUnsignedByte()) {
			int type = (b & 0xE0) >> 5;
			int index = b & 0x1F;

			switch (type) {
				case Parameter.TYPE_BYTE:
					parameters.add(new Parameter<Byte>(type, index, buf.readByte()));
					break;
				case Parameter.TYPE_SHORT:
					parameters.add(new Parameter<Short>(type, index, buf.readShort()));
					break;
				case Parameter.TYPE_INT:
					parameters.add(new Parameter<Integer>(type, index, buf.readInt()));
					break;
				case Parameter.TYPE_FLOAT:
					parameters.add(new Parameter<Float>(type, index, buf.readFloat()));
					break;
				case Parameter.TYPE_STRING:
					parameters.add(new Parameter<String>(type, index, readString(buf)));
					break;
				case Parameter.TYPE_ITEM:
					short id = buf.readShort();
					int count = buf.readByte();
					short data = buf.readShort();
					ItemStack item = new ItemStack(Material.get(id), data, count);
					parameters.add(new Parameter<ItemStack>(type, index, item));
					break;
			}
		}

		return parameters;
	}

	/**
	 * Writes a UTF-8 string to the buffer.
	 *
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long <em>after</em> it is encoded.
	 */
	public static void writeUtf8String(ChannelBuffer buf, String str) {
		byte[] bytes = str.getBytes(CharsetUtil.UTF_8);
		if (bytes.length >= 65536) {
			throw new IllegalArgumentException("Encoded UTF-8 string too long.");
		}

		buf.writeShort(bytes.length);
		buf.writeBytes(bytes);
	}

	/**
	 * Reads a UTF-8 encoded string from the buffer.
	 *
	 * @param buf The buffer.
	 * @return The string.
	 */
	public static String readUtf8String(ChannelBuffer buf) {
		int len = buf.readUnsignedShort();

		byte[] bytes = new byte[len];
		buf.readBytes(bytes);

		return new String(bytes, CharsetUtil.UTF_8);
	}

	public static CompoundMap readCompound(ChannelBuffer buf) {
		int len = buf.readShort();
		if (len > 0) {
			byte[] bytes = new byte[len];
			buf.readBytes(bytes);
			NBTInputStream str = null;
			try {
				str = new NBTInputStream(new ByteArrayInputStream(bytes));
				Tag<?> tag = str.readTag();
				if (tag instanceof CompoundTag) {
					return ((CompoundTag) tag).getValue();
				}
			} catch (IOException e) {
			} finally {
				if (str != null) {
					try {
						str.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return null;
	}

	public static void writeCompound(ChannelBuffer buf, CompoundMap data) {
		if (data == null) {
			buf.writeShort(-1);
			return;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NBTOutputStream str = null;
		try {
			str = new NBTOutputStream(out);
			str.writeTag(new CompoundTag("", data));
			str.close();
			str = null;
			buf.writeShort(out.size());
			buf.writeBytes(out.toByteArray());
		} catch (IOException e) {
		} finally {
			if (str != null) {
				try {
					str.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static int getShifts(int height) {
		if (height == 0) {
			return 0;
		}
		int shifts = 0;
		int tempVal = height;
		while (tempVal != 1) {
			tempVal >>= 1;
			++shifts;
		}
		return shifts;
	}

	public static int getExpandedHeight(int shift) {
		if (shift > 0 && shift < 12) {
			return 2 << shift;
		} else if (shift >= 32) {
			return shift;
		}
		return 256;
	}

	public static Vector2 readVector2(ChannelBuffer buf) {
		float x = buf.readFloat();
		float z = buf.readFloat();
		return new Vector2(x, z);
	}

	public static void writeVector2(ChannelBuffer buf, Vector2 vec) {
		buf.writeFloat(vec.getX());
		buf.writeFloat(vec.getY());
	}

	public static Color readColor(ChannelBuffer buf) {
		int argb = buf.readInt();
		return new Color(argb);
	}

	public static void writeColor(Color color, ChannelBuffer buf) {
		buf.writeInt(color.getRGB());
	}

	public static String readString(ChannelBuffer buffer) {
		int length = buffer.readInt();
		byte[] stringBytes = new byte[length];
		buffer.readBytes(stringBytes);
		return new String(stringBytes, CharsetUtil.UTF_8);
	}

	public static void writeString(ChannelBuffer buffer, String str) {
		byte[] stringBytes = str.getBytes(CharsetUtil.UTF_8);
		buffer.writeInt(stringBytes.length);
		buffer.writeBytes(stringBytes);
	}

	public static UUID readUUID(ChannelBuffer buffer) {
		final long lsb = buffer.readLong();
		final long msb = buffer.readLong();
		return new UUID(msb, lsb);
	}

	public static void writeUUID(ChannelBuffer buffer, UUID uuid) {
		buffer.writeLong(uuid.getLeastSignificantBits());
		buffer.writeLong(uuid.getMostSignificantBits());
	}

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
		World world = null;
		if (Spout.getPlatform() == Platform.SERVER) {
			world = ((Server) Spout.getEngine()).getWorld(uuid);
		} else {
			World world1 = ((Client) Spout.getEngine()).getWorld();
			if (world1.getUID().equals(uuid)) {
				world = world1;
			}
		}
		if (world == null) {
			throw new IllegalArgumentException("Unknown world with UUID " + uuid
					+ (Spout.getPlatform() == Platform.CLIENT ? "Client UUID: " + ((Client) Spout.getEngine()).getWorld().getUID()
					+ " World: " + ((Client) Spout.getEngine()).getWorld().getName() : ""));
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

	public static String[] readStringArray(ChannelBuffer buffer) {
		int len = buffer.readShort();
		String[] args = new String[len];
		for (int i = 0; i < args.length; i++) {
			args[i] = readString(buffer);
		}
		return args;
	}

	public static void writeStringArray(ChannelBuffer buffer, String... arguments) {
		buffer.writeShort(arguments.length);
		for (String arg : arguments) {
			writeString(buffer, arg);
		}
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ChannelBufferUtils() {
	}
}
