package org.getspout.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;

import org.bukkit.inventory.ItemStack;

import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.NBTInputStream;
import org.getspout.server.util.nbt.NBTOutputStream;
import org.getspout.server.util.nbt.Tag;

import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.getspout.api.util.Color;

/**
 * Contains several {@link ChannelBuffer}-related utility methods.
 * @author Graham Edgecombe
 */
public final class ChannelBufferUtils {
	/**
	 * The UTF-8 character set.
	 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * Writes a list of parameters (e.g. mob metadata) to the buffer.
	 * @param buf The buffer.
	 * @param parameters The parameters.
	 */
	@SuppressWarnings("unchecked")
	public static void writeParameters(ChannelBuffer buf, List<Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			int type  = parameter.getType();
			int index = parameter.getIndex();

			buf.writeByte((type << 5) | index);

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
				buf.writeShort(item.getTypeId());
				buf.writeByte(item.getAmount());
				buf.writeShort(item.getDurability());
				break;				
			case Parameter.TYPE_VECTOR3:
				Vector3 vector = ((Parameter<Vector3>) parameter).getValue();
				buf.writeDouble(vector.getX());
				buf.writeDouble(vector.getY());
				buf.writeDouble(vector.getZ());
				break;
			case Parameter.TYPE_VECTOR2:
				Vector2 vect = ((Parameter<Vector2>) parameter).getValue();
				buf.writeDouble(vect.getX());
				buf.writeDouble(vect.getY());
				break;
			case Parameter.TYPE_COLOR:
				Color c = ((Parameter<Color>) parameter).getValue();
				buf.writeShort((short)c.getRedI());
				buf.writeShort((short)c.getBlueI());
				buf.writeShort((short)c.getGreenI());
				buf.writeShort((short)c.getAlphaI());
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
			int type  = (b & 0x0E) >> 5;
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
				int id = buf.readShort();
				int count = buf.readByte();
				short damage = buf.readShort();
				ItemStack item = new ItemStack(id, count, damage);
				parameters.add(new Parameter<ItemStack>(type, index, item));
				break;
			case Parameter.TYPE_VECTOR3:
				parameters.add(new Parameter<Vector3>(type, index, new Vector3(buf.readDouble(),buf.readDouble(),buf.readDouble())));
				break;
			case Parameter.TYPE_VECTOR2:
				parameters.add(new Parameter<Vector2>(type, index, new Vector2(buf.readDouble(),buf.readDouble())));
				break;
			case Parameter.TYPE_COLOR:
				parameters.add(new Parameter<Color>(type, index, new Color(buf.readShort(),buf.readShort(), buf.readShort())));
				break;
			}
		}

		return parameters;
	}

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

	public static Map<String, Tag> readCompound(ChannelBuffer buf) {
		int len = buf.readShort();
		if (len >= 0) {
			byte[] bytes = new byte[len];
			buf.readBytes(bytes);
			NBTInputStream str = null;
			try {
				str = new NBTInputStream(new ByteArrayInputStream(bytes));
				Tag tag = str.readTag();
				if (tag instanceof CompoundTag) {
					return ((CompoundTag) tag).getValue();
				}
			} catch (IOException e) {
			} finally {
				if (str != null) {
					try {
						str.close();
					} catch (IOException e) {}
				}
			}
		}
		return null;
	}

	public static void writeCompound(ChannelBuffer buf, Map<String, Tag> data) {
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
				} catch (IOException e) {}
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

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ChannelBufferUtils() {
	}
}
