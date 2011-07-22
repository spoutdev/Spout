package org.bukkitcontrib;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Field;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.Block;
import net.minecraft.server.Packet53BlockChange;
import net.minecraft.server.Packet52MultiBlockChange;
import net.minecraft.server.Packet50PreChunk;
import net.minecraft.server.TileEntity;
import net.minecraft.server.WorldServer;

class ContribPlayerInstance {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void replacePlayerInstances(ContribPlayerManager manager) {
		List<Object> playerInstances;
		try {
			Class clazz = Class.forName("net.minecraft.server.PlayerManager");
			Field fc = clazz.getDeclaredField("c");
			fc.setAccessible(true);
			playerInstances = (List<Object>) fc.get(manager);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		List<Object> updatedPlayerInstances = new ArrayList<Object>();
		for (Object instance : playerInstances) {
			if (instance.getClass().getName().equals("PlayerInstance")) {
				updatedPlayerInstances.add(new ContribPlayerInstance(manager, instance));
				System.out.println("Updating player instance");
			} else {
				updatedPlayerInstances.add(instance);
			}
		}
		try {
			Class clazz = Class.forName("net.minecraft.server.PlayerManager");
			Field fc = clazz.getDeclaredField("c");
			fc.setAccessible(true);
			fc.set(manager, updatedPlayerInstances);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	public ContribPlayerInstance(ContribPlayerManager manager, Object instance) {
		this(manager, 0, 0);
		transferField(instance, this, "b");
		transferField(instance, this, "chunkX");
		transferField(instance, this, "chunkY");
		transferField(instance, this, "location");
		transferField(instance, this, "dirtyBlocks");
		transferField(instance, this, "dirtyCount");
		transferField(instance, this, "h");
		transferField(instance, this, "i");
		transferField(instance, this, "j");
		transferField(instance, this, "k");
		transferField(instance, this, "l");
		transferField(instance, this, "m");
	}

	private static void transferField(Object src, Object dest, String fieldName) {
		try {
			Field field = src.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object temp = field.get(src);
			field.set(dest, temp);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	private List b;
	private int chunkX;
	private int chunkZ;
	private ChunkCoordIntPair location;
	private short[] dirtyBlocks;
	private int dirtyCount;
	private int h;
	private int i;
	private int j;
	private int k;
	private int l;
	private int m;

	final ContribPlayerManager playerManager;

	@SuppressWarnings("rawtypes")
	public ContribPlayerInstance(ContribPlayerManager playermanager, int i, int j) {
		this.playerManager = playermanager;
		this.b = new ArrayList();
		this.dirtyBlocks = new short[10];
		this.dirtyCount = 0;
		this.chunkX = i;
		this.chunkZ = j;
		this.location = new ChunkCoordIntPair(i, j);
		playermanager.a().chunkProviderServer.getChunkAt(i, j);
	}

	@SuppressWarnings("unchecked")
	public void a(EntityPlayer entityplayer) {
		if (this.b.contains(entityplayer)) {
			throw new IllegalStateException("Failed to add player. " + entityplayer + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
		} else {
			// CraftBukkit start
			if (entityplayer.playerChunkCoordIntPairs.add(this.location)) {
				entityplayer.netServerHandler.sendPacket(new Packet50PreChunk(this.location.x, this.location.z, true));
			}
			// CraftBukkit end

			this.b.add(entityplayer);
			entityplayer.chunkCoordIntPairQueue.add(this.location);
		}
	}

	public void b(EntityPlayer entityplayer) {
		if (this.b.contains(entityplayer)) {
			this.b.remove(entityplayer);
			if (this.b.size() == 0) {
				long i = (long) this.chunkX + 2147483647L | (long) this.chunkZ + 2147483647L << 32;

				ContribPlayerManager.a(this.playerManager).b(i);
				if (this.dirtyCount > 0) {
					ContribPlayerManager.b(this.playerManager).remove(this);
				}

				this.playerManager.a().chunkProviderServer.queueUnload(this.chunkX, this.chunkZ);
			}

			entityplayer.chunkCoordIntPairQueue.remove(this.location);
			// CraftBukkit - contains -> remove -- TODO VERIFY!!!!
			if (entityplayer.playerChunkCoordIntPairs.remove(this.location)) {
				entityplayer.netServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void a(int i, int j, int k) {
		if (this.dirtyCount == 0) {
			ContribPlayerManager.b(this.playerManager).add(this);
			this.h = this.i = i;
			this.j = this.k = j;
			this.l = this.m = k;
		}

		if (this.h > i) {
			this.h = i;
		}

		if (this.i < i) {
			this.i = i;
		}

		if (this.j > j) {
			this.j = j;
		}

		if (this.k < j) {
			this.k = j;
		}

		if (this.l > k) {
			this.l = k;
		}

		if (this.m < k) {
			this.m = k;
		}

		if (this.dirtyCount < 10) {
			short short1 = (short) (i << 12 | k << 8 | j);

			for (int l = 0; l < this.dirtyCount; ++l) {
				if (this.dirtyBlocks[l] == short1) {
					return;
				}
			}

			this.dirtyBlocks[this.dirtyCount++] = short1;
		}
	}

	@SuppressWarnings("unchecked")
	public void sendAll(Packet packet) {
		MapChunkThread.sendPacket(this.location, this.b, packet);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void a() {
		WorldServer worldserver = this.playerManager.a();

		if (this.dirtyCount != 0) {
			int i;
			int j;
			int k;

			if (this.dirtyCount == 1) {
				i = this.chunkX * 16 + this.h;
				j = this.j;
				k = this.chunkZ * 16 + this.l;
				this.sendAll(new Packet53BlockChange(i, j, k, worldserver));
				if (Block.isTileEntity[worldserver.getTypeId(i, j, k)]) {
					this.sendTileEntity(worldserver.getTileEntity(i, j, k));
				}
			} else {
				int l;

				if (this.dirtyCount == 10) {
					this.j = this.j / 2 * 2;
					this.k = (this.k / 2 + 1) * 2;
					i = this.h + this.chunkX * 16;
					j = this.j;
					k = this.l + this.chunkZ * 16;
					l = this.i - this.h + 1;
					int i1 = this.k - this.j + 2;
					int j1 = this.m - this.l + 1;

					MapChunkThread.sendPacketMapChunk(this.location, this.b, i, j, k, l, i1, j1, worldserver);    
					List list = worldserver.getTileEntities(i, j, k, i + l, j + i1, k + j1);

					for (int k1 = 0; k1 < list.size(); ++k1) {
						this.sendTileEntity((TileEntity) list.get(k1));
					}
				} else {
					this.sendAll(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.dirtyBlocks, this.dirtyCount, worldserver));

					for (i = 0; i < this.dirtyCount; ++i) {
						// CraftBukkit start - Fixes TileEntity updates
						// occurring upon a multi-block change; dirtyCount ->
						// dirtyBlocks[i]
						j = this.chunkX * 16 + (this.dirtyBlocks[i] >> 12 & 15);
						k = this.dirtyBlocks[i] & 255;
						l = this.chunkZ * 16 + (this.dirtyBlocks[i] >> 8 & 15);
						// CraftBukkit end

						if (Block.isTileEntity[worldserver.getTypeId(j, k, l)]) {
							// System.out.println("Sending!"); // CraftBukkit
							this.sendTileEntity(worldserver.getTileEntity(j, k, l));
						}
					}
				}
			}

			this.dirtyCount = 0;
		}
	}

	private void sendTileEntity(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.f();

			if (packet != null) {
				this.sendAll(packet);
			}
		}
	}
}
