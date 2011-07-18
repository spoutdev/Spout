package net.minecraft.src;


public class ColorizerGrass {

	public static int[] grassBuffer = new int[65536]; //BukkitContrib HD private -> public


	public static void func_28181_a(int[] var0) {
		grassBuffer = var0;
	}

	public static int getGrassColor(double var0, double var2) {
		var2 *= var0;
		int var4 = (int)((1.0D - var0) * 255.0D);
		int var5 = (int)((1.0D - var2) * 255.0D);
		return grassBuffer[var5 << 8 | var4];
	}

}
