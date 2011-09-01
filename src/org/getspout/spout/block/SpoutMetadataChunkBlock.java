package org.getspout.spout.block;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;
import net.minecraft.server.EnumSkyBlock;
import net.minecraft.server.MetadataChunkBlock;
import net.minecraft.server.World;

public class SpoutMetadataChunkBlock extends MetadataChunkBlock{
	
	public SpoutMetadataChunkBlock(MetadataChunkBlock parent) {
		super(parent.a, parent.b, parent.c, parent.d, parent.e, parent.f, parent.g);
	}
	
	@Override
	public void a(World world) {
		int i = this.e - this.b + 1;
		int j = this.f - this.c + 1;
		int k = this.g - this.d + 1;
		int l = i * j * k;
		if (l > 32768) {
			System.out.println("Light too large, skipping!");
		} else {
			for (int dx = this.b; dx <= this.e; ++dx) {
				for (int dz = this.d; dz <= this.g; ++dz) {
					Chunk chunk = null;
					if (world.areChunksLoaded(dx, 0, dz, 1)) {
						chunk = world.getChunkAt(dx >> 4, dz >> 4);

						if (chunk.isEmpty()) {
							chunk = null;
						}
					}
					if (chunk != null) {
						if (this.c < 0) {
							this.c = 0;
						}

						if (this.f >= 128) {
							this.f = 127;
						}

						for (int dy = this.c; dy <= this.f; ++dy) {
							
							int neighborLightLevel;
							int lightLevel = 0;
							
							int oldLightLevel = world.a(this.a, dx, dy, dz);
							
							int index = (dx & 0xF) << 11 | (dz & 0xF) << 7 | (dy & 0x7F);
							SpoutCraftChunk spoutChunk = (SpoutCraftChunk)chunk.bukkitChunk;
							if (spoutChunk.lightOverrides.containsKey(index)) {
								lightLevel = spoutChunk.lightOverrides.get(index);
								if (lightLevel != oldLightLevel) {
									world.b(this.a, dx, dy, dz, lightLevel);
								}
								continue;
							}
							
							int typeId = world.getTypeId(dx, dy, dz);
							int j3 = Block.q[typeId]; //opacity of block

							if (j3 == 0) {
								j3 = 1;
							}

							int k3 = 0;

							if (this.a == EnumSkyBlock.SKY) {
								if (world.m(dx, dy, dz)) {
									k3 = 15;
								}
							} else if (this.a == EnumSkyBlock.BLOCK) {
								k3 = Block.s[typeId]; //light level of block
							}
							
							if (j3 >= 15 && k3 == 0) {
								lightLevel = 0;
							} else {
								neighborLightLevel = world.a(this.a, dx - 1, dy, dz);
								int j4 = world.a(this.a, dx + 1, dy, dz);
								int k4 = world.a(this.a, dx, dy - 1, dz);
								int l4 = world.a(this.a, dx, dy + 1, dz);
								int i5 = world.a(this.a, dx, dy, dz - 1);
								int j5 = world.a(this.a, dx, dy, dz + 1);

								lightLevel = neighborLightLevel;
								if (j4 > neighborLightLevel) {
									lightLevel = j4;
								}

								if (k4 > lightLevel) {
									lightLevel = k4;
								}

								if (l4 > lightLevel) {
									lightLevel = l4;
								}

								if (i5 > lightLevel) {
									lightLevel = i5;
								}

								if (j5 > lightLevel) {
									lightLevel = j5;
								}

								lightLevel -= j3;
								if (lightLevel < 0) {
									lightLevel = 0;
								}

								if (k3 > lightLevel) {
									lightLevel = k3;
								}
							}
							if (oldLightLevel != lightLevel) {
								world.b(this.a, dx, dy, dz, lightLevel);
								neighborLightLevel = lightLevel - 1;
								if (neighborLightLevel < 0) {
									neighborLightLevel = 0;
								}
								int origneighborLightLevel = neighborLightLevel;
								
								//Update Neighbor (North)
								index = getIndex(dx - 1, dy, dz);
								if (spoutChunk.lightOverrides.containsKey(index)) {
									neighborLightLevel = spoutChunk.lightOverrides.get(index);
									oldLightLevel = world.a(this.a, dx - 1, dy, dz);
									if (neighborLightLevel != oldLightLevel) {
										world.b(this.a, dx - 1, dy, dz, lightLevel);
									}
								}
								else {
									neighborLightLevel = origneighborLightLevel;
									world.a(this.a, dx - 1, dy, dz, neighborLightLevel);
								}
								
								//Update Neighbor (Below)
								index = getIndex(dx, dy - 1, dz);
								if (spoutChunk.lightOverrides.containsKey(index)) {
									neighborLightLevel = spoutChunk.lightOverrides.get(index);
									oldLightLevel = world.a(this.a, dx, dy - 1, dz);
									if (neighborLightLevel != oldLightLevel) {
										world.b(this.a, dx, dy - 1, dz, lightLevel);
									}
								}
								else {
									neighborLightLevel = origneighborLightLevel;
									world.a(this.a, dx, dy - 1, dz, neighborLightLevel);
								}
								
								//Update Neighbor (East)
								index = getIndex(dx, dy, dz - 1);
								if (spoutChunk.lightOverrides.containsKey(index)) {
									neighborLightLevel = spoutChunk.lightOverrides.get(index);
									oldLightLevel = world.a(this.a, dx, dy, dz - 1);
									if (neighborLightLevel != oldLightLevel) {
										world.b(this.a, dx, dy, dz - 1, lightLevel);
									}
								}
								else {
									neighborLightLevel = origneighborLightLevel;
									world.a(this.a, dx, dy, dz - 1, neighborLightLevel);
								}
								
								if (dx + 1 >= this.e) {
									//Update Neighbor (South)
									index = getIndex(dx + 1, dy, dz);
									if (spoutChunk.lightOverrides.containsKey(index)) {
										neighborLightLevel = spoutChunk.lightOverrides.get(index);
										oldLightLevel = world.a(this.a, dx + 1, dy, dz);
										if (neighborLightLevel != oldLightLevel) {
											world.b(this.a, dx + 1, dy, dz, lightLevel);
										}
									}
									else {
										neighborLightLevel = origneighborLightLevel;
										world.a(this.a, dx + 1, dy, dz, neighborLightLevel);
									}
								}
	
								if (dy + 1 >= this.f) {
									//Update Neighbor (Up)
									index = getIndex(dx, dy + 1, dz);
									if (spoutChunk.lightOverrides.containsKey(index)) {
										neighborLightLevel = spoutChunk.lightOverrides.get(index);
										oldLightLevel = world.a(this.a, dx, dy + 1, dz);
										if (neighborLightLevel != oldLightLevel) {
											world.b(this.a, dx, dy + 1, dz, lightLevel);
										}
									}
									else {
										neighborLightLevel = origneighborLightLevel;
										world.a(this.a, dx, dy + 1, dz, neighborLightLevel);
									}
								}
	
								if (dz + 1 >= this.g) {
									//Update Neighbor (West)
									index = getIndex(dx, dy, dz + 1);
									if (spoutChunk.lightOverrides.containsKey(index)) {
										neighborLightLevel = spoutChunk.lightOverrides.get(index);
										oldLightLevel = world.a(this.a, dx - 1, dy, dz);
										if (neighborLightLevel != oldLightLevel) {
											world.b(this.a, dx - 1, dy, dz, lightLevel);
										}
									}
									else {
										neighborLightLevel = origneighborLightLevel;
										world.a(this.a, dx - 1, dy, dz, neighborLightLevel);
									}
								}
							}
						}
					}
				}
			}
		}
		updateLighting();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateLighting() {
		for (org.bukkit.World world : Bukkit.getServer().getWorlds()) {
			CraftWorld cw = (CraftWorld)world;
			World handle = cw.getHandle();
			try {
				Field metablocks = World.class.getDeclaredField("C");
				metablocks.setAccessible(true);
				List metablockList = (List) metablocks.get(handle);
				for (int i = 0; i < metablockList.size(); i++) {
					MetadataChunkBlock metablock = (MetadataChunkBlock) metablockList.get(i);
					if (!(metablock instanceof SpoutMetadataChunkBlock)) {
						metablockList.set(i, new SpoutMetadataChunkBlock(metablock));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static int getIndex(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
	}

}
