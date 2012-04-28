package org.spout.api.util;

import org.spout.api.util.list.ByteCircularBufferFIFO;

/**
 * Implements a variable integer format.  All integers are supported, but small positive integers are represented using fewer bytes.
 * 
 * 0 - 127:      1 byte
 * 128 - 32511:  2 bytes
 * Rest:         5 bytes
 */
public class VarInt {

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

	public static int readInt(ByteCircularBufferFIFO buf) {

		int b1 = buf.read();
		if (b1 == -1) {
			throw new IllegalStateException("FIFO is empty when trying to read integer");
		}

		int data = 0;
		if (b1 == 255) {
			byte[] arr = new byte[4];
			if (buf.read(arr) != 4) {
				throw new IllegalStateException("FIFO ran out of bytes when trying to read integer");
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
