package net.minecraft.src;
//BukkitContrib HD Start
import com.pclewis.mcpatcher.mod.TileSize;
//BukkitContrib HD End
import net.minecraft.src.Block;
import net.minecraft.src.TextureFX;

public class TextureFlamesFX extends TextureFX {
//BukkitContrib HD Start
	protected float[] field_1133_g = new float[TileSize.int_flameArraySize];
	protected float[] field_1132_h = new float[TileSize.int_flameArraySize];
//BukkitContrib HD End

	public TextureFlamesFX(int var1) {
		super(Block.fire.blockIndexInTexture + var1 * 16);
	}

	public void onTick() {
		int var2;
		float var4;
		int var5;
		int var6;
//BukkitContrib HD Start
		for(int var1 = 0; var1 < TileSize.int_size; ++var1) {
			for(var2 = 0; var2 < TileSize.int_flameHeight; ++var2) {
//BukkitContrib HD End
				int var3 = 18;
//BukkitContrib HD Start
				var4 = this.field_1133_g[var1 + (var2 + 1) % TileSize.int_flameHeight * TileSize.int_size] * (float)var3;
//BukkitContrib HD End
				for(var5 = var1 - 1; var5 <= var1 + 1; ++var5) {
					for(var6 = var2; var6 <= var2 + 1; ++var6) {
//BukkitContrib HD Start
						if(var5 >= 0 && var6 >= 0 && var5 < TileSize.int_size && var6 < TileSize.int_flameHeight) {
							var4 += this.field_1133_g[var5 + var6 * TileSize.int_size];
						}
//BukkitContrib HD End
						++var3;
					}
				}
//BukkitContrib HD Start
				this.field_1132_h[var1 + var2 * TileSize.int_size] = var4 / ((float)var3 * TileSize.float_flameNudge);
				if(var2 >= TileSize.int_flameHeightMinus1) {
					this.field_1132_h[var1 + var2 * TileSize.int_size] = (float)(Math.random() * Math.random() * Math.random() * 4.0D + Math.random() * 0.10000000149011612D + 0.20000000298023224D);
//BukkitContrib HD End
				}
			}
		}

		float[] var12 = this.field_1132_h;
		this.field_1132_h = this.field_1133_g;
		this.field_1133_g = var12;
//BukkitContrib HD Start
		for(var2 = 0; var2 < TileSize.int_numPixels; ++var2) {
//BukkitContrib HD End
			float var13 = this.field_1133_g[var2] * 1.8F;
			if(var13 > 1.0F) {
				var13 = 1.0F;
			}

			if(var13 < 0.0F) {
				var13 = 0.0F;
			}

			var5 = (int)(var13 * 155.0F + 100.0F);
			var6 = (int)(var13 * var13 * 255.0F);
			int var7 = (int)(var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * 255.0F);
			short var8 = 255;
			if(var13 < 0.5F) {
				var8 = 0;
			}

			var4 = (var13 - 0.5F) * 2.0F;
			if(this.anaglyphEnabled) {
				int var9 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
				int var10 = (var5 * 30 + var6 * 70) / 100;
				int var11 = (var5 * 30 + var7 * 70) / 100;
				var5 = var9;
				var6 = var10;
				var7 = var11;
			}

			this.imageData[var2 * 4 + 0] = (byte)var5;
			this.imageData[var2 * 4 + 1] = (byte)var6;
			this.imageData[var2 * 4 + 2] = (byte)var7;
			this.imageData[var2 * 4 + 3] = (byte)var8;
		}

	}
}
