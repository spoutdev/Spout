package org.bukkitcontrib.packet.listener;

import java.util.Arrays;

import net.minecraft.server.Packet;

/**
 * Keeps track of packet listeners
 * 
 * @author Nightgunner5
 */
public class Listeners {
	/**
	 * Private constructor to avoid initialization
	 */
	private Listeners() {}

	private static Listener[][] listeners = new Listener[256][0];

	public static boolean canSend(Packet packet) {
		for (Listener listener : listeners[packet.b()]) {
			if (!listener.checkPacket(packet))
				return false;
		}
		return true;
	}

	public static void addListener(int packetId, Listener listener) {
		if (packetId >= listeners.length)
			return;

		listeners[packetId] = Arrays.copyOf(listeners[packetId], listeners[packetId].length + 1);
		listeners[packetId][listeners[packetId].length - 1] = listener;
	}

	public static boolean removeListener(int packetId, Listener listener) {
		if (packetId >= listeners.length)
			return false;

		int index = -1;
		for (int i = 0; i < listeners[packetId].length; i++) {
			if (listeners[packetId][i] == listener) {
				index = i;
				break;
			}
		}
		if (index == -1)
			return false;

		Listener[] oldListeners = listeners[packetId];
		listeners[packetId] = new Listener[oldListeners.length - 1];
		System.arraycopy(oldListeners, 0, listeners[packetId], 0, index);
		System.arraycopy(oldListeners, index + 1, listeners[packetId], index, oldListeners.length - 1 - index);

		return true;
	}

	public static boolean hasListeners(int packetId) {
		if (packetId >= listeners.length)
			return false;

		return listeners[packetId].length > 0;
	}

	public static boolean hasListeners() {
		for (Listener[] packetListeners : listeners) {
			if (packetListeners.length > 0)
				return true;
		}
		return false;
	}

	public static boolean hasListener(int packetId, Listener listener) {
		if (packetId >= listeners.length)
			return false;

		for (Listener packetListener : listeners[packetId]) {
			if (packetListener == listener)
				return true;
		}
		return false;
	}

	public static void clearAllListeners() {
		listeners = new Listener[256][0];
	}
}
