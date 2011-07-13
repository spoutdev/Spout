package net.minecraft.src;
//BukkitContrib HD Start
import com.pclewis.mcpatcher.mod.TileSize;
//BukkitContrib HD End
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MapData;
import net.minecraft.src.MapItemRenderer;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {

	private Minecraft mc;
	private ItemStack itemToRender = null;
	private float equippedProgress = 0.0F;
	private float prevEquippedProgress = 0.0F;
	private RenderBlocks renderBlocksInstance = new RenderBlocks();
	private MapItemRenderer field_28131_f;
	private int field_20099_f = -1;


	public ItemRenderer(Minecraft var1) {
		this.mc = var1;
		this.field_28131_f = new MapItemRenderer(var1.fontRenderer, var1.gameSettings, var1.renderEngine);
	}

	public void renderItem(EntityLiving var1, ItemStack var2) {
		GL11.glPushMatrix();
		if(var2.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var2.itemID].getRenderType())) {
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/terrain.png"));
			this.renderBlocksInstance.renderBlockOnInventory(Block.blocksList[var2.itemID], var2.getItemDamage(), var1.getEntityBrightness(1.0F));
		} else {
			if(var2.itemID < 256) {
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/terrain.png"));
			} else {
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/gui/items.png"));
			}

			Tessellator var3 = Tessellator.instance;
			int var4 = var1.getItemIcon(var2);
			//BukkitContrib HD Start
			float var5 = ((float)(var4 % 16 * TileSize.int_size) + 0.0F) / TileSize.float_size16;
			float var6 = ((float)(var4 % 16 * TileSize.int_size) + TileSize.float_sizeMinus0_01) / TileSize.float_size16;
			float var7 = ((float)(var4 / 16 * TileSize.int_size) + 0.0F) / TileSize.float_size16;
			float var8 = ((float)(var4 / 16 * TileSize.int_size) + TileSize.float_sizeMinus0_01) / TileSize.float_size16;
			//BukkitContrib HD End
			float var9 = 1.0F;
			float var10 = 0.0F;
			float var11 = 0.3F;
			GL11.glEnable('\u803a');
			GL11.glTranslatef(-var10, -var11, 0.0F);
			float var12 = 1.5F;
			GL11.glScalef(var12, var12, var12);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			float var13 = 0.0625F;
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 0.0F, 1.0F);
			var3.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)var6, (double)var8);
			var3.addVertexWithUV((double)var9, 0.0D, 0.0D, (double)var5, (double)var8);
			var3.addVertexWithUV((double)var9, 1.0D, 0.0D, (double)var5, (double)var7);
			var3.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)var6, (double)var7);
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 0.0F, -1.0F);
			var3.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - var13), (double)var6, (double)var7);
			var3.addVertexWithUV((double)var9, 1.0D, (double)(0.0F - var13), (double)var5, (double)var7);
			var3.addVertexWithUV((double)var9, 0.0D, (double)(0.0F - var13), (double)var5, (double)var8);
			var3.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - var13), (double)var6, (double)var8);
			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(-1.0F, 0.0F, 0.0F);

			int var14;
			float var15;
			float var17;
			float var16;
			//BukkitContrib HD Start
			for(var14 = 0; var14 < TileSize.int_size; ++var14) {
				var15 = (float)var14 / TileSize.float_size;
				var16 = var6 + (var5 - var6) * var15 - TileSize.float_texNudge;
			//BukkitContrib HD End
				var17 = var9 * var15;
				var3.addVertexWithUV((double)var17, 0.0D, (double)(0.0F - var13), (double)var16, (double)var8);
				var3.addVertexWithUV((double)var17, 0.0D, 0.0D, (double)var16, (double)var8);
				var3.addVertexWithUV((double)var17, 1.0D, 0.0D, (double)var16, (double)var7);
				var3.addVertexWithUV((double)var17, 1.0D, (double)(0.0F - var13), (double)var16, (double)var7);
			}

			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(1.0F, 0.0F, 0.0F);
			//BukkitContrib HD Start
			for(var14 = 0; var14 < TileSize.int_size; ++var14) {
				var15 = (float)var14 / TileSize.float_size;
				var16 = var6 + (var5 - var6) * var15 - TileSize.float_texNudge;
				var17 = var9 * var15 + TileSize.float_reciprocal;
			//BukkitContrib HD End
				var3.addVertexWithUV((double)var17, 1.0D, (double)(0.0F - var13), (double)var16, (double)var7);
				var3.addVertexWithUV((double)var17, 1.0D, 0.0D, (double)var16, (double)var7);
				var3.addVertexWithUV((double)var17, 0.0D, 0.0D, (double)var16, (double)var8);
				var3.addVertexWithUV((double)var17, 0.0D, (double)(0.0F - var13), (double)var16, (double)var8);
			}

			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 1.0F, 0.0F);
			//BukkitContrib HD Start
			for(var14 = 0; var14 < TileSize.int_size; ++var14) {
				var15 = (float)var14 / TileSize.float_size;
				var16 = var8 + (var7 - var8) * var15 - TileSize.float_texNudge;
				var17 = var9 * var15 + TileSize.float_reciprocal;
			//BukkitContrib HD End
				var3.addVertexWithUV(0.0D, (double)var17, 0.0D, (double)var6, (double)var16);
				var3.addVertexWithUV((double)var9, (double)var17, 0.0D, (double)var5, (double)var16);
				var3.addVertexWithUV((double)var9, (double)var17, (double)(0.0F - var13), (double)var5, (double)var16);
				var3.addVertexWithUV(0.0D, (double)var17, (double)(0.0F - var13), (double)var6, (double)var16);
			}

			var3.draw();
			var3.startDrawingQuads();
			var3.setNormal(0.0F, -1.0F, 0.0F);
			//BukkitContrib HD Start
			for(var14 = 0; var14 < TileSize.int_size; ++var14) {
				var15 = (float)var14 / TileSize.float_size;
				var16 = var8 + (var7 - var8) * var15 - TileSize.float_texNudge;
			//BukkitContrib HD End
				var17 = var9 * var15;
				var3.addVertexWithUV((double)var9, (double)var17, 0.0D, (double)var5, (double)var16);
				var3.addVertexWithUV(0.0D, (double)var17, 0.0D, (double)var6, (double)var16);
				var3.addVertexWithUV(0.0D, (double)var17, (double)(0.0F - var13), (double)var6, (double)var16);
				var3.addVertexWithUV((double)var9, (double)var17, (double)(0.0F - var13), (double)var5, (double)var16);
			}

			var3.draw();
			GL11.glDisable('\u803a');
		}

		GL11.glPopMatrix();
	}

	public void renderItemInFirstPerson(float var1) {
		float var2 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * var1;
		EntityPlayerSP var3 = this.mc.thePlayer;
		float var4 = var3.prevRotationPitch + (var3.rotationPitch - var3.prevRotationPitch) * var1;
		GL11.glPushMatrix();
		GL11.glRotatef(var4, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(var3.prevRotationYaw + (var3.rotationYaw - var3.prevRotationYaw) * var1, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		ItemStack var5 = this.itemToRender;
		float var6 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(var3.posX), MathHelper.floor_double(var3.posY), MathHelper.floor_double(var3.posZ));
		float var8;
		float var9;
		float var10;
		if(var5 != null) {
			int var7 = Item.itemsList[var5.itemID].getColorFromDamage(var5.getItemDamage());
			var8 = (float)(var7 >> 16 & 255) / 255.0F;
			var9 = (float)(var7 >> 8 & 255) / 255.0F;
			var10 = (float)(var7 & 255) / 255.0F;
			GL11.glColor4f(var6 * var8, var6 * var9, var6 * var10, 1.0F);
		} else {
			GL11.glColor4f(var6, var6, var6, 1.0F);
		}

		float var14;
		if(var5 != null && var5.itemID == Item.mapItem.shiftedIndex) {
			GL11.glPushMatrix();
			var14 = 0.8F;
			var8 = var3.getSwingProgress(var1);
			var9 = MathHelper.sin(var8 * 3.1415927F);
			var10 = MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F);
			GL11.glTranslatef(-var10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F * 2.0F) * 0.2F, -var9 * 0.2F);
			var8 = 1.0F - var4 / 45.0F + 0.1F;
			if(var8 < 0.0F) {
				var8 = 0.0F;
			}

			if(var8 > 1.0F) {
				var8 = 1.0F;
			}

			var8 = -MathHelper.cos(var8 * 3.1415927F) * 0.5F + 0.5F;
			GL11.glTranslatef(0.0F, 0.0F * var14 - (1.0F - var2) * 1.2F - var8 * 0.5F + 0.04F, -0.9F * var14);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(var8 * -85.0F, 0.0F, 0.0F, 1.0F);
			GL11.glEnable('\u803a');
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTextureForDownloadableImage(this.mc.thePlayer.skinUrl, this.mc.thePlayer.getEntityTexture()));

			for(int var16 = 0; var16 < 2; ++var16) {
				int var20 = var16 * 2 - 1;
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)var20);
				GL11.glRotatef((float)(-45 * var20), 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef((float)(-65 * var20), 0.0F, 1.0F, 0.0F);
				Render var11 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
				RenderPlayer var12 = (RenderPlayer)var11;
				float var13 = 1.0F;
				GL11.glScalef(var13, var13, var13);
				var12.drawFirstPersonHand();
				GL11.glPopMatrix();
			}

			var9 = var3.getSwingProgress(var1);
			var10 = MathHelper.sin(var9 * var9 * 3.1415927F);
			float var17 = MathHelper.sin(MathHelper.sqrt_float(var9) * 3.1415927F);
			GL11.glRotatef(-var10 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-var17 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var17 * 80.0F, 1.0F, 0.0F, 0.0F);
			var9 = 0.38F;
			GL11.glScalef(var9, var9, var9);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
			var10 = 0.015625F;
			GL11.glScalef(var10, var10, var10);
			this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/misc/mapbg.png"));
			Tessellator var21 = Tessellator.instance;
			GL11.glNormal3f(0.0F, 0.0F, -1.0F);
			var21.startDrawingQuads();
			byte var19 = 7;
			var21.addVertexWithUV((double)(0 - var19), (double)(128 + var19), 0.0D, 0.0D, 1.0D);
			var21.addVertexWithUV((double)(128 + var19), (double)(128 + var19), 0.0D, 1.0D, 1.0D);
			var21.addVertexWithUV((double)(128 + var19), (double)(0 - var19), 0.0D, 1.0D, 0.0D);
			var21.addVertexWithUV((double)(0 - var19), (double)(0 - var19), 0.0D, 0.0D, 0.0D);
			var21.draw();
			MapData var22 = Item.mapItem.func_28012_a(var5, this.mc.theWorld);
			this.field_28131_f.func_28157_a(this.mc.thePlayer, this.mc.renderEngine, var22);
			GL11.glPopMatrix();
		} else if(var5 != null) {
			GL11.glPushMatrix();
			var14 = 0.8F;
			var8 = var3.getSwingProgress(var1);
			var9 = MathHelper.sin(var8 * 3.1415927F);
			var10 = MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F);
			GL11.glTranslatef(-var10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F * 2.0F) * 0.2F, -var9 * 0.2F);
			GL11.glTranslatef(0.7F * var14, -0.65F * var14 - (1.0F - var2) * 0.6F, -0.9F * var14);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable('\u803a');
			var8 = var3.getSwingProgress(var1);
			var9 = MathHelper.sin(var8 * var8 * 3.1415927F);
			var10 = MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F);
			GL11.glRotatef(-var9 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-var10 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var10 * 80.0F, 1.0F, 0.0F, 0.0F);
			var8 = 0.4F;
			GL11.glScalef(var8, var8, var8);
			if(var5.getItem().shouldRotateAroundWhenRendering()) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}

			this.renderItem(var3, var5);
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			var14 = 0.8F;
			var8 = var3.getSwingProgress(var1);
			var9 = MathHelper.sin(var8 * 3.1415927F);
			var10 = MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F);
			GL11.glTranslatef(-var10 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F * 2.0F) * 0.4F, -var9 * 0.4F);
			GL11.glTranslatef(0.8F * var14, -0.75F * var14 - (1.0F - var2) * 0.6F, -0.9F * var14);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable('\u803a');
			var8 = var3.getSwingProgress(var1);
			var9 = MathHelper.sin(var8 * var8 * 3.1415927F);
			var10 = MathHelper.sin(MathHelper.sqrt_float(var8) * 3.1415927F);
			GL11.glRotatef(var10 * 70.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-var9 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTextureForDownloadableImage(this.mc.thePlayer.skinUrl, this.mc.thePlayer.getEntityTexture()));
			GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
			GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(5.6F, 0.0F, 0.0F);
			Render var15 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
			RenderPlayer var18 = (RenderPlayer)var15;
			var10 = 1.0F;
			GL11.glScalef(var10, var10, var10);
			var18.drawFirstPersonHand();
			GL11.glPopMatrix();
		}

		GL11.glDisable('\u803a');
		RenderHelper.disableStandardItemLighting();
	}

	public void renderOverlays(float var1) {
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
		int var2;
		if(this.mc.thePlayer.isBurning()) {
			var2 = this.mc.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var2);
			this.renderFireInFirstPerson(var1);
		}

		if(this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
			var2 = MathHelper.floor_double(this.mc.thePlayer.posX);
			int var3 = MathHelper.floor_double(this.mc.thePlayer.posY);
			int var4 = MathHelper.floor_double(this.mc.thePlayer.posZ);
			int var5 = this.mc.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var5);
			int var6 = this.mc.theWorld.getBlockId(var2, var3, var4);
			if(this.mc.theWorld.isBlockNormalCube(var2, var3, var4)) {
				this.renderInsideOfBlock(var1, Block.blocksList[var6].getBlockTextureFromSide(2));
			} else {
				for(int var7 = 0; var7 < 8; ++var7) {
					float var8 = ((float)((var7 >> 0) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
					float var9 = ((float)((var7 >> 1) % 2) - 0.5F) * this.mc.thePlayer.height * 0.2F;
					float var10 = ((float)((var7 >> 2) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
					int var11 = MathHelper.floor_float((float)var2 + var8);
					int var12 = MathHelper.floor_float((float)var3 + var9);
					int var13 = MathHelper.floor_float((float)var4 + var10);
					if(this.mc.theWorld.isBlockNormalCube(var11, var12, var13)) {
						var6 = this.mc.theWorld.getBlockId(var11, var12, var13);
					}
				}
			}

			if(Block.blocksList[var6] != null) {
				this.renderInsideOfBlock(var1, Block.blocksList[var6].getBlockTextureFromSide(2));
			}
		}

		if(this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
			var2 = this.mc.renderEngine.getTexture("/misc/water.png");
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var2);
			this.renderWarpedTextureOverlay(var1);
		}

		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
	}

	private void renderInsideOfBlock(float var1, int var2) {
		Tessellator var3 = Tessellator.instance;
		this.mc.thePlayer.getEntityBrightness(var1);
		float var4 = 0.1F;
		GL11.glColor4f(var4, var4, var4, 0.5F);
		GL11.glPushMatrix();
		float var5 = -1.0F;
		float var6 = 1.0F;
		float var7 = -1.0F;
		float var8 = 1.0F;
		float var9 = -0.5F;
		float var10 = 0.0078125F;
		float var11 = (float)(var2 % 16) / 256.0F - var10;
		float var12 = ((float)(var2 % 16) + 15.99F) / 256.0F + var10;
		float var13 = (float)(var2 / 16) / 256.0F - var10;
		float var14 = ((float)(var2 / 16) + 15.99F) / 256.0F + var10;
		var3.startDrawingQuads();
		var3.addVertexWithUV((double)var5, (double)var7, (double)var9, (double)var12, (double)var14);
		var3.addVertexWithUV((double)var6, (double)var7, (double)var9, (double)var11, (double)var14);
		var3.addVertexWithUV((double)var6, (double)var8, (double)var9, (double)var11, (double)var13);
		var3.addVertexWithUV((double)var5, (double)var8, (double)var9, (double)var12, (double)var13);
		var3.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderWarpedTextureOverlay(float var1) {
		Tessellator var2 = Tessellator.instance;
		float var3 = this.mc.thePlayer.getEntityBrightness(var1);
		GL11.glColor4f(var3, var3, var3, 0.5F);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		GL11.glPushMatrix();
		float var4 = 4.0F;
		float var5 = -1.0F;
		float var6 = 1.0F;
		float var7 = -1.0F;
		float var8 = 1.0F;
		float var9 = -0.5F;
		float var10 = -this.mc.thePlayer.rotationYaw / 64.0F;
		float var11 = this.mc.thePlayer.rotationPitch / 64.0F;
		var2.startDrawingQuads();
		var2.addVertexWithUV((double)var5, (double)var7, (double)var9, (double)(var4 + var10), (double)(var4 + var11));
		var2.addVertexWithUV((double)var6, (double)var7, (double)var9, (double)(0.0F + var10), (double)(var4 + var11));
		var2.addVertexWithUV((double)var6, (double)var8, (double)var9, (double)(0.0F + var10), (double)(0.0F + var11));
		var2.addVertexWithUV((double)var5, (double)var8, (double)var9, (double)(var4 + var10), (double)(0.0F + var11));
		var2.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(3042 /*GL_BLEND*/);
	}

	private void renderFireInFirstPerson(float var1) {
		Tessellator var2 = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		float var3 = 1.0F;

		for(int var4 = 0; var4 < 2; ++var4) {
			GL11.glPushMatrix();
			int var5 = Block.fire.blockIndexInTexture + var4 * 16;
			int var6 = (var5 & 15) << 4;
			int var7 = var5 & 240;
			float var8 = (float)var6 / 256.0F;
			float var9 = ((float)var6 + 15.99F) / 256.0F;
			float var10 = (float)var7 / 256.0F;
			float var11 = ((float)var7 + 15.99F) / 256.0F;
			float var12 = (0.0F - var3) / 2.0F;
			float var13 = var12 + var3;
			float var14 = 0.0F - var3 / 2.0F;
			float var15 = var14 + var3;
			float var16 = -0.5F;
			GL11.glTranslatef((float)(-(var4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GL11.glRotatef((float)(var4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			var2.startDrawingQuads();
			var2.addVertexWithUV((double)var12, (double)var14, (double)var16, (double)var9, (double)var11);
			var2.addVertexWithUV((double)var13, (double)var14, (double)var16, (double)var8, (double)var11);
			var2.addVertexWithUV((double)var13, (double)var15, (double)var16, (double)var8, (double)var10);
			var2.addVertexWithUV((double)var12, (double)var15, (double)var16, (double)var9, (double)var10);
			var2.draw();
			GL11.glPopMatrix();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(3042 /*GL_BLEND*/);
	}

	public void updateEquippedItem() {
		this.prevEquippedProgress = this.equippedProgress;
		EntityPlayerSP var1 = this.mc.thePlayer;
		ItemStack var2 = var1.inventory.getCurrentItem();
		boolean var4 = this.field_20099_f == var1.inventory.currentItem && var2 == this.itemToRender;
		if(this.itemToRender == null && var2 == null) {
			var4 = true;
		}

		if(var2 != null && this.itemToRender != null && var2 != this.itemToRender && var2.itemID == this.itemToRender.itemID && var2.getItemDamage() == this.itemToRender.getItemDamage()) {
			this.itemToRender = var2;
			var4 = true;
		}

		float var5 = 0.4F;
		float var6 = var4?1.0F:0.0F;
		float var7 = var6 - this.equippedProgress;
		if(var7 < -var5) {
			var7 = -var5;
		}

		if(var7 > var5) {
			var7 = var5;
		}

		this.equippedProgress += var7;
		if(this.equippedProgress < 0.1F) {
			this.itemToRender = var2;
			this.field_20099_f = var1.inventory.currentItem;
		}

	}

	public void func_9449_b() {
		this.equippedProgress = 0.0F;
	}

	public void func_9450_c() {
		this.equippedProgress = 0.0F;
	}
}
