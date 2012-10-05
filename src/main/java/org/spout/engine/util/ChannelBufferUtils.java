/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.util.Parameter;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

/**
 * Contains several {@link ChannelBuffer}-related utility methods.
 */
public final class ChannelBufferUtils {
	/**
	 * The UTF-8 character set.
	 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * Writes a list of parameters (e.g. mob metadata) to the buffer.
	 * @param buf        The buffer.
	 * @param parameters The parameters.
	 */
	@SuppressWarnings("unchecked")
	public static void writeParameters(ChannelBuffer buf, List<Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			int type = parameter.getType();
			int index = parameter.getIndex();

			buf.writeByte(type << 5 | index);

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
	 * @param buf The buffer.
	 * @return The parameters.
	 */
	public static List<Parameter<?>> readParameters(ChannelBuffer buf) {
		List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

		for (int b = buf.readUnsignedByte(); b != 127; ) {
			int type = (b & 0x0E) >> 5;
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
	 * Writes a string to the buffer.
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long <em>after</em>
	 *                                  it is encoded.
	 */
	public static void writeString(ChannelBuffer buf, String str) {
		int len = str.length();
		if (len >= 65536) {
			throw new IllegalArgumentException("String too long.");
		}

		buf.writeShort(len);
		for (int i = 0; i < len; ++i) {
			buf.writeChar(str.charAt(i));
		}
	}

	/**
	 * Writes a UTF-8 string to the buffer.
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long <em>after</em>
	 *                                  it is encoded.
	 */
	public static void writeUtf8String(ChannelBuffer buf, String str) {
		byte[] bytes = str.getBytes(CHARSET_UTF8);
		if (bytes.length >= 65536) {
			throw new IllegalArgumentException("Encoded UTF-8 string too long.");
		}

		buf.writeShort(bytes.length);
		buf.writeBytes(bytes);
	}

	/**
	 * Reads a string from the buffer.
	 * @param buf The buffer.
	 * @return The string.
	 */
	public static String readString(ChannelBuffer buf) {
		int len = buf.readUnsignedShort();

		char[] characters = new char[len];
		for (int i = 0; i < len; i++) {
			characters[i] = buf.readChar();
		}

		return new String(characters);
	}

	/**
	 * Reads a UTF-8 encoded string from the buffer.
	 * @param buf The buffer.
	 * @return The string.
	 */
	public static String readUtf8String(ChannelBuffer buf) {
		int len = buf.readUnsignedShort();

		byte[] bytes = new byte[len];
		buf.readBytes(bytes);

		return new String(bytes, CHARSET_UTF8);
	}

	public static CompoundMap readCompound(ChannelBuffer buf) {
		int len = buf.readShort();
		if (len >= 0) {
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
		return 128;
	}

	public static Vector3 readVector3(ChannelBuffer buf) {
		float x = buf.readFloat();
		float y = buf.readFloat();
		float z = buf.readFloat();
		return new Vector3(x, y, z);
	}

	public static void writeVector3(Vector3 vec, ChannelBuffer buf) {
		buf.writeFloat(vec.getX());
		buf.writeFloat(vec.getY());
		buf.writeFloat(vec.getZ());
	}

	public static Vector2 readVector2(ChannelBuffer buf) {
		float x = buf.readFloat();
		float z = buf.readFloat();
		return new Vector2(x, z);
	}

	public static void writeVector2(Vector2 vec, ChannelBuffer buf) {
		buf.writeFloat(vec.getX());
		buf.writeFloat(vec.getY());
	}

	public static Color readColor(ChannelBuffer buf) {
		int red = buf.readInt();
		int green = buf.readInt();
		int blue = buf.readInt();
		int alpha = buf.readInt();
		return new Color(red, green, blue, alpha);
	}

	public static void writeColor(Color color, ChannelBuffer buf) {
		buf.writeInt(color.getRed());
		buf.writeInt(color.getGreen());
		buf.writeInt(color.getBlue());
		buf.writeInt(color.getAlpha());
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ChannelBufferUtils() {
	}
}
