/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.getspout.api.util.Color;

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

			if (color.getRedF() == -1F)
				flags |= FLAG_COLORINVALID;
			else if (color.getRedF() == -2F)
				flags |= FLAG_COLOROVERRIDE;

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

			if ((flags & FLAG_COLORINVALID) > 0)
				return Color.invalid();
			if ((flags & FLAG_COLOROVERRIDE) > 0)
				return Color.override();

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
		for (int i = 0; i < ints.length; i++) {
			output.writeInt(ints[i]);
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
		for (int i = 0; i < floats.length; i++) {
			writeQuadFloat(output, floats[i]);
		}
	}
	public static void writeVector3(DataOutputStream output, Vector3 vector) throws IOException{
		output.writeDouble(vector.getX());
		output.writeDouble(vector.getY());
		output.writeDouble(vector.getZ());
	}
	public static Vector3 readVector3(DataInputStream input) throws IOException{
		return new Vector3(input.readDouble(), input.readDouble(), input.readDouble());
	}
	
	public static void writeVector2(DataOutputStream output, Vector2 vector) throws IOException{
		output.writeDouble(vector.getX());
		output.writeDouble(vector.getY());
	}
	public static Vector2 readVector2(DataInputStream input) throws IOException{
		return new Vector2(input.readDouble(), input.readDouble());
	}

}
