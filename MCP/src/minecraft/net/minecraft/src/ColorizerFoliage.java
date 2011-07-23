package net.minecraft.src;


public class ColorizerFoliage {

	public static int[] foliageBuffer = new int[65536]; //BukkitContrib HD private->public


	public static void func_28152_a(int[] var0) {
		foliageBuffer = var0;
	}

	public static int getFoliageColor(double var0, double var2) {
		var2 *= var0;
		int var4 = (int)((1.0D - var0) * 255.0D);
		int var5 = (int)((1.0D - var2) * 255.0D);
		return foliageBuffer[var5 << 8 | var4];
	}

	public static int getFoliageColorPine() {
		return 6396257;
	}

	public static int getFoliageColorBirch() {
		return 8431445;
	}

	public static int func_31073_c() {
		return 4764952;
	}

}
