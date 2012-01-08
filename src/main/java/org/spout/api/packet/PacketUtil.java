/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.util.Color;

public abstract class PacketUtil {

	public static final int maxString = 32767;
	public static final byte FLAG_COLORINVALID = 1;
	public static final byte FLAG_COLOROVERRIDE = 2;

	public static void writeString(DataOutputStream output, String s) {
		try {
			output.writeShort(s.length());
			output.writeChars(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readString(DataInputStream input) {
		return readString(input, maxString);
	}

	public static int getNumBytes(String str) {
		if (str != null) {
			return 2 + str.length() * 2;
		}
		return 2;
	}

	public static String readString(DataInputStream input, int maxSize) {
		try {
			short size = input.readShort();

			if (size > maxSize) {
				throw new IOException("Received string length longer than maximum allowed (" + size + " > " + maxSize + ")");
			} else if (size < 0) {
				throw new IOException("Received string length is less than zero! Weird string!");
			} else {
				StringBuilder stringbuilder = new StringBuilder();

				for (int j = 0; j < size; ++j) {
					stringbuilder.append(input.readChar());
				}

				return stringbuilder.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeColor(DataOutputStream output, Color color) {
		try {
			byte flags = 0x0;

			if (color.getRedF() == -1F) {
				flags |= FLAG_COLORINVALID;
			} else if (color.getRedF() == -2F) {
				flags |= FLAG_COLOROVERRIDE;
			}

			output.writeByte(flags);
			output.writeInt(color.toInt());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Color readColor(DataInputStream input) {
		try {
			byte flags = input.readByte();
			int argb = input.readInt();

			if ((flags & FLAG_COLORINVALID) > 0) {
				return Color.invalid();
			}
			if ((flags & FLAG_COLOROVERRIDE) > 0) {
				return Color.override();
			}

			return new Color(argb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int[] readIntArray(DataInputStream input) throws IOException {
		int length = input.readInt();
		if (length > 256) {
			throw new IllegalArgumentException("Int array exceeded max length (" + length + ")");
		}
		int[] newArray = new int[length];
		for (int i = 0; i < length; i++) {
			newArray[i] = input.readInt();
		}
		return newArray;
	}

	public static float[] readQuadFloat(DataInputStream input) throws IOException {
		float[] newArray = new float[4];
		for (int i = 0; i < 4; i++) {
			newArray[i] = input.readFloat();
		}
		return newArray;
	}

	public static int getDoubleArrayLength(float[][] doubleArray) {
		return doubleArray.length * 16;
	}

	public static float[][] readDoubleArray(DataInputStream input) throws IOException {
		int length = input.readShort();
		if (length > 256) {
			throw new IllegalArgumentException("Double array exceeded max length (" + length + ")");
		}
		float[][] newDoubleArray = new float[length][];
		for (int i = 0; i < length; i++) {
			newDoubleArray[i] = readQuadFloat(input);
		}
		return newDoubleArray;
	}

	public static void writeIntArray(DataOutputStream output, int[] ints) throws IOException {
		if (ints.length > 256) {
			throw new IllegalArgumentException("Array containing " + ints.length + " ints passed to writeQuadFloat");
		}
		output.writeInt(ints.length);
		for (int j : ints) {
			output.writeInt(j);
		}
	}

	public static void writeQuadFloat(DataOutputStream output, float[] floats) throws IOException {
		if (floats.length != 4) {
			throw new IllegalArgumentException("Array containing " + floats.length + " floats passed to writeQuadFloat");
		}
		for (int i = 0; i < 4; i++) {
			output.writeFloat(floats[i]);
		}
	}

	public static void writeDoubleArray(DataOutputStream output, float[][] floats) throws IOException {
		if (floats.length > 256) {
			throw new IllegalArgumentException("Double array exceeded max length (" + floats.length + ")");
		}

		output.writeShort(floats.length);
		for (float[] f : floats) {
			writeQuadFloat(output, f);
		}
	}

	public static void writeVector3(DataOutputStream output, Vector3 vector) throws IOException {
		output.writeFloat(vector.getX());
		output.writeFloat(vector.getY());
		output.writeFloat(vector.getZ());
	}

	public static Vector3 readVector3(DataInputStream input) throws IOException {
		return new Vector3(input.readFloat(), input.readFloat(), input.readFloat());
	}

	public static void writeVector2(DataOutputStream output, Vector2 vector) throws IOException {
		output.writeFloat(vector.getX());
		output.writeFloat(vector.getY());
	}

	public static Vector2 readVector2(DataInputStream input) throws IOException {
		return new Vector2(input.readFloat(), input.readFloat());
	}

	/**
	 * Unpacks an integer from the smallest space for network use.
	 * @param input network stream
	 * @return the value
	 * @throws IOException on network error
	 */
	public static int readPacked(final DataInputStream input)
			throws IOException {
		int value = input.readByte();
		if ((value & 0x80) > 0) {
			value = (value & 0x7F) + (input.readByte() << 7);
		}
		if ((value & 0x4000) > 0) {
			value = (value & 0x3FFF) + (input.readByte() << 14);
		}
		if ((value & 0x200000) > 0) {
			value = (value & 0x1FFFFF) + (input.readByte() << 21);
		}
		if ((value & 0x10000000) > 0) {
			value = (value & 0x0FFFFFFF) + (input.readByte() << 28);
		}
		return value;
	}

	/**
	 * Packs an integer into the smallest space for network use.
	 * @param output network stream
	 * @param value the value
	 * @throws IOException on network error
	 */
	public static void writePacked(final DataOutputStream output, final int value)
			throws IOException {
		if (value < 0x80) {
			output.writeByte(value);
		} else if (value < 0x4000) {
			output.writeByte(value & 0x7F | 0x80);
			output.writeByte((value & 0x3F80) >>> 7);
		} else if (value < 0x200000) {
			output.writeByte(value & 0x7F | 0x80);
			output.writeByte((value & 0x3FFF | 0x4000) >>> 7);
			output.writeByte((value & 0x1FFFFF) >>> 14);
		} else if (value < 0x10000000) {
			output.writeByte(value & 0x7F | 0x80);
			output.writeByte((value & 0x3FFF | 0x4000) >>> 7);
			output.writeByte((value & 0x1FFFFF | 0x200000) >>> 14);
			output.writeByte((value & 0x0FFFFFFF) >>> 21);
		} else {
			output.writeByte(value & 0x7F | 0x80);
			output.writeByte((value & 0x3FFF | 0x4000) >>> 7);
			output.writeByte((value & 0x1FFFFF | 0x200000) >>> 14);
			output.writeByte((value & 0x0FFFFFFF | 0x10000000) >>> 21);
			output.writeByte(value >>> 28);
		}
	}

	/**
	 * Gets the number of bytes it will take to send a packed number.
	 * @param value the number to pack
	 * @return the number of bytes
	 */
	public static int getPackedSize(final int value) {
		int size = 0;
		if (value < 0x80) {
			size = 1;
		} else if (value < 0x4000) {
			size = 2;
		} else if (value < 0x200000) {
			size = 3;
		} else if (value < 0x10000000) {
			size = 4;
		} else {
			size = 5;
		}
		return size;
	}
}
