/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.io.nbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an
 * underlying {@link java.io.OutputStream}.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 * @author Graham Edgecombe
 */
public final class NBTOutputStream implements Closeable {
	/**
	 * The output stream.
	 */
	private final DataOutputStream os;

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the
	 * specified underlying output stream. This assumes the output stream
	 * should be compressed with GZIP.
	 * @param os The output stream.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os) throws IOException {
		this(os, true);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the
	 * specified underlying output stream. A flag indicates if the output
	 * should be compressed with GZIP or not.
	 * @param os The output stream.
	 * @param boolean A flag that indicates if the output should be compressed.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
		this.os = new DataOutputStream(compressed ? new GZIPOutputStream(os) : os);
	}

	/**
	 * Writes a tag.
	 * @param tag The tag to write.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public void writeTag(Tag tag) throws IOException {
		int type = NBTUtils.getTypeCode(tag.getClass());
		String name = tag.getName();
		byte[] nameBytes = name.getBytes(NBTConstants.CHARSET.name());

		os.writeByte(type);
		os.writeShort(nameBytes.length);
		os.write(nameBytes);

		if (type == NBTConstants.TYPE_END) {
			throw new IOException("Named TAG_End not permitted.");
		}

		writeTagPayload(tag);
	}

	/**
	 * Writes tag payload.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeTagPayload(Tag tag) throws IOException {
		int type = NBTUtils.getTypeCode(tag.getClass());
		switch (type) {
		case NBTConstants.TYPE_END:
			writeEndTagPayload((EndTag) tag);
			break;

		case NBTConstants.TYPE_BYTE:
			writeByteTagPayload((ByteTag) tag);
			break;

		case NBTConstants.TYPE_SHORT:
			writeShortTagPayload((ShortTag) tag);
			break;

		case NBTConstants.TYPE_INT:
			writeIntTagPayload((IntTag) tag);
			break;

		case NBTConstants.TYPE_LONG:
			writeLongTagPayload((LongTag) tag);
			break;

		case NBTConstants.TYPE_FLOAT:
			writeFloatTagPayload((FloatTag) tag);
			break;

		case NBTConstants.TYPE_DOUBLE:
			writeDoubleTagPayload((DoubleTag) tag);
			break;

		case NBTConstants.TYPE_BYTE_ARRAY:
			writeByteArrayTagPayload((ByteArrayTag) tag);
			break;

		case NBTConstants.TYPE_STRING:
			writeStringTagPayload((StringTag) tag);
			break;

		case NBTConstants.TYPE_LIST:
			writeListTagPayload((ListTag<?>) tag);
			break;

		case NBTConstants.TYPE_COMPOUND:
			writeCompoundTagPayload((CompoundTag) tag);
			break;

		default:
			throw new IOException("Invalid tag type: " + type + ".");
		}
	}

	/**
	 * Writes a {@code TAG_Byte} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeByteTagPayload(ByteTag tag) throws IOException {
		os.writeByte(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Byte_Array} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
		byte[] bytes = tag.getValue();
		os.writeInt(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Compound} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
		for (Tag childTag : tag.getValue().values()) {
			writeTag(childTag);
		}
		os.writeByte((byte) 0); // end tag - better way?
	}

	/**
	 * Writes a {@code TAG_List} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	@SuppressWarnings("unchecked")
	private void writeListTagPayload(ListTag<?> tag) throws IOException {
		Class<? extends Tag> clazz = tag.getType();
		List<Tag> tags = (List<Tag>) tag.getValue();
		int size = tags.size();

		os.writeByte(NBTUtils.getTypeCode(clazz));
		os.writeInt(size);
		for (int i = 0; i < size; i++) {
			writeTagPayload(tags.get(i));
		}
	}

	/**
	 * Writes a {@code TAG_String} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeStringTagPayload(StringTag tag) throws IOException {
		byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET.name());
		os.writeShort(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Double} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
		os.writeDouble(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Float} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeFloatTagPayload(FloatTag tag) throws IOException {
		os.writeFloat(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Long} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeLongTagPayload(LongTag tag) throws IOException {
		os.writeLong(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeIntTagPayload(IntTag tag) throws IOException {
		os.writeInt(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Short} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeShortTagPayload(ShortTag tag) throws IOException {
		os.writeShort(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Empty} tag.
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeEndTagPayload(EndTag tag) {
		/* empty */
	}

	public void close() throws IOException {
		os.close();
	}
}
