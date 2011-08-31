package org.getspout.spout.block.mcblock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockContainer;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.BlockMinecartTrack;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class CustomBlock extends Block{
	protected Block parent;
	
	protected CustomBlock(Block parent) {
		super(parent.id, parent.textureId, parent.material);
		this.parent = parent;
		updateField(parent, this, "strength");
		updateField(parent, this, "durability");
		updateField(parent, this, "bq");
		updateField(parent, this, "br");
		this.minX = parent.minX;
		this.minY = parent.minY;
		this.minZ = parent.minZ;
		this.maxX = parent.maxX;
		this.maxY = parent.maxY;
		this.maxZ = parent.maxZ;
		this.stepSound = parent.stepSound;
		this.bz = parent.bz;
		this.frictionFactor = parent.frictionFactor;
		updateField(parent, this, "name");
	}
	
	protected static int getIndex(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
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
	public boolean k_() {
		return parent.k_();
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
		parent.doPhysics(world, i, j, k, l);
	}
	
	@Override
	public int c() {
		return parent.c();
	}
	
	@Override
	public void c(World world, int i, int j, int k) {
		parent.c(world, i, j, k);
	}
	
	@Override
	public void remove(World world, int i, int j, int k) {
		parent.remove(world, i, j, k);
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
		if (entityhuman instanceof EntityPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutManager.getPlayer((Player)((EntityPlayer)entityhuman).getBukkitEntity());
			Location target = player.getRawLastClickedLocation();
			if (target != null) {
				int index = CustomBlock.getIndex((int)target.getX(), (int)target.getY(), (int)target.getZ());
				Map<Integer, Float> hardnessOverrides = ((SpoutCraftChunk)target.getWorld().getChunkAt(target)).hardnessOverrides;
				if (hardnessOverrides.containsKey(index)) {
					return hardnessOverrides.get(index);
				}
			}
		}
		return parent.getDamage(entityhuman);
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
	public void d(World world, int i, int j, int k) {
		parent.d(world, i, j, k);
	}
	
	@Override
	public boolean canPlace(World world, int i, int j, int k, int l) {
		return parent.canPlace(world, i, j, k, l);
	}
	
	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		return parent.canPlace(world, i, j, k);
	}
	
	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
		return parent.interact(world, i, j, k, entityhuman);
	}
	
	@Override
	public void b(World world, int i, int j, int k, Entity entity) {
		parent.b(world, i, j, k, entity);
	}
	
	@Override
	public void postPlace(World world, int i, int j, int k, int l) {
		parent.postPlace(world, i, j, k, l);
	}
	
	@Override
	public void b(World world, int i, int j, int k, EntityHuman entityhuman) {
		if (entityhuman instanceof EntityPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutManager.getPlayer((Player)((EntityPlayer)entityhuman).getBukkitEntity());
			player.setLastClickedLocation(new Location(player.getWorld(), i, j, k));
		}
		parent.b(world, i, j, k, entityhuman);
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
		Map<Integer, Integer> powerOverrides = ((SpoutCraftChunk)((World)iblockaccess).getChunkAt(x >> 4, z >> 4).bukkitChunk).powerOverrides;
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
				
				if (parent instanceof BlockContainer) {
					Block.byId[i] = new CustomContainer((BlockContainer)parent);
				}
				else if (parent instanceof BlockMinecartTrack) {
					Block.byId[i] = new CustomMinecartTrack((BlockMinecartTrack)parent);
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
		
		
		/*int range = 55;
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					((SpoutBlock)Bukkit.getServer().getWorlds().get(0).getBlockAt(dx, dy, dz)).setBlockPowered(true, BlockFace.EAST);
					((SpoutBlock)Bukkit.getServer().getWorlds().get(0).getBlockAt(dx, dy, dz)).setHardness(2f);
				}
			}
		}*/
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
