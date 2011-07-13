package net.minecraft.src;

import com.pclewis.mcpatcher.mod.TileSize;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TextureFX;

public class TextureLavaFX extends TextureFX {
//BukkitContrib HD Start
	protected float[] field_1147_g = new float[TileSize.int_numPixels];
	protected float[] field_1146_h = new float[TileSize.int_numPixels];
	protected float[] field_1145_i = new float[TileSize.int_numPixels];
	protected float[] field_1144_j = new float[TileSize.int_numPixels];
//BukkitContrib HD End

	public TextureLavaFX() {
		super(Block.lavaMoving.blockIndexInTexture);
	}

	public void onTick() {
		int var2;
		float var3;
		int var5;
		int var6;
		int var7;
		int var8;
		int var9;
//BukkitContrib HD Start
		for(int var1 = 0; var1 < TileSize.int_size; ++var1) {
			for(var2 = 0; var2 < TileSize.int_size; ++var2) {
//BukkitContrib HD End
				var3 = 0.0F;
				int var4 = (int)(MathHelper.sin((float)var2 * 3.1415927F * 2.0F / 16.0F) * 1.2F);
				var5 = (int)(MathHelper.sin((float)var1 * 3.1415927F * 2.0F / 16.0F) * 1.2F);

				for(var6 = var1 - 1; var6 <= var1 + 1; ++var6) {
					for(var7 = var2 - 1; var7 <= var2 + 1; ++var7) {
//BukkitContrib HD Start
						var8 = var6 + var4 & TileSize.int_sizeMinus1;
						var9 = var7 + var5 & TileSize.int_sizeMinus1;
						var3 += this.field_1147_g[var8 + var9 * TileSize.int_size];
//BukkitContrib HD End
					}
				}
//BukkitContrib HD Start
				this.field_1146_h[var1 + var2 * TileSize.int_size] = var3 / 10.0F + (this.field_1145_i[(var1 + 0 & TileSize.int_sizeMinus1) + (var2 + 0 & TileSize.int_sizeMinus1) * TileSize.int_size] + this.field_1145_i[(var1 + 1 & TileSize.int_sizeMinus1) + (var2 + 0 & TileSize.int_sizeMinus1) * TileSize.int_size] + this.field_1145_i[(var1 + 1 & TileSize.int_sizeMinus1) + (var2 + 1 & TileSize.int_sizeMinus1) * TileSize.int_size] + this.field_1145_i[(var1 + 0 & TileSize.int_sizeMinus1) + (var2 + 1 & TileSize.int_sizeMinus1) * TileSize.int_size]) / 4.0F * 0.8F;
				this.field_1145_i[var1 + var2 * TileSize.int_size] += this.field_1144_j[var1 + var2 * TileSize.int_size] * 0.01F;
				if(this.field_1145_i[var1 + var2 * TileSize.int_size] < 0.0F) {
					this.field_1145_i[var1 + var2 * TileSize.int_size] = 0.0F;
//BukkitContrib HD End
				}
//BukkitContrib HD Start
				this.field_1144_j[var1 + var2 * TileSize.int_size] -= 0.06F;
				if(Math.random() < 0.0050D) {
					this.field_1144_j[var1 + var2 * TileSize.int_size] = 1.5F;
//BukkitContrib HD End
				}
			}
		}

		float[] var11 = this.field_1146_h;
		this.field_1146_h = this.field_1147_g;
		this.field_1147_g = var11;
//BukkitContrib HD Start
		for(var2 = 0; var2 < TileSize.int_numPixels; ++var2) {
//BukkitContrib HD End
			var3 = this.field_1147_g[var2] * 2.0F;
			if(var3 > 1.0F) {
				var3 = 1.0F;
			}

			if(var3 < 0.0F) {
				var3 = 0.0F;
			}

			var5 = (int)(var3 * 100.0F + 155.0F);
			var6 = (int)(var3 * var3 * 255.0F);
			var7 = (int)(var3 * var3 * var3 * var3 * 128.0F);
			if(this.anaglyphEnabled) {
				var8 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
				var9 = (var5 * 30 + var6 * 70) / 100;
				int var10 = (var5 * 30 + var7 * 70) / 100;
				var5 = var8;
				var6 = var9;
				var7 = var10;
			}

			this.imageData[var2 * 4 + 0] = (byte)var5;
			this.imageData[var2 * 4 + 1] = (byte)var6;
			this.imageData[var2 * 4 + 2] = (byte)var7;
			this.imageData[var2 * 4 + 3] = -1;
		}

	}
}
