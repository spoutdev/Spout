package net.minecraft.src;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;
//BukkitContrib HD Start
import com.pclewis.mcpatcher.mod.TextureUtils; 
//BukkitContrib HD End

public class FontRenderer {

	private int[] charWidth = new int[256];
	public int fontTextureName = 0;
	private int fontDisplayLists;
	private IntBuffer buffer = GLAllocation.createDirectIntBuffer(1024 /*GL_FRONT_LEFT*/);


	public FontRenderer(GameSettings var1, String var2, RenderEngine var3) {
		BufferedImage var4;
		try {
//BukkitContrib HD Start
			var4 = TextureUtils.getResourceAsBufferedImage(var2);
//BukkitContrib HD End
		} catch (IOException var18) {
			throw new RuntimeException(var18);
		}

		int var5 = var4.getWidth();
		int var6 = var4.getHeight();
		int[] var7 = new int[var5 * var6];
		var4.getRGB(0, 0, var5, var6, var7, 0, var5);
		int var8 = 0;

		int var9;
		int var10;
		int var11;
		int var12;
		int var15;
		int var16;
		while(var8 < 256) {
			var9 = var8 % 16;
			var10 = var8 / 16;
//BukkitContrib HD Start
			var11 = var5 / 16 - 1;
//BukkitContrib HD End
			while(true) {
				if(var11 >= 0) {
//BukkitContrib HD Start
					var12 = var9 * (var5 / 16) + var11;
//BukkitContrib HD End
					boolean var13 = true;
//BukkitContrib HD Start
					for(int var14 = 0; var14 < var5 / 16 && var13; ++var14) {
						var15 = (var10 * (var5 / 16) + var14) * var5;
//BukkitContrib HD End
						var16 = var7[var12 + var15] & 255;
						if(var16 > 0) {
							var13 = false;
						}
					}

					if(var13) {
						--var11;
						continue;
					}
				}

				if(var8 == 32) {
//BukkitContrib HD Start
					var11 = var5 / 64;
//BukkitContrib HD End
				}
//BukkitContrib HD Start
				this.charWidth[var8] = (128 * var11 + 256) / var5;
//BukkitContrib HD End
				++var8;
				break;
			}
		}

		this.fontTextureName = var3.allocateAndSetupTexture(var4);
		this.fontDisplayLists = GLAllocation.generateDisplayLists(288);
		Tessellator var19 = Tessellator.instance;

		for(var9 = 0; var9 < 256; ++var9) {
			GL11.glNewList(this.fontDisplayLists + var9, 4864 /*GL_COMPILE*/);
			var19.startDrawingQuads();
			var10 = var9 % 16 * 8;
			var11 = var9 / 16 * 8;
			float var20 = 7.99F;
			float var21 = 0.0F;
			float var24 = 0.0F;
			var19.addVertexWithUV(0.0D, (double)(0.0F + var20), 0.0D, (double)((float)var10 / 128.0F + var21), (double)(((float)var11 + var20) / 128.0F + var24));
			var19.addVertexWithUV((double)(0.0F + var20), (double)(0.0F + var20), 0.0D, (double)(((float)var10 + var20) / 128.0F + var21), (double)(((float)var11 + var20) / 128.0F + var24));
			var19.addVertexWithUV((double)(0.0F + var20), 0.0D, 0.0D, (double)(((float)var10 + var20) / 128.0F + var21), (double)((float)var11 / 128.0F + var24));
			var19.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)((float)var10 / 128.0F + var21), (double)((float)var11 / 128.0F + var24));
			var19.draw();
			GL11.glTranslatef((float)this.charWidth[var9], 0.0F, 0.0F);
			GL11.glEndList();
		}

		for(var9 = 0; var9 < 32; ++var9) {
			var10 = (var9 >> 3 & 1) * 85;
			var11 = (var9 >> 2 & 1) * 170 + var10;
			var12 = (var9 >> 1 & 1) * 170 + var10;
			int var22 = (var9 >> 0 & 1) * 170 + var10;
			if(var9 == 6) {
				var11 += 85;
			}

			boolean var23 = var9 >= 16;
			if(var1.anaglyph) {
				var15 = (var11 * 30 + var12 * 59 + var22 * 11) / 100;
				var16 = (var11 * 30 + var12 * 70) / 100;
				int var17 = (var11 * 30 + var22 * 70) / 100;
				var11 = var15;
				var12 = var16;
				var22 = var17;
			}

			if(var23) {
				var11 /= 4;
				var12 /= 4;
				var22 /= 4;
			}

			GL11.glNewList(this.fontDisplayLists + 256 + var9, 4864 /*GL_COMPILE*/);
			GL11.glColor3f((float)var11 / 255.0F, (float)var12 / 255.0F, (float)var22 / 255.0F);
			GL11.glEndList();
		}

	}

	public void drawStringWithShadow(String var1, int var2, int var3, int var4) {
		this.renderString(var1, var2 + 1, var3 + 1, var4, true);
		this.drawString(var1, var2, var3, var4);
	}

	public void drawString(String var1, int var2, int var3, int var4) {
		this.renderString(var1, var2, var3, var4, false);
	}

	public void renderString(String var1, int var2, int var3, int var4, boolean var5) {
		if(var1 != null) {
			int var6;
			if(var5) {
				var6 = var4 & -16777216;
				var4 = (var4 & 16579836) >> 2;
				var4 += var6;
			}

			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.fontTextureName);
			float var10 = (float)(var4 >> 16 & 255) / 255.0F;
			float var7 = (float)(var4 >> 8 & 255) / 255.0F;
			float var8 = (float)(var4 & 255) / 255.0F;
			float var9 = (float)(var4 >> 24 & 255) / 255.0F;
			if(var9 == 0.0F) {
				var9 = 1.0F;
			}

			GL11.glColor4f(var10, var7, var8, var9);
			this.buffer.clear();
			GL11.glPushMatrix();
			GL11.glTranslatef((float)var2, (float)var3, 0.0F);

			for(var6 = 0; var6 < var1.length(); ++var6) {
				int var11;
				for(; var1.length() > var6 + 1 && var1.charAt(var6) == 167; var6 += 2) {
					var11 = "0123456789abcdef".indexOf(var1.toLowerCase().charAt(var6 + 1));
					if(var11 < 0 || var11 > 15) {
						var11 = 15;
					}

					this.buffer.put(this.fontDisplayLists + 256 + var11 + (var5?16:0));
					if(this.buffer.remaining() == 0) {
						this.buffer.flip();
						GL11.glCallLists(this.buffer);
						this.buffer.clear();
					}
				}

				if(var6 < var1.length()) {
					var11 = ChatAllowedCharacters.allowedCharacters.indexOf(var1.charAt(var6));
					if(var11 >= 0) {
						this.buffer.put(this.fontDisplayLists + var11 + 32);
					}
				}

				if(this.buffer.remaining() == 0) {
					this.buffer.flip();
					GL11.glCallLists(this.buffer);
					this.buffer.clear();
				}
			}

			this.buffer.flip();
			GL11.glCallLists(this.buffer);
			GL11.glPopMatrix();
		}
	}

	public int getStringWidth(String var1) {
		if(var1 == null) {
			return 0;
		} else {
			int var2 = 0;

			for(int var3 = 0; var3 < var1.length(); ++var3) {
				if(var1.charAt(var3) == 167) {
					++var3;
				} else {
					int var4 = ChatAllowedCharacters.allowedCharacters.indexOf(var1.charAt(var3));
					if(var4 >= 0) {
						var2 += this.charWidth[var4 + 32];
					}
				}
			}

			return var2;
		}
	}

	public void func_27278_a(String var1, int var2, int var3, int var4, int var5) {
		String[] var6 = var1.split("\n");
		if(var6.length > 1) {
			for(int var11 = 0; var11 < var6.length; ++var11) {
				this.func_27278_a(var6[var11], var2, var3, var4, var5);
				var3 += this.func_27277_a(var6[var11], var4);
			}

		} else {
			String[] var7 = var1.split(" ");
			int var8 = 0;

			while(var8 < var7.length) {
				String var9;
				for(var9 = var7[var8++] + " "; var8 < var7.length && this.getStringWidth(var9 + var7[var8]) < var4; var9 = var9 + var7[var8++] + " ") {
					;
				}

				int var10;
				for(; this.getStringWidth(var9) > var4; var9 = var9.substring(var10)) {
					for(var10 = 0; this.getStringWidth(var9.substring(0, var10 + 1)) <= var4; ++var10) {
						;
					}

					if(var9.substring(0, var10).trim().length() > 0) {
						this.drawString(var9.substring(0, var10), var2, var3, var5);
						var3 += 8;
					}
				}

				if(var9.trim().length() > 0) {
					this.drawString(var9, var2, var3, var5);
					var3 += 8;
				}
			}

		}
	}

	public int func_27277_a(String var1, int var2) {
		String[] var3 = var1.split("\n");
		int var5;
		if(var3.length > 1) {
			int var9 = 0;

			for(var5 = 0; var5 < var3.length; ++var5) {
				var9 += this.func_27277_a(var3[var5], var2);
			}

			return var9;
		} else {
			String[] var4 = var1.split(" ");
			var5 = 0;
			int var6 = 0;

			while(var5 < var4.length) {
				String var7;
				for(var7 = var4[var5++] + " "; var5 < var4.length && this.getStringWidth(var7 + var4[var5]) < var2; var7 = var7 + var4[var5++] + " ") {
					;
				}

				int var8;
				for(; this.getStringWidth(var7) > var2; var7 = var7.substring(var8)) {
					for(var8 = 0; this.getStringWidth(var7.substring(0, var8 + 1)) <= var2; ++var8) {
						;
					}

					if(var7.substring(0, var8).trim().length() > 0) {
						var6 += 8;
					}
				}

				if(var7.trim().length() > 0) {
					var6 += 8;
				}
			}
//BukkitContrib HD Start
			if(var6 < var5 / 16) {
//BukkitContrib HD End
				var6 += 8;
			}

			return var6;
		}
	}
//BukkitContrib HD Start
	public void initialize(GameSettings var1, String var2, RenderEngine var3) {
		this.charWidth = new int[256];
		this.fontTextureName = 0;
		this.buffer = GLAllocation.createDirectIntBuffer(1024 /*GL_FRONT_LEFT*/);

		BufferedImage var4;
		try {
			var4 = TextureUtils.getResourceAsBufferedImage(var2);
		} catch (IOException var18) {
			throw new RuntimeException(var18);
		}

		int var5 = var4.getWidth();
		int var6 = var4.getHeight();
		int[] var7 = new int[var5 * var6];
		var4.getRGB(0, 0, var5, var6, var7, 0, var5);
		int var8 = 0;

		int var9;
		int var10;
		int var11;
		int var12;
		int var15;
		int var16;
		while(var8 < 256) {
			var9 = var8 % 16;
			var10 = var8 / 16;
			var11 = var5 / 16 - 1;

			while(true) {
				if(var11 >= 0) {
					var12 = var9 * (var5 / 16) + var11;
					boolean var13 = true;

					for(int var14 = 0; var14 < var5 / 16 && var13; ++var14) {
						var15 = (var10 * (var5 / 16) + var14) * var5;
						var16 = var7[var12 + var15] & 255;
						if(var16 > 0) {
							var13 = false;
						}
					}

					if(var13) {
						--var11;
						continue;
					}
				}

				if(var8 == 32) {
					var11 = var5 / 64;
				}

				this.charWidth[var8] = (128 * var11 + 256) / var5;
				++var8;
				break;
			}
		}

		this.fontTextureName = var3.allocateAndSetupTexture(var4);
		this.fontDisplayLists = GLAllocation.generateDisplayLists(288);
		Tessellator var19 = Tessellator.instance;

		for(var9 = 0; var9 < 256; ++var9) {
			GL11.glNewList(this.fontDisplayLists + var9, 4864 /*GL_COMPILE*/);
			var19.startDrawingQuads();
			var10 = var9 % 16 * 8;
			var11 = var9 / 16 * 8;
			float var20 = 7.99F;
			float var21 = 0.0F;
			float var24 = 0.0F;
			var19.addVertexWithUV(0.0D, (double)(0.0F + var20), 0.0D, (double)((float)var10 / 128.0F + var21), (double)(((float)var11 + var20) / 128.0F + var24));
			var19.addVertexWithUV((double)(0.0F + var20), (double)(0.0F + var20), 0.0D, (double)(((float)var10 + var20) / 128.0F + var21), (double)(((float)var11 + var20) / 128.0F + var24));
			var19.addVertexWithUV((double)(0.0F + var20), 0.0D, 0.0D, (double)(((float)var10 + var20) / 128.0F + var21), (double)((float)var11 / 128.0F + var24));
			var19.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)((float)var10 / 128.0F + var21), (double)((float)var11 / 128.0F + var24));
			var19.draw();
			GL11.glTranslatef((float)this.charWidth[var9], 0.0F, 0.0F);
			GL11.glEndList();
		}

		for(var9 = 0; var9 < 32; ++var9) {
			var10 = (var9 >> 3 & 1) * 85;
			var11 = (var9 >> 2 & 1) * 170 + var10;
			var12 = (var9 >> 1 & 1) * 170 + var10;
			int var22 = (var9 >> 0 & 1) * 170 + var10;
			if(var9 == 6) {
				var11 += 85;
			}

			boolean var23 = var9 >= 16;
			if(var1.anaglyph) {
				var15 = (var11 * 30 + var12 * 59 + var22 * 11) / 100;
				var16 = (var11 * 30 + var12 * 70) / 100;
				int var17 = (var11 * 30 + var22 * 70) / 100;
				var11 = var15;
				var12 = var16;
				var22 = var17;
			}

			if(var23) {
				var11 /= 4;
				var12 /= 4;
				var22 /= 4;
			}

			GL11.glNewList(this.fontDisplayLists + 256 + var9, 4864 /*GL_COMPILE*/);
			GL11.glColor3f((float)var11 / 255.0F, (float)var12 / 255.0F, (float)var22 / 255.0F);
			GL11.glEndList();
		}
//BukkitContrib HD End
	}
}
