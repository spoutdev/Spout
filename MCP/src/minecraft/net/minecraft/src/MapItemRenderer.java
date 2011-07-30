package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GameSettings;
import net.minecraft.src.MapColor;
import net.minecraft.src.MapCoord;
import net.minecraft.src.MapData;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class MapItemRenderer {

	private int[] field_28159_a = new int[16384 /*GL_LIGHT0*/];
	private int field_28158_b;
	private GameSettings field_28161_c;
	private FontRenderer field_28160_d;


	public MapItemRenderer(FontRenderer var1, GameSettings var2, RenderEngine var3) {
		this.field_28161_c = var2;
		this.field_28160_d = var1;
		this.field_28158_b = var3.allocateAndSetupTexture(new BufferedImage(128, 128, 2));

		for(int var4 = 0; var4 < 16384 /*GL_LIGHT0*/; ++var4) {
			this.field_28159_a[var4] = 0;
		}

	}

	public void func_28157_a(EntityPlayer var1, RenderEngine var2, MapData var3) {
		for(int var4 = 0; var4 < 16384 /*GL_LIGHT0*/; ++var4) {
			byte var5 = var3.field_28176_f[var4];
			if(var5 / 4 == 0) {
				this.field_28159_a[var4] = (var4 + var4 / 128 & 1) * 8 + 16 << 24;
			} else {
				int var6 = MapColor.mapColorArray[var5 / 4].colorValue;
				int var7 = var5 & 3;
				short var8 = 220;
				if(var7 == 2) {
					var8 = 255;
				}

				if(var7 == 0) {
					var8 = 180;
				}

				int var9 = (var6 >> 16 & 255) * var8 / 255;
				int var10 = (var6 >> 8 & 255) * var8 / 255;
				int var11 = (var6 & 255) * var8 / 255;
				if(this.field_28161_c.anaglyph) {
					int var12 = (var9 * 30 + var10 * 59 + var11 * 11) / 100;
					int var13 = (var9 * 30 + var10 * 70) / 100;
					int var14 = (var9 * 30 + var11 * 70) / 100;
					var9 = var12;
					var10 = var13;
					var11 = var14;
				}

				this.field_28159_a[var4] = -16777216 | var9 << 16 | var10 << 8 | var11;
			}
		}

		var2.createTextureFromBytes(this.field_28159_a, 128, 128, this.field_28158_b);
		byte var15 = 0;
		byte var16 = 0;
		Tessellator var17 = Tessellator.instance;
		float var18 = 0.0F;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.field_28158_b);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
		var17.startDrawingQuads();
		var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 0.0D, 1.0D);
		var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 1.0D, 1.0D);
		var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 1.0D, 0.0D);
		var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 0.0D, 0.0D);
		var17.draw();
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		var2.bindTexture(var2.getTexture("/misc/mapicons.png"));
		Iterator var19 = var3.field_28173_i.iterator();

		while(var19.hasNext()) {
			MapCoord var20 = (MapCoord)var19.next();
			GL11.glPushMatrix();
			GL11.glTranslatef((float)var15 + (float)var20.field_28216_b / 2.0F + 64.0F, (float)var16 + (float)var20.field_28220_c / 2.0F + 64.0F, -0.02F);
			GL11.glRotatef((float)(var20.field_28219_d * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
			GL11.glScalef(4.0F, 4.0F, 3.0F);
			GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
			float var21 = (float)(var20.field_28217_a % 4 + 0) / 4.0F;
			float var23 = (float)(var20.field_28217_a / 4 + 0) / 4.0F;
			float var22 = (float)(var20.field_28217_a % 4 + 1) / 4.0F;
			float var24 = (float)(var20.field_28217_a / 4 + 1) / 4.0F;
			var17.startDrawingQuads();
			var17.addVertexWithUV(-1.0D, 1.0D, 0.0D, (double)var21, (double)var23);
			var17.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)var22, (double)var23);
			var17.addVertexWithUV(1.0D, -1.0D, 0.0D, (double)var22, (double)var24);
			var17.addVertexWithUV(-1.0D, -1.0D, 0.0D, (double)var21, (double)var24);
			var17.draw();
			GL11.glPopMatrix();
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, -0.04F);
		GL11.glScalef(1.0F, 1.0F, 1.0F);
		//Spout Start
		ItemStack inHand = var1.inventory.getCurrentItem();
		String customName = null;
		if (inHand != null) {
			customName = BukkitContrib.getItemManager().getCustomItemName(inHand.itemID, (short)(inHand.getItemDamage()));
		}
		if (customName != null && customName.length() > 0) {
			this.field_28160_d.drawString(customName, var15, var16, -16777216);
		}
		else {
			this.field_28160_d.drawString(var3.field_28168_a, var15, var16, -16777216);
		}
		//Spout End
		GL11.glPopMatrix();
	}
}
