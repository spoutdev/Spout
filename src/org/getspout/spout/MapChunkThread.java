// Author: Zeerix, used with permission

package org.getspout.spout;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.World;

import org.bukkit.entity.Player;
import org.getspout.spout.chunkcache.ChunkCache;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.packet.listener.PacketListeners;
import org.getspout.spout.packet.standard.MCCraftPacket51MapChunkUncompressed;

public final class MapChunkThread implements Runnable {

	// configuration
	private static final int QUEUE_CAPACITY = 1024 * 10; // how many packets can be queued before the main thread blocks

	// singleton
	private static final MapChunkThread instance = new MapChunkThread();
	private static Thread thread = null;
	private static boolean runs = false;

	private static MCCraftPacket51MapChunkUncompressed MCPacket = new MCCraftPacket51MapChunkUncompressed();

	public static void startThread() {
		if (!runs) {
			runs = true;
			thread = new Thread(instance);
			thread.start();
		}
	}

	public static void endThread() {
		instance.kill.set(true);
		if (thread != null) {
			thread.interrupt();
		}
		try {
			thread.join();
		} catch (InterruptedException ie) {
		}
		thread = null;
		while(!instance.queue.isEmpty()) {
			instance.handle(instance.queue.poll());
		}
	}

	// worker thread queue
	private final ConcurrentHashMap<Integer, AtomicInteger> queueSizePerPlayer = new ConcurrentHashMap<Integer, AtomicInteger>();
	private final LinkedBlockingDeque<QueuedPacket> queue = new LinkedBlockingDeque<QueuedPacket>(QUEUE_CAPACITY);

	private final AtomicBoolean kill = new AtomicBoolean(false);

	private static class QueuedPacket {
		final ChunkCoordIntPair coords;
		final EntityPlayer[] players;
		final Packet packet;
		final boolean compress;

		QueuedPacket(ChunkCoordIntPair coords, EntityPlayer[] players, Packet packet, boolean compress) {
			this.coords = coords;
			this.players = players;
			this.packet = packet;
			this.compress = compress;
		}
	}

	// utility methods
	private void addToQueueSize(EntityPlayer[] players, int amount) {
		for (EntityPlayer player : players) {
			AtomicInteger count = queueSizePerPlayer.get(player.id);
			
			if (count == null) {
				count = new AtomicInteger(0);
				AtomicInteger current = queueSizePerPlayer.putIfAbsent(player.id, count);
				if (current != null) {
					count = current;
				}
			}
			count.addAndGet(amount);
		}
	}

	// consumer thread
	public void run() {
		while (thread != null && !thread.isInterrupted() && !kill.get()) {
			try {
				handle(queue.take());
			} catch (InterruptedException ie) {
				thread.interrupt();
			} catch (Exception e) {
				e.printStackTrace(); // print & ignore
			}
		}
	}

	private void handle(QueuedPacket task) {
		addToQueueSize(task.players, -1);
		if (task.compress) {
			Player p = task.players.length == 1 ? (Player)task.players[0].getBukkitEntity() : null;
			MCPacket.setPacket(task.packet, 51);
			if (!PacketListeners.canSendUncompressedPacket(p,MCPacket)) {
				return;
			}
			handleMapChunk(task);
		} else if (task.packet instanceof CustomPacket) {
			if(!ChunkCache.handleCustomPacket(task.players, (CustomPacket)task.packet)) {
				return;
			}
		}
		sendToNetworkQueue(task);
	}

	private void handleMapChunk(QueuedPacket task) {
		Packet51MapChunk packet = (Packet51MapChunk) task.packet;

		try {
			packet.rawData = ChunkCache.cacheChunk(task.players, packet.rawData);
		} catch (NoSuchFieldError e) {
		}

	}

	private void sendToNetworkQueue(QueuedPacket task) {
		for (EntityPlayer player : task.players) {
			if (task.coords == null || player.playerChunkCoordIntPairs.contains(task.coords)) {
				if (player.netServerHandler.getClass().equals(SpoutNetServerHandler.class)) {
					((SpoutNetServerHandler) player.netServerHandler).queueOutputPacket(task.packet);
				}
				else {
					player.netServerHandler.sendPacket(task.packet);
				}
			}
		}
	}

	// producer
	private void putTask(QueuedPacket task, boolean skip) {
		if(instance.kill.get()) {
			throw new RuntimeException("MapChunkData: attempting to add task to queue after thread has been killed");
		}
		addToQueueSize(task.players, +1);

		while (true) {
			try {
				if (skip) {
					queue.putFirst(task);
				} else {
					queue.put(task);
				}
				return;
			} catch (InterruptedException e) {
				// TODO: ignore?
			}
		}
	}

	public static int getQueueLength(EntityPlayer player) {
		AtomicInteger count = instance.queueSizePerPlayer.get(player.id);
		return count == null ? 0 : count.get();
	}
	
	public static void removeId(int id) {
		instance.queueSizePerPlayer.remove(id);
	}

	public static void sendPacket(EntityPlayer player, Packet packet) {
		instance.putTask(new QueuedPacket(null, new EntityPlayer[] { player }, packet, false), false);
	}

	public static void sendPacketSkipQueue(EntityPlayer player, Packet packet) {
		instance.putTask(new QueuedPacket(null, new EntityPlayer[] { player }, packet, false), true);
	}

	public static void sendPacket(ChunkCoordIntPair coords, List<EntityPlayer> players, Packet packet) {
		instance.putTask(new QueuedPacket(coords, players.toArray(new EntityPlayer[0]), packet, false), false);
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, EntityPlayer player, World world) {
		sendPacketMapChunk(coords, new EntityPlayer[] { player }, coords.x * 16, 0, coords.z * 16, 16, 128, 16, world);
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, List<EntityPlayer> players, int x, int y, int z, int dx, int dy, int dz, World world) {
		sendPacketMapChunk(coords, players.toArray(new EntityPlayer[0]), x, y, z, dx, dy, dz, world);
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, EntityPlayer[] players, int x, int y, int z, int dx, int dy, int dz, World world) {
		// create packet with uncompressed data to be compressed by worker thread
		Packet51MapChunk mapChunk = new Packet51MapChunk(x, y, z, dx, dy, dz, world);

		instance.putTask(new QueuedPacket(coords, players, mapChunk, true), false);
	}
}
