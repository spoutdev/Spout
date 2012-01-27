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
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.util.Color;

public class WidgetHandler extends MessageHandler {

	/** Maximum default string length. */
	public static final int MAXSTRING = 0x7FFF;

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final byte value) {
		return 1;
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final short value) {
		return 2;
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final int value) {
		return getNumBytes(value, true);
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return number of bytes
	 */
	public static int getNumBytes(final int value, final boolean packed) {
		int size = 0;
		final int val = Math.abs(value);
		if (packed) {
			if (val < 0x40) {
				size = 1;
			} else if (val < 0x2000) {
				size = 2;
			} else if (val < 0x100000) {
				size = 3;
			} else if (val < 0x08000000) {
				size = 4;
			} else {
				size = 5;
			}
		} else {
			size = 4;
		}
		return size;
	}

	/**
	 * Read an integer from a network stream.
	 * @param input network stream
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return the value
	 * @throws IOException on network error
	 */
	public static int readInt(final DataInputStream input)
			throws IOException {
		return readInt(input, true);
	}

	/**
	 * Read an integer from a network stream.
	 * @param input network stream
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return the value
	 * @throws IOException on network error
	 */
	public static int readInt(final DataInputStream input, final boolean packed)
			throws IOException {
		int value;
		if (packed) {
			value = input.readByte();
			final boolean sign = (value & 0x40) > 0;
			if (sign) {
				value &= ~0x40;
			}
			if ((value & 0x80) > 0) {
				value = (value & 0x7F) + (input.readByte() << 6);
			}
			if ((value & 0x4000) > 0) {
				value = (value & 0x3FFF) + (input.readByte() << 13);
			}
			if ((value & 0x200000) > 0) {
				value = (value & 0x1FFFFF) + (input.readByte() << 20);
			}
			if ((value & 0x10000000) > 0) {
				value = (value & 0x0FFFFFFF) + (input.readByte() << 27);
			}
			if (sign) {
				value = -value;
			}
		} else {
			value = input.readInt();
		}
		return value;
	}

	/**
	 * Send an integer to a network stream.
	 * @param output network stream
	 * @param value the value
	 * @throws IOException on network error
	 */
	public static void writeInt(final DataOutputStream output, final int value)
			throws IOException {
		writeInt(output, value, true);
	}
	/**
	 * Send an integer to a network stream.
	 * @param output network stream
	 * @param value the value
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @throws IOException on network error
	 */
	public static void writeInt(final DataOutputStream output, final int value, final boolean packed)
			throws IOException {
		if (packed) {
			final int sign = value >= 0 ? 0x00 : 0x40, val = Math.abs(value);
			byte[] out;
			if (val < 0x40) {
				out = new byte[1];
				out[0] = (byte) (val | sign);
			} else if (val < 0x2000) {
				out = new byte[2];
				out[0] = (byte) (val & 0x3F | 0x80 | sign);
				out[1] = (byte) ((val >>> 6) & 0x7f);
			} else if (val < 0x100000) {
				out = new byte[3];
				out[0] = (byte) (val & 0x3F | 0x80 | sign);
				out[1] = (byte) ((val >>> 6) & 0x7f | 0x80);
				out[2] = (byte) ((val >>> 13) & 0x7f);
			} else if (val < 0x08000000) {
				out = new byte[4];
				out[0] = (byte) (val & 0x3F | 0x80 | sign);
				out[1] = (byte) ((val >>> 6) & 0x7f | 0x80);
				out[2] = (byte) ((val >>> 13) & 0x7f | 0x80);
				out[3] = (byte) ((val >>> 20) & 0x7f);
			} else {
				out = new byte[5];
				out[0] = (byte) (val & 0x3F | 0x80 | sign);
				out[1] = (byte) ((val >>> 6) & 0x7f | 0x80);
				out[2] = (byte) ((val >>> 13) & 0x7f | 0x80);
				out[3] = (byte) ((val >>> 20) & 0x7f | 0x80);
				out[4] = (byte) (val >>> 27);
			}
			output.write(out);
		} else {
			output.writeInt(value);
		}
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final boolean value) {
		return 1;
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final String value) {
		return getNumBytes(value, true);
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return number of bytes
	 */
	public static int getNumBytes(final String value, final boolean packed) {
		int size = 1;
		if (value != null) {
			if (packed) {
				size = value.length();
			} else {
				size = value.length() * 2;
			}
			size += getNumBytes(size, true);
		}
		return size;
	}

	/**
	 * Read a string from a network stream.
	 * @param input the network stream
	 * @return the string
	 * @throws IOException on network error
	 */
	public static String readString(final DataInputStream input)
			throws IOException {
		return readString(input, MAXSTRING);
	}

	/**
	 * Read a string from a network stream.
	 * @param input the network stream
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return the string
	 * @throws IOException on network error
	 */
	public static String readString(final DataInputStream input, final boolean packed)
			throws IOException {
		return readString(input, MAXSTRING);
	}

	/**
	 * Read a string from a network stream.
	 * @param input the network stream
	 * @param maxSize the maximum string length
	 * @return the string
	 * @throws IOException on network error
	 */
	public static String readString(final DataInputStream input, final int maxSize)
			throws IOException {
		return readString(input, maxSize, false);
	}

	/**
	 * Read a string from a network stream.
	 * @param input the network stream
	 * @param maxSize the maximum string length
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @return the string
	 * @throws IOException on network error
	 */
	public static String readString(final DataInputStream input, final int maxSize, final boolean packed)
			throws IOException {
		final int size = readInt(input, true);
		if (size > maxSize) {
			throw new IOException("Received string length longer than maximum allowed (" + size + " > " + maxSize + ")");
		} else if (size < 0) {
			throw new IOException("Received string length is less than zero! Weird string!");
		} else {
			final StringBuilder stringbuilder = new StringBuilder();
			for (int j = 0; j < size; ++j) {
				stringbuilder.append(packed ? input.readByte() : input.readChar());
			}
			return stringbuilder.toString();
		}
	}

	/**
	 * Send a string to a network stream.
	 * @param output network stream
	 * @param string the string to send
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @throws IOException on network error
	 */
	public static void writeString(final DataOutputStream output, final String string)
			throws IOException {
		writeString(output, string, false);
	}

	/**
	 * Send a string to a network stream.
	 * @param output network stream
	 * @param string the string to send
	 * @param packed if it should be packed (trade cpu for bandwidth)
	 * @throws IOException on network error
	 */
	public static void writeString(final DataOutputStream output, final String string, final boolean packed)
			throws IOException {
		writeInt(output, string.length(), true);
		if (packed) {
			output.write(string.getBytes());
		} else {
			output.writeChars(string);
		}
	}

	/**
	 * Read a color from a network stream.
	 * @param input the network stream
	 * @return the color
	 * @throws IOException on network error
	 */
	public static Color readColor(final DataInputStream input)
			throws IOException {
		return new Color(input.readInt());
	}

	/**
	 * Send a color to a network stream.
	 * @param output network stream
	 * @param color the color to send
	 * @throws IOException on network error
	 */
	public static void writeColor(final DataOutputStream output, final Color color)
			throws IOException {
		output.writeInt(color.toInt());
	}

	/**
	 * Get the number of bytes a given datatype will use.
	 * @param value the value to check
	 * @return number of bytes
	 */
	public static int getNumBytes(final Color value) {
		return 4;
	}
}
