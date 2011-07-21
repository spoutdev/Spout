package org.bukkitcontrib.packet.listener;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

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
	private static final WriteLock lockWrite;
	private static final ReadLock lockRead;
	static {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		lockWrite = lock.writeLock();
		lockRead = lock.readLock();
	}

	public static boolean canSend(Packet packet) {
		lockRead.lock();
		try {
			for (Listener listener : listeners[packet.b()]) {
				if (!listener.checkPacket(packet))
					return false;
			}
			return true;
		} finally {
			lockRead.unlock();
		}
	}

	public static void addListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return;

		lockWrite.lock();
		try {
			listeners[packetId] = Arrays.copyOf(listeners[packetId], listeners[packetId].length + 1);
			listeners[packetId][listeners[packetId].length - 1] = listener;
		} finally {
			lockWrite.unlock();
		}
	}

	public static boolean removeListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return false;

		lockWrite.lock();
		try {
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
		} finally {
			lockWrite.unlock();
		}
	}

	public static boolean hasListeners(int packetId) {
		if (packetId < 0 || packetId > 255)
			return false;

		lockRead.lock();
		try {
			return listeners[packetId].length > 0;
		} finally {
			lockRead.unlock();
		}
	}

	public static boolean hasListeners() {
		lockRead.lock();
		try {
			for (Listener[] packetListeners : listeners) {
				if (packetListeners.length > 0)
					return true;
			}
			return false;
		} finally {
			lockRead.unlock();
		}
	}

	public static boolean hasListener(int packetId, Listener listener) {
		if (packetId < 0 || packetId > 255)
			return false;

		lockRead.lock();
		try {
			for (Listener packetListener : listeners[packetId]) {
				if (packetListener == listener)
					return true;
			}
			return false;
		} finally {
			lockRead.unlock();
		}
	}

	public static void clearAllListeners() {
		lockWrite.lock();
		try {
			listeners = new Listener[256][0];
		} finally {
			lockWrite.unlock();
		}
	}
}
