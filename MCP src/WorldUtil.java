package net.minecraft.src;
//BukkitContrib

public abstract class WorldUtil {
	public static void obtainEntitySkin(World world, Entity entity) {
		world.obtainEntitySkin(entity);
	}
	
	public static void releaseEntitySkin(World world, Entity entity) {
		world.releaseEntitySkin(entity);
	}
}