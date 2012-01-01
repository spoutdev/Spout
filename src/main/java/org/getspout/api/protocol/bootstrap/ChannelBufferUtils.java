package org.getspout.api.protocol.bootstrap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.getspout.api.inventory.ItemStack;
import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.api.io.nbt.NBTInputStream;
import org.getspout.api.io.nbt.NBTOutputStream;
import org.getspout.api.io.nbt.Tag;
import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.getspout.api.util.Color;
import org.getspout.api.util.Parameter;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Contains several {@link org.jboss.netty.buffer.ChannelBuffer}-related utility methods.
 * @author Graham Edgecombe
 */

// TODO - move to vanilla ?
public final class ChannelBufferUtils {
	/**
	 * The UTF-8 character set.
	 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * Writes a string to the buffer.
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long
	 * <em>after</em> it is encoded.
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
	 * @throws IllegalArgumentException if the string is too long
	 * <em>after</em> it is encoded.
	 */
	public static void writeUtf8String(ChannelBuffer buf, String str) {
		try {
			byte[] bytes = str.getBytes(CHARSET_UTF8.name());
			if (bytes.length >= 65536) {
				throw new IllegalArgumentException("Encoded UTF-8 string too long.");
			}

			buf.writeShort(bytes.length);
			buf.writeBytes(bytes);
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException("Unable to find UTF8 encoding system");
		}
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

		try {
			return new String(bytes, CHARSET_UTF8.name());
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException("Unable to find UTF8 encoding system");
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

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ChannelBufferUtils() {
	}
}
