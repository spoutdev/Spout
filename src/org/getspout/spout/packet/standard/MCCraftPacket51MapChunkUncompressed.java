/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.packet.standard;

import org.getspout.spoutapi.packet.standard.MCPacket51MapChunkUncompressed;

public class MCCraftPacket51MapChunkUncompressed extends MCCraftPacket51MapChunk implements MCPacket51MapChunkUncompressed {

	public byte[] getCompressedChunkData() {
		throw new IllegalStateException("MCCraftPacket51MapChunkUncompressed packets don't have compressed chunk data");
	}

	public byte[] getUncompressedChunkData() {

		try {
			byte[] raw = super.getPacket().rawData; 
			if (raw == null) {
				return super.getCompressedChunkData();
			} else {
				return raw;
			}
		} catch (NoSuchFieldError e) {
			return super.getCompressedChunkData();
		}
	}

}
