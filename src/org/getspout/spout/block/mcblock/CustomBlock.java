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
package org.getspout.spout.block.mcblock;

import gnu.trove.map.hash.TIntIntHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockChest;
import net.minecraft.server.BlockContainer;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.BlockMinecartTrack;
import net.minecraft.server.BlockMushroom;
import net.minecraft.server.BlockStem;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CustomBlock extends Block implements CustomMCBlock{
	protected Block parent;
	
	protected CustomBlock(Block parent) {
		super(parent.id, parent.textureId, parent.material);
		this.parent = parent;
		updateField(parent, this, "strength");
		updateField(parent, this, "durability");
		updateField(parent, this, "bD");
		updateField(parent, this, "bE");
		this.minX = parent.minX;
		this.minY = parent.minY;
		this.minZ = parent.minZ;
		this.maxX = parent.maxX;
		this.maxY = parent.maxY;
		this.maxZ = parent.maxZ;
		this.stepSound = parent.stepSound;
		this.bM = parent.bM;
		this.frictionFactor = parent.frictionFactor;
		updateField(parent, this, "name");
	}
	
	private org.getspout.spoutapi.material.CustomBlock getCustomBlock(World world, int x, int y, int z) {
		if (this.id == MaterialData.stone.getRawId() || this.id == MaterialData.glass.getRawId()) {
			Object o = SpoutManager.getChunkDataManager().getBlockData(SimpleMaterialManager.blockIdString, world.getWorld(), x, y, z);
			if (o != null && o instanceof Integer) {
				return MaterialData.getCustomBlock(((Integer)o).intValue());
			}
		}
		return null;
	}
	
	public Block getParent() {
		return parent;
	}
	
	public void setHardness(float hardness) {
		this.strength = hardness;
		updateField(this, parent, "strength");
	}
			
	protected static int getIndex(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
	}
	
	public float getExplosionResistance() {
		return this.durability;
	}
	
	public void setExplosionResistance(float resistance) {
		this.durability = resistance;
	}
	
	@Override
	protected void h() {
		try{
			Method h = Block.class.getDeclaredMethod("h", (Class[])null);
			h.setAccessible(true);
			h.invoke(parent, (Object[]) null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean b() {
		return parent.b();
	}
	
	@Override
	public float j() {
		return parent.j();
	}
	
	@Override
	public Block c(float f) {
		return super.c(f); 
	}
	
	@Override
	public void a(float f, float f1, float f2, float f3, float f4, float f5) {
		if (parent != null) {
			parent.a(f, f1, f2, f3, f4, f5);
		}
		else {
			super.a(f, f1, f2, f3, f4, f5);
		}
	}
	
	@Override
	public boolean b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return parent.b(iblockaccess, i, j, k, l);
	}
	
	@Override
	public int a(int i, int j) {
		return parent.a(i, j);
	}
	
	@Override
	public int a(int i) {
		return parent.a(i);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
		parent.a(world, i, j, k, axisalignedbb, arraylist);
	}
	
	@Override
	public AxisAlignedBB e(World world, int i, int j, int k) {
		return parent.e(world, i, j, k);
	}
	
	@Override
	public boolean a() {
		//Spout
		if (this.id == Block.GLASS.id){
			return true;
		}
		//Spout end
		if (parent != null) {
			return parent.a();
		}
		return super.a();
	}
	
	@Override
	 public boolean a(int i, boolean flag) {
		return parent.a(i, flag);
	}
	
	@Override
	public boolean q_() {
		return parent.q_();
	}
	
	@Override
	public void a(World world, int i, int j, int k, Random random) {
		parent.a(world, i, j, k, random);
	}
	
	@Override
	public void postBreak(World world, int i, int j, int k, int l) {
		parent.postBreak(world, i, j, k, l);
	}
	
	@Override
	public void doPhysics(World world, int i, int j, int k, int l) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onNeighborBlockChange(world.getWorld(), i, j, k, l);
			handled = true;
		}
		if (!handled) {
			parent.doPhysics(world, i, j, k, l);
		}
	}
	
	@Override
	public int c() {
		return parent.c();
	}
	
	@Override
	public void a(World world, int i, int j, int k) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onBlockPlace(world.getWorld(), i, j, k);
			handled = true;
		}
		if (!handled) {
			parent.a(world, i, j, k);
		}
	}
	
	@Override
	public void remove(World world, int i, int j, int k) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onBlockDestroyed(world.getWorld(), i, j, k);
			handled = true;
		}
		if (!handled) {
			parent.remove(world, i, j, k);
		}
	}
	
	@Override
	public int a(Random random) {
		return parent.a(random);
	}
	
	@Override
	public int a(int i, Random random) {
		return parent.a(i, random);
	}
	
	@Override
	public float getDamage(EntityHuman entityhuman) {
		return parent.getDamage(entityhuman); //could have modified hardness, return super
	}
	
	@Override
	public void dropNaturally(World world, int i, int j, int k, int l, float f) {
		parent.dropNaturally(world, i, j, k, l, f);
	}
	
	@Override
	public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
		return parent.a(world, i, j, k, vec3d, vec3d1);
	}
	
	@Override
	public void a_(World world, int i, int j, int k) {
		parent.a_(world, i, j, k);
	}
	
	@Override
	public boolean canPlace(World world, int i, int j, int k, int l) {
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			return block.canPlaceBlockAt(world.getWorld(), i, j, k, CraftBlock.notchToBlockFace(l));
		}
		return parent.canPlace(world, i, j, k, l);
	}
	
	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			return block.canPlaceBlockAt(world.getWorld(), i, j, k);
		}
		return parent.canPlace(world, i, j, k);
	}
	
	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null && entityhuman instanceof EntityPlayer) {
			return block.onBlockInteract(world.getWorld(), i, j, k, ((SpoutPlayer)entityhuman.getBukkitEntity()));
		}
		return parent.interact(world, i, j, k, entityhuman);
	}
	
	@Override
	public void b(World world, int i, int j, int k, Entity entity) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onEntityMoveAt(world.getWorld(), i, j, k, entity.getBukkitEntity());
			handled = true;
		}
		if (!handled) {
			parent.b(world, i, j, k, entity);
		}
	}
	
	@Override
	public void postPlace(World world, int i, int j, int k, int l) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onBlockPlace(world.getWorld(), i, j, k);
			handled = true;
		}
		if (!handled) {
			parent.postPlace(world, i, j, k, l);
		}
	}
	
	@Override
	public void b(World world, int i, int j, int k, EntityHuman entityhuman) {
		boolean handled = false;
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			block.onBlockClicked(world.getWorld(), i, j, k, (SpoutPlayer)entityhuman.getBukkitEntity());
			handled = true;
		}
		
		if (entityhuman instanceof EntityPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutManager.getPlayer((Player)((EntityPlayer)entityhuman).getBukkitEntity());
			player.setLastClickedLocation(new Location(player.getWorld(), i, j, k));
		}
		
		if (!handled) {
			parent.b(world, i, j, k, entityhuman);
		}	
	}
	
	@Override
	public void a(World world, int i, int j, int k, Entity entity, Vec3D vec3d) {
		parent.a(world, i, j, k, entity, vec3d);
	}
	
	@Override
	public void a(IBlockAccess iblockaccess, int i, int j, int k) {
		parent.a(iblockaccess, i, j, k);
	}
	
	@Override
	public boolean a(IBlockAccess iblockaccess, int x, int y, int z, int face) {
		int index = CustomBlock.getIndex(x, y, z);
		Chunk chunk = ((World)iblockaccess).getChunkAt(x >> 4, z >> 4).bukkitChunk;
		if (chunk.getClass().equals(SpoutCraftChunk.class)) { 
			TIntIntHashMap powerOverrides = ((SpoutCraftChunk)chunk).powerOverrides;
			if (powerOverrides.containsKey(index)) {
				int powerbits = powerOverrides.get(index);
				switch (face) {
					case 0:
						return (powerbits & (1 << 0)) != 0;
					case 1:
						return (powerbits & (1 << 1)) != 0;
					case 2:
						return (powerbits & (1 << 2)) != 0;
					case 3:
						return (powerbits & (1 << 3)) != 0;
					case 4:
						return (powerbits & (1 << 4)) != 0;
					case 5:
						return (powerbits & (1 << 5)) != 0;
					default:
						return parent.a(iblockaccess, x, y, z, face);
				}
			}
		}
		if (iblockaccess instanceof World) {
			org.getspout.spoutapi.material.CustomBlock block = getCustomBlock((World)iblockaccess, x, y, z);
			if (block != null) {
				return block.isProvidingPowerTo(((World)iblockaccess).getWorld(), x, y, z, CraftBlock.notchToBlockFace(face));
			}
		}
		return parent.a(iblockaccess, x, y, z, face);
	}
	
	@Override
	public boolean isPowerSource() {
		return parent.isPowerSource();
	}
	
	@Override
	public void a(World world, int i, int j, int k, Entity entity) {
		parent.a(world, i, j, k, entity);
	}
	
	@Override
	public boolean d(World world, int i, int j, int k, int l) {
		org.getspout.spoutapi.material.CustomBlock block = getCustomBlock(world, i, j, k);
		if (block != null) {
			return block.isProvidingPowerTo(world.getWorld(), i, j, k, CraftBlock.notchToBlockFace(l));
		}
		return parent.d(world, i, j, k, l);
	}
	
	@Override
	public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
		parent.a(world, entityhuman, i, j, k, l);
	}
	
	@Override
	public boolean f(World world, int i, int j, int k) {
		return parent.f(world, i, j, k);
	}
	
	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving) {
		parent.postPlace(world, i, j, k, entityliving);
	}
	
	@Override
	public void a(World world, int i, int j, int k, int l, int i1) {
		parent.a(world, i, j, k, l, i1);
	}
	
	public static void replaceBlocks() {
		for (int i = 0; i < Block.byId.length; i++) {
			if (Block.byId[i] != null) {
				Block parent = Block.byId[i];
				Block.byId[i] = null;
				
				boolean oldn = n[i];
				boolean oldo = o[i];
				boolean oldTileEntity = isTileEntity[i];
				int oldq = q[i];
				boolean oldr = r[i];
				int olds = s[i];
				boolean oldt = t[i];
				
				/* Order matters, BlockChest extends BlockContainer*/
				if (parent instanceof BlockChest) {
					Block.byId[i] = new CustomChest((BlockChest)parent);
				}
				else if (parent instanceof BlockContainer) {
					Block.byId[i] = new CustomContainer((BlockContainer)parent);
				}
				else if (parent instanceof BlockMinecartTrack) {
					Block.byId[i] = new CustomMinecartTrack((BlockMinecartTrack)parent);
				}
				/* Order matters, BlockStem extends BlockFlower*/
				else if (parent instanceof BlockStem) {
					Block.byId[i] = new CustomStem((BlockStem)parent);
				}
				else if (parent instanceof BlockMushroom) {
					Block.byId[i] = new CustomMushroom((BlockMushroom)parent);
				}
				else if (parent instanceof BlockFlower) {
					Block.byId[i] = new CustomFlower((BlockFlower)parent);
				}
				else {
					Block.byId[i] = new CustomBlock(parent);
				}
				n[i] = oldn;
				o[i] = oldo;
				isTileEntity[i] = oldTileEntity;
				q[i] = oldq;
				r[i] = oldr;
				s[i] = olds;
				t[i] = oldt;

			}
		}
		
		//Allow placement of blocks on glass
		try {
			Field field = Material.SHATTERABLE.getClass().getDeclaredField("G");
			field.setAccessible(true);
			field.setBoolean(Material.SHATTERABLE, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Block.q[Block.GLASS.id] = 0;
	}
	
	public static void resetBlocks() {
		for (int i = 0; i < Block.byId.length; i++) {
			if (Block.byId[i] != null) {
				Block parent = Block.byId[i];
				Block.byId[i] = null;
				
				if (parent instanceof CustomMCBlock) {
					Block.byId[i] = ((CustomMCBlock)parent).getParent();
				}
				else {
					Block.byId[i] = parent;
				}
			}
		}
	}
	
	private static void updateField(Block parent, Block child, String fieldName) {
		try {
			Field field = Block.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(child, field.get(parent));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
