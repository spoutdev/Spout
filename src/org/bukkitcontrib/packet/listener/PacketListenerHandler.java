package org.bukkitcontrib.packet.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.server.Packet;

import org.bukkitcontrib.packet.CorePacketType;

public class PacketListenerHandler {
	private static final EnumMap<CorePacketType, ArrayList<PacketListener>> listeners = new EnumMap<CorePacketType, ArrayList<PacketListener>>(
			CorePacketType.class);

	/**
	 * Add a packet listener for a specific packet type
	 * 
	 * @param type
	 *            packet type
	 * @param listener
	 *            the listener object
	 */
	public static void addListener(CorePacketType type, PacketListener listener) {
		if (!listeners.containsKey(type)) {
			listeners.put(type, new ArrayList<PacketListener>());
		}

		listeners.get(type).add(listener);
	}

	/**
	 * Remove a packet listener from the list of listeners
	 * 
	 * @param type
	 *            packet type
	 * @param listener
	 *            the listener object
	 * @return true if the listener was found and removed, false if it was not
	 *         found
	 */
	public static boolean removeListener(CorePacketType type,
			PacketListener listener) {
		if (!listeners.containsKey(type))
			return false;
		if (listeners.get(type).remove(listener)) {
			if (listeners.get(type).isEmpty()) {
				listeners.remove(type);
			}
			return true;
		}
		return false;
	}

	/**
	 * @return true if there are any packet listeners, false if there are none
	 */
	public static boolean hasListeners() {
		return !listeners.isEmpty();
	}

	/**
	 * @param type
	 *            packet type
	 * @return true if there are listeners for the given packet type, false if
	 *         there are none
	 */
	public static boolean hasListeners(CorePacketType type) {
		return listeners.containsKey(type);
	}

	/**
	 * Get a list of packet listeners for a type.
	 * 
	 * @param type
	 *            packet type
	 * @return a list of listeners for the specified type
	 */
	public static List<PacketListener> getListeners(CorePacketType type) {
		if (hasListeners(type))
			return listeners.get(type);
		return Collections.emptyList();
	}

	/**
	 * Check a packet to see if it can be sent.
	 * 
	 * @param type
	 *            packet type
	 * @param packet
	 *            the packet to check
	 * @return true if the packet is allowed, false if any listener said no
	 */
	public static boolean checkPacket(CorePacketType type, Packet packet) {
		for (PacketListener listener : getListeners(type)) {
			if (!listener.isAllowed(packet))
				return false;
		}
		return true;
	}
}
