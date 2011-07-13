package com.pclewis.mcpatcher.mod;

import com.pclewis.mcpatcher.MCPatcherUtils;
import java.lang.reflect.Field;

public final class TileSize {

	public static int int_size;
	public static int int_sizeMinus1;
	public static int int_sizeHalf;
	public static int int_glBufferSize = 65536;
	public static int int_numPixels;
	public static int int_numBytes;
	public static int int_numPixelsMinus1;
	public static int int_compassNeedleMin;
	public static int int_compassNeedleMax;
	public static int int_compassCrossMin;
	public static int int_compassCrossMax;
	public static int int_flameHeight;
	public static int int_flameHeightMinus1;
	public static int int_flameArraySize;
	public static float float_size;
	public static float float_sizeMinus1;
	public static float float_sizeMinus0_01;
	public static float float_sizeHalf;
	public static float float_size16;
	public static float float_reciprocal;
	public static float float_texNudge;
	public static float float_flameNudge;
	public static double double_size;
	public static double double_sizeMinus1;
	public static double double_compassCenterMin;
	public static double double_compassCenterMax;


	public static void setTileSize(int var0) {
		int_size = var0;
		int_sizeMinus1 = var0 - 1;
		int_sizeHalf = var0 / 2;
		int_glBufferSize = Math.max(int_glBufferSize, 1024 * var0 * var0);
		int_numPixels = var0 * var0;
		int_numBytes = 4 * int_numPixels;
		int_numPixelsMinus1 = int_numPixels - 1;
		int_compassNeedleMin = var0 / -2;
		int_compassNeedleMax = var0;
		int_compassCrossMin = var0 / -4;
		int_compassCrossMax = var0 / 4;
		int_flameHeight = var0 + 4;
		int_flameHeightMinus1 = int_flameHeight - 1;
		int_flameArraySize = var0 * int_flameHeight;
		float_size = (float)int_size;
		float_sizeMinus1 = float_size - 1.0F;
		float_sizeMinus0_01 = float_size - 0.01F;
		float_sizeHalf = float_size / 2.0F;
		float_size16 = float_size * 16.0F;
		float_reciprocal = 1.0F / float_size;
		float_texNudge = 1.0F / (float_size * float_size * 2.0F);
		if(var0 < 64) {
			float_flameNudge = 1.0F + 0.96F / float_size;
		} else {
			float_flameNudge = 1.0F + 1.28F / float_size;
		}

		double_size = (double)int_size;
		double_sizeMinus1 = double_size - 1.0D;
		double_compassCenterMin = double_size / 2.0D - 0.5D;
		double_compassCenterMax = double_size / 2.0D + 0.5D;
	}

	private static void dump() {
		Field[] var0 = TileSize.class.getDeclaredFields();
		int var1 = var0.length;

		for(int var2 = 0; var2 < var1; ++var2) {
			Field var3 = var0[var2];
			if(var3.getName().contains("_")) {
				try {
					//MCPatcherUtils.log("%s = %s", new Object[]{var3.getName(), var3.get((Object)null)});
				} catch (Exception var5) {
					//MCPatcherUtils.log("%s: %s", new Object[]{var3.getName(), var5.toString()});
				}
			}
		}

	}

	static {
		setTileSize(16);
	}
}
