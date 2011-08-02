// Author: Zeerix, used with permission

package org.getspout.spout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.World;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket51MapChunkUncompressed;
import org.getspout.spoutapi.packet.listener.Listeners;

public final class MapChunkThread implements Runnable {

	// configuration
	private static final int QUEUE_CAPACITY = 1024 * 10; // how many packets can be queued before the main thread blocks

	private static final int CHUNK_SIZE = 16 * 128 * 16 * 5 / 2;
	private static final int REDUCED_DEFLATE_THRESHOLD = CHUNK_SIZE / 4;

	private static final int DEFLATE_LEVEL_CHUNKS = 6; // big chunks gets compressed well
	private static final int DEFLATE_LEVEL_PARTS = 1; // small data gets compressed fast (reduced CPU load)

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

	// deflater stuff
	private final Deflater deflater = new Deflater();
	private byte[] deflateBuffer = new byte[CHUNK_SIZE + 100];

	// worker thread queue
	private final HashMap<EntityPlayer, Integer> queueSizePerPlayer = new HashMap<EntityPlayer, Integer>();
	private final BlockingQueue<QueuedPacket> queue = new LinkedBlockingQueue<QueuedPacket>(QUEUE_CAPACITY);

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
		synchronized (queueSizePerPlayer) {
			for (EntityPlayer player : players) {
				Integer count = queueSizePerPlayer.get(player);
				queueSizePerPlayer.put(player, (count == null ? 0 : count) + amount);
			}
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
			if (!Listeners.canSendUncompressedPacket(p,MCPacket)) {
				return;
			}
			handleMapChunk(task);
		}
		sendToNetworkQueue(task);
	}

	private void handleMapChunk(QueuedPacket task) {
		Packet51MapChunk packet = (Packet51MapChunk) task.packet;

		// compress packet.g
		int dataSize = packet.g.length;
		if (deflateBuffer.length < dataSize + 100)
			deflateBuffer = new byte[dataSize + 100];

		deflater.reset();
		deflater.setLevel(dataSize < REDUCED_DEFLATE_THRESHOLD ? DEFLATE_LEVEL_PARTS : DEFLATE_LEVEL_CHUNKS);
		deflater.setInput(packet.g);
		deflater.finish();
		int size = deflater.deflate(deflateBuffer);
		if (size == 0) {
			size = deflater.deflate(deflateBuffer);
		}

		// copy compressed data to packet
		packet.g = new byte[size];
		try {
			Field ph = Packet51MapChunk.class.getDeclaredField("h");
			ph.setAccessible(true);
			ph.set(packet, size);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		System.arraycopy(deflateBuffer, 0, packet.g, 0, size);
	}

	private void sendToNetworkQueue(QueuedPacket task) {
		for (EntityPlayer player : task.players) {
			if (task.coords == null || player.playerChunkCoordIntPairs.contains(task.coords)) {
				((SpoutNetServerHandler) player.netServerHandler).sendPacket2(task.packet);
			}
		}
	}

	// producer
	private void putTask(QueuedPacket task) {
		if(instance.kill.get()) {
			throw new RuntimeException("MapChunkData: attempting to add task to queue after thread has been killed");
		}
		addToQueueSize(task.players, +1);

		while (true) {
			try {
				queue.put(task);
				return;
			} catch (InterruptedException e) {
				// TODO: ignore?
			}
		}
	}

	public static int getQueueLength(EntityPlayer player) {
		synchronized (instance.queueSizePerPlayer) {
			Integer count = instance.queueSizePerPlayer.get(player);
			return count == null ? 0 : count;
		}
	}

	public static void sendPacket(EntityPlayer player, Packet packet) {
		instance.putTask(new QueuedPacket(null, new EntityPlayer[] { player }, packet, false));
	}

	public static void sendPacket(ChunkCoordIntPair coords, List<EntityPlayer> players, Packet packet) {
		instance.putTask(new QueuedPacket(coords, players.toArray(new EntityPlayer[0]), packet, false));
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, EntityPlayer player, World world) {
		sendPacketMapChunk(coords, new EntityPlayer[] { player }, coords.x * 16, 0, coords.z * 16, 16, 128, 16, world);
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, List<EntityPlayer> players, int x, int y, int z, int dx, int dy, int dz, World world) {
		sendPacketMapChunk(coords, players.toArray(new EntityPlayer[0]), x, y, z, dx, dy, dz, world);
	}

	public static void sendPacketMapChunk(ChunkCoordIntPair coords, EntityPlayer[] players, int x, int y, int z, int dx, int dy, int dz, World world) {
		// create packet with uncompressed data to be compressed by worker thread
		Packet51MapChunk mapChunk = new Packet51MapChunk();
		mapChunk.a = x;
		mapChunk.b = y;
		mapChunk.c = z;
		mapChunk.d = dx;
		mapChunk.e = dy;
		mapChunk.f = dz;
		mapChunk.g = world.getMultiChunkData(x, y, z, dx, dy, dz);

		instance.putTask(new QueuedPacket(coords, players, mapChunk, true));
	}
}
