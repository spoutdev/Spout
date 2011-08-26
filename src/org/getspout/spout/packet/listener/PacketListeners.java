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
package org.getspout.spout.packet.listener;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.server.Packet;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spout.packet.standard.MCCraftPacketUnknown;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

/**
 * Keeps track of packet listeners
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PacketListeners {
	/**
	 * Private constructor to avoid initialization
	 */
	private PacketListeners() {
	}

	private final static AtomicReference[] listeners;

	static {
		listeners = new AtomicReference[257];
		for (int i = 0; i < listeners.length; i++) {
			listeners[i] = new AtomicReference<PacketListener[]>();
		}
		clearAllListeners();
	}

	public static boolean canSendUncompressedPacket(Player player, MCCraftPacket MCPacket) {
		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[256];
		PacketListener[] listenerArray = listenerReference.get();
		if (listenerArray != null) {
			for (PacketListener listener : listenerArray) {
				if (!listener.checkPacket(player, MCPacket))
					return false;
			}
		}
		return true;
	}

	public static boolean canSend(Player player, Packet packet, MCCraftPacket[] packetWrappers, int packetId) {
		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[packetId];
		PacketListener[] listenerArray = listenerReference.get();
		if (listenerArray != null) {
			MCPacket wrapper = wrapPacket(packet, packetWrappers, packetId);
			for (PacketListener listener : listenerArray) {
				if (!listener.checkPacket(player, wrapper))
					return false;
			}
		}
		return true;
	}

	private static MCCraftPacket unknownPacket = new MCCraftPacketUnknown();

	private static MCPacket wrapPacket(Packet packet, MCCraftPacket[] packetWrappers, int packetId) {
		MCCraftPacket packetWrapper = packetWrappers[packetId];
		if (packetWrapper == null) {
			packetWrapper = MCCraftPacket.newInstance(packetId, packet);
			packetWrappers[packetId] = packetWrapper;
		} else {
			packetWrapper.setPacket(packet, packetId);
		}

		if (packetWrapper == null) {
			packetWrapper = unknownPacket;
			packetWrapper.setPacket(packet, packetId);
		}

		return packetWrapper;

	}

	public static void addListenerUncompressedChunk(PacketListener listener) {
		addListener2(256, listener);
	}

	public static void addListener(int packetId, PacketListener listener) {
		if (packetId > 255) {
			return;
		}
		addListener2(packetId, listener);
	}

	private static void addListener2(int packetId, PacketListener listener) {
		if (packetId < 0)
			return;

		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[packetId];

		boolean success = false;
		while (!success) {
			PacketListener[] oldListeners = listenerReference.get();
			PacketListener[] newListeners = Arrays.copyOf(oldListeners, oldListeners.length + 1);
			newListeners[oldListeners.length] = listener;
			success = listenerReference.compareAndSet(oldListeners, newListeners);
		}
	}

	public static boolean removeListenerUncompressedChunk(PacketListener listener) {
		return removeListener2(256, listener);
	}

	public static boolean removeListener(int packetId, PacketListener listener) {
		if (packetId > 255) {
			return false;
		}
		return removeListener2(packetId, listener);
	}

	private static boolean removeListener2(int packetId, PacketListener listener) {
		if (packetId < 0)
			return false;

		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[packetId];

		boolean success = false;
		while (!success) {
			PacketListener[] oldListeners = listenerReference.get();
			int index = -1;
			for (int i = 0; i < oldListeners.length; i++) {
				if (oldListeners[i] == listener) {
					index = i;
					break;
				}
			}
			if (index == -1)
				return false;

			PacketListener[] newListeners = new PacketListener[oldListeners.length - 1];
			System.arraycopy(oldListeners, 0, newListeners, 0, index);
			System.arraycopy(oldListeners, index + 1, newListeners, index, oldListeners.length - 1 - index);
			success = listenerReference.compareAndSet(oldListeners, newListeners);
		}
		return true;
	}

	public static boolean hasListeners(int packetId) {
		if (packetId < 0 || packetId > 256)
			return false;

		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[packetId];

		return listenerReference.get().length > 0;
	}

	public static boolean hasListeners() {
		for (int i = 0; i < listeners.length; i++) {
			if (hasListeners(i)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasListener(int packetId, PacketListener listener) {
		if (packetId < 0 || packetId > 256)
			return false;

		AtomicReference<PacketListener[]> listenerReference = (AtomicReference<PacketListener[]>) listeners[packetId];

		for (PacketListener packetListener : listenerReference.get()) {
			if (packetListener == listener)
				return true;
		}
		return false;
	}

	public static void clearAllListeners() {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].set(new PacketListener[0]);
		}
	}
}