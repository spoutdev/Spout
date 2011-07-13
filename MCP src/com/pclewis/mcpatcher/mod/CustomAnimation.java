package com.pclewis.mcpatcher.mod;

import com.pclewis.mcpatcher.mod.TextureUtils;
import com.pclewis.mcpatcher.mod.TileSize;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import net.minecraft.src.TextureFX;

public class CustomAnimation extends TextureFX {

	private int frame;
	private int numFrames;
	private byte[] src;
	private byte[] temp;
	private int minScrollDelay = -1;
	private int maxScrollDelay = -1;
	private int timer = -1;
	private boolean isScrolling;
	private static Random rand = new Random();


	public CustomAnimation(int var1, int var2, int var3, String var4, int var5, int var6) {
		super(var1);
		this.iconIndex = var1;
		this.tileImage = var2;
		this.tileSize = var3;
		this.minScrollDelay = var5;
		this.maxScrollDelay = var6;
		this.isScrolling = var5 >= 0;
		BufferedImage var7 = null;
		String var8 = "custom_" + var4 + ".png";

		try {
			var7 = TextureUtils.getResourceAsBufferedImage("/" + var8);
		} catch (IOException var14) {
			;
		}

		//MCPatcherUtils.log("new CustomAnimation %s, src=%s, buffer size=0x%x, tile=%d", new Object[]{var4, var7 == null?"terrain.png":var8, Integer.valueOf(this.imageData.length), Integer.valueOf(this.iconIndex)});
		if(var7 == null) {
			BufferedImage var9;
			try {
				var9 = TextureUtils.getResourceAsBufferedImage("/terrain.png");
			} catch (IOException var13) {
				var13.printStackTrace();
				return;
			}

			int var10 = var1 % 16 * TileSize.int_size;
			int var11 = var1 / 16 * TileSize.int_size;
			int[] var12 = new int[TileSize.int_numPixels];
			var9.getRGB(var10, var11, TileSize.int_size, TileSize.int_size, var12, 0, TileSize.int_size);
			ARGBtoRGBA(var12, this.imageData);
			if(this.isScrolling) {
				this.temp = new byte[TileSize.int_size * 4];
			}
		} else {
			this.numFrames = var7.getHeight() / var7.getWidth();
			int[] var15 = new int[var7.getWidth() * var7.getHeight()];
			var7.getRGB(0, 0, var7.getWidth(), var7.getHeight(), var15, 0, TileSize.int_size);
			this.src = new byte[var15.length * 4];
			ARGBtoRGBA(var15, this.src);
		}

	}

	private static void ARGBtoRGBA(int[] var0, byte[] var1) {
		for(int var2 = 0; var2 < var0.length; ++var2) {
			int var3 = var0[var2];
			var1[var2 * 4 + 3] = (byte)(var3 >> 24 & 255);
			var1[var2 * 4 + 0] = (byte)(var3 >> 16 & 255);
			var1[var2 * 4 + 1] = (byte)(var3 >> 8 & 255);
			var1[var2 * 4 + 2] = (byte)(var3 >> 0 & 255);
		}

	}

	public void onTick() {
		if(this.src != null) {
			if(++this.frame >= this.numFrames) {
				this.frame = 0;
			}

			System.arraycopy(this.src, this.frame * TileSize.int_size * TileSize.int_size * 4, this.imageData, 0, TileSize.int_size * TileSize.int_size * 4);
		} else if(this.isScrolling && (this.maxScrollDelay <= 0 || --this.timer <= 0)) {
			if(this.maxScrollDelay > 0) {
				this.timer = rand.nextInt(this.maxScrollDelay - this.minScrollDelay + 1) + this.minScrollDelay;
			}

			System.arraycopy(this.imageData, (TileSize.int_size - 1) * TileSize.int_size * 4, this.temp, 0, TileSize.int_size * 4);
			System.arraycopy(this.imageData, 0, this.imageData, TileSize.int_size * 4, TileSize.int_size * (TileSize.int_size - 1) * 4);
			System.arraycopy(this.temp, 0, this.imageData, 0, TileSize.int_size * 4);
		}

	}

}
