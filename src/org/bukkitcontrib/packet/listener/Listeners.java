package org.bukkitcontrib.packet.listener;

import java.util.Arrays;
import net.minecraft.server.Packet;

import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Keeps track of packet listeners
 * 
 */
public class Listeners {
	/**
	 * Private constructor to avoid initialization
	 */
	private Listeners() {}

	private final static AtomicReference[] listeners;

	static {
		listeners = new AtomicReference[256];
		for(int i = 0; i < listeners.length; i++) {
			listeners[i] = new AtomicReference<Listener[]>();
		}
		clearAllListeners();
	}

	public static boolean canSend(Player player, Packet packet) {
		AtomicReference<Listener[]> listenerReference = (AtomicReference<Listener[]>)listeners[packet.b()];
		Listener[] listenerArray = listenerReference.get();
		for (Listener listener : listenerArray) {
			if (!listener.checkPacket(player, packet))
				return false;
		}
		return true;
	}

	public static void addListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return;

		AtomicReference<Listener[]> listenerReference = (AtomicReference<Listener[]>)listeners[packetId];

		boolean success = false;
		while(!success) {
			Listener[] oldListeners = listenerReference.get();
			Listener[] newListeners = Arrays.copyOf(oldListeners, oldListeners.length + 1);
			newListeners[oldListeners.length] = listener;
			success = listenerReference.compareAndSet(oldListeners, newListeners);
		}
	}

	public static boolean removeListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return false;

		AtomicReference<Listener[]> listenerReference = (AtomicReference<Listener[]>)listeners[packetId];
		
		boolean success = false;
		while(!success) {
			Listener[] oldListeners = listenerReference.get();
			int index = -1;
			for (int i = 0; i < oldListeners.length; i++) {
				if (oldListeners[i] == listener) {
					index = i;
					break;
				}
			}
			if (index == -1)
				return false;

			Listener[] newListeners = new Listener[oldListeners.length - 1];
			System.arraycopy(oldListeners, 0, newListeners, 0, index);
			System.arraycopy(oldListeners, index + 1, newListeners, index, oldListeners.length - 1 - index);
			success = listenerReference.compareAndSet(oldListeners, newListeners);
		}
		return true;
	}

	public static boolean hasListeners(int packetId) {
		if (packetId < 0 || packetId > 255)
			return false;

		AtomicReference<Listener[]> listenerReference = (AtomicReference<Listener[]>)listeners[packetId];

		return listenerReference.get().length > 0;
	}

	public static boolean hasListeners() {
		for(int i = 0; i < listeners.length; i++) {
			if(hasListeners(i)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return false;

		AtomicReference<Listener[]> listenerReference = (AtomicReference<Listener[]>)listeners[packetId];

		for (Listener packetListener : listenerReference.get()) {
			if (packetListener == listener)
				return true;
		}
		return false;
	}

	public static void clearAllListeners() {
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].set(new Listener[0]);
		}
	}
}
