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
package org.spout.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spout.api.util.list.ByteCircularBufferFIFO;

/**
 * Implements a variable integer format.  All integers are supported, but small positive integers are represented using fewer bytes.
 * 
 * 0 - 127:      1 byte
 * 128 - 32511:  2 bytes
 * Rest:         5 bytes
 */
public class VarInt {
	public static void writeString(OutputStream buf, String data) throws IOException {
		if (data == null) {
			writeInt(buf, -1);
			return;
		}

		writeInt(buf, data.length());

		for (int i = 0; i < data.length(); i++) {
			writeInt(buf, data.charAt(i) & 0xFFFF);
		}
	}

	public static void writeInt(ByteCircularBufferFIFO buf, int data) {
		if (data < 0 || data >= 0x00007F00) {
			buf.write((byte)(0xFF));
			buf.write((byte)(data >> 24));
			buf.write((byte)(data >> 16));
			buf.write((byte)(data >> 8));
			buf.write((byte)(data >> 0));
		} else if (data >= 0x00000080) {
			buf.write((byte)(0x80 | (data >> 8)));
			buf.write((byte)(       (data >> 0)));
		} else {
			buf.write((byte)data);
		}
	}
	
	public static void writeInt(OutputStream buf, int data) throws IOException {
		if (data < 0 || data >= 0x00007F00) {
			buf.write((byte)(0xFF));
			buf.write((byte)(data >> 24));
			buf.write((byte)(data >> 16));
			buf.write((byte)(data >> 8));
			buf.write((byte)(data >> 0));
		} else if (data >= 0x00000080) {
			buf.write((byte)(0x80 | (data >> 8)));
			buf.write((byte)(       (data >> 0)));
		} else {
			buf.write((byte)data);
		}
	}
	
	public static String readString(InputStream buf) throws IOException {
		int length = readInt(buf);
		if (length == -1) {
			return null;
		}

		char[] data = new char[length];
		for (int i = 0; i < length; i++) {
			data[i] = (char)readInt(buf);
		}
		return new String(data);
	}

	public static int readInt(ByteCircularBufferFIFO buf) {

		int b1 = buf.read();
		if (b1 == -1) {
			throw new IllegalStateException("FIFO is empty when trying to read integer");
		}

		if (b1 == 255) {
			byte[] arr = new byte[4];
			if (buf.read(arr) != 4) {
				throw new IllegalStateException("FIFO ran out of bytes when trying to read integer");
			}

			int data = 0;
			data |= (arr[0] & 0xFF) << 24;
			data |= (arr[1] & 0xFF) << 16;
			data |= (arr[2] & 0xFF) << 8;
			data |= (arr[3] & 0xFF) << 0;
			return data;
		} else if ((b1 & 0x80) == 0x80) {
			int b2 = buf.read();
			return ((b1 << 8) | (b2 & 0xFF)) & 0x7FFF;
		} else {
			return b1;
		}
	}
	
	public static int readInt(InputStream buf) throws IOException {

		int b1 = buf.read();
		if (b1 == -1) {
			throw new IllegalStateException("InputStream reached end when trying to read integer");
		}

		int data = 0;
		if (b1 == 255) {
			byte[] arr = new byte[4];
			if (buf.read(arr) != 4) {
				throw new IllegalStateException("InputStream ran out of bytes when trying to read integer");
			}
			data |= (arr[0] & 0xFF) << 24;
			data |= (arr[1] & 0xFF) << 16;
			data |= (arr[2] & 0xFF) << 8;
			data |= (arr[3] & 0xFF) << 0;
		} else if ((b1 & 0x80) == 0x80) {
			int b2 = buf.read();
			return ((b1 << 8) | (b2 & 0xFF)) & 0x7FFF;
		} else {
			return b1;
		}
		return data;
	}

}
