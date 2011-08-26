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
package org.getspout.spout.packet;

import org.getspout.spout.packet.listener.PacketListeners;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.PacketManager;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class SimplePacketManager implements PacketManager {

	public void addListener(int packetId, PacketListener listener) {
		PacketListeners.addListener(packetId, listener);	
	}

	public void addListenerUncompressedChunk(PacketListener listener) {
		PacketListeners.addListenerUncompressedChunk(listener);
	}

	public MCPacket getInstance(int packetId) {
		return MCCraftPacket.newInstance(packetId);
	}

	public boolean removeListener(int packetId, PacketListener listener) {
		return PacketListeners.removeListener(packetId, listener);
	}

	public boolean removeListenerUncompressedChunk(PacketListener listener) {
		return PacketListeners.removeListenerUncompressedChunk(listener);
	}
	
	public void clearAllListeners() {
		PacketListeners.clearAllListeners();
	}

}
