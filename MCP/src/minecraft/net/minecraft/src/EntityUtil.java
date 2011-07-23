package net.minecraft.src;
//BukkitContrib

public abstract class EntityUtil {
	public static void setMaximumAir(Entity entity, int time) {
		entity.maxAir = time;
	}
}