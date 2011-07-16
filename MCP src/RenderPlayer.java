package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderLiving;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class RenderPlayer extends RenderLiving {

	private ModelBiped modelBipedMain;
	private ModelBiped modelArmorChestplate;
	private ModelBiped modelArmor;
	private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};


	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
		this.modelBipedMain = (ModelBiped)this.mainModel;
		this.modelArmorChestplate = new ModelBiped(1.0F);
		this.modelArmor = new ModelBiped(0.5F);
	}

	protected boolean setArmorModel(EntityPlayer var1, int var2, float var3) {
		ItemStack var4 = var1.inventory.armorItemInSlot(3 - var2);
		if(var4 != null) {
			Item var5 = var4.getItem();
			if(var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (var2 == 2?2:1) + ".png");
				ModelBiped var7 = var2 == 2?this.modelArmor:this.modelArmorChestplate;
				var7.bipedHead.showModel = var2 == 0;
				var7.bipedHeadwear.showModel = var2 == 0;
				var7.bipedBody.showModel = var2 == 1 || var2 == 2;
				var7.bipedRightArm.showModel = var2 == 1;
				var7.bipedLeftArm.showModel = var2 == 1;
				var7.bipedRightLeg.showModel = var2 == 2 || var2 == 3;
				var7.bipedLeftLeg.showModel = var2 == 2 || var2 == 3;
				this.setRenderPassModel(var7);
				return true;
			}
		}

		return false;
	}

	public void renderPlayer(EntityPlayer var1, double var2, double var4, double var6, float var8, float var9) {
		ItemStack var10 = var1.inventory.getCurrentItem();
		this.modelArmorChestplate.field_1278_i = this.modelArmor.field_1278_i = this.modelBipedMain.field_1278_i = var10 != null;
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = var1.isSneaking();
		double var11 = var4 - (double)var1.yOffset;
		if(var1.isSneaking() && !(var1 instanceof EntityPlayerSP)) {
			var11 -= 0.125D;
		}

		super.doRenderLiving(var1, var2, var11, var6, var8, var9);
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestplate.field_1278_i = this.modelArmor.field_1278_i = this.modelBipedMain.field_1278_i = false;
	}

	protected void renderName(EntityPlayer var1, double var2, double var4, double var6) {
		if(Minecraft.isGuiEnabled() && var1 != this.renderManager.livingPlayer) {
			float var8 = 1.6F;
			float var9 = 0.016666668F * var8;
			float var10 = var1.getDistanceToEntity(this.renderManager.livingPlayer);
			float var11 = var1.isSneaking()?32.0F:64.0F;
			if(var10 < var11) {
				String var12 = var1.username;
				//BukkitContrib Start
				String title = BukkitContrib.entityLabel.get(var1.entityId);
				if (title != null) {
					var12 = title;
				}
				if (!var12.equals("[hide]")) {
					String lines[] = var12.split("\\n");
					double y = var4;
					for (int line = 0; line < lines.length; line++) {
						var12 = lines[line];
						var4 = y + (0.275D * (lines.length - line - 1));
						if(!var1.isSneaking()) {
							if(var1.isPlayerSleeping()) {
								this.renderLivingLabel(var1, var12, var2, var4 - 1.5D, var6, 64);
							} else {
								this.renderLivingLabel(var1, var12, var2, var4, var6, 64);
							}
						} else {
							FontRenderer var13 = this.getFontRendererFromRenderManager();
							GL11.glPushMatrix();
							GL11.glTranslatef((float)var2 + 0.0F, (float)var4 + 2.3F, (float)var6);
							GL11.glNormal3f(0.0F, 1.0F, 0.0F);
							GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
							GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
							GL11.glScalef(-var9, -var9, var9);
							GL11.glDisable(2896 /*GL_LIGHTING*/);
							GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
							GL11.glDepthMask(false);
							GL11.glEnable(3042 /*GL_BLEND*/);
							GL11.glBlendFunc(770, 771);
							Tessellator var14 = Tessellator.instance;
							GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
							var14.startDrawingQuads();
							int var15 = var13.getStringWidth(var12) / 2;
							var14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
							var14.addVertex((double)(-var15 - 1), -1.0D, 0.0D);
							var14.addVertex((double)(-var15 - 1), 8.0D, 0.0D);
							var14.addVertex((double)(var15 + 1), 8.0D, 0.0D);
							var14.addVertex((double)(var15 + 1), -1.0D, 0.0D);
							var14.draw();
							GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
							GL11.glDepthMask(true);
							var13.drawString(var12, -var13.getStringWidth(var12) / 2, 0, 553648127);
							GL11.glEnable(2896 /*GL_LIGHTING*/);
							GL11.glDisable(3042 /*GL_BLEND*/);
							GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
							GL11.glPopMatrix();
						}
					}
				}
				//BukkitContrib End
			}
		}

	}

	protected void renderSpecials(EntityPlayer var1, float var2) {
		ItemStack var3 = var1.inventory.armorItemInSlot(3);
		if(var3 != null && var3.getItem().shiftedIndex < 256) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(0.0625F);
			if(RenderBlocks.renderItemIn3d(Block.blocksList[var3.itemID].getRenderType())) {
				float var4 = 0.625F;
				GL11.glTranslatef(0.0F, -0.25F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var4, -var4, var4);
			}

			this.renderManager.itemRenderer.renderItem(var1, var3);
			GL11.glPopMatrix();
		}

		float var5;
		if(var1.username.equals("deadmau5") && this.loadDownloadableImageTexture(var1.skinUrl, (String)null)) {
			for(int var19 = 0; var19 < 2; ++var19) {
				var5 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2 - (var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2);
				float var6 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
				GL11.glPushMatrix();
				GL11.glRotatef(var5, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.375F * (float)(var19 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -0.375F, 0.0F);
				GL11.glRotatef(-var6, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-var5, 0.0F, 1.0F, 0.0F);
				float var7 = 1.3333334F;
				GL11.glScalef(var7, var7, var7);
				this.modelBipedMain.renderEars(0.0625F);
				GL11.glPopMatrix();
			}
		}

		if(this.loadDownloadableImageTexture(var1.playerCloakUrl, (String)null)) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
			double var21 = var1.field_20066_r + (var1.field_20063_u - var1.field_20066_r) * (double)var2 - (var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2);
			double var22 = var1.field_20065_s + (var1.field_20062_v - var1.field_20065_s) * (double)var2 - (var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2);
			double var8 = var1.field_20064_t + (var1.field_20061_w - var1.field_20064_t) * (double)var2 - (var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2);
			float var10 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2;
			double var11 = (double)MathHelper.sin(var10 * 3.1415927F / 180.0F);
			double var13 = (double)(-MathHelper.cos(var10 * 3.1415927F / 180.0F));
			float var15 = (float)var22 * 10.0F;
			if(var15 < -6.0F) {
				var15 = -6.0F;
			}

			if(var15 > 32.0F) {
				var15 = 32.0F;
			}

			float var16 = (float)(var21 * var11 + var8 * var13) * 100.0F;
			float var17 = (float)(var21 * var13 - var8 * var11) * 100.0F;
			if(var16 < 0.0F) {
				var16 = 0.0F;
			}

			float var18 = var1.field_775_e + (var1.field_774_f - var1.field_775_e) * var2;
			var15 += MathHelper.sin((var1.prevDistanceWalkedModified + (var1.distanceWalkedModified - var1.prevDistanceWalkedModified) * var2) * 6.0F) * 32.0F * var18;
			if(var1.isSneaking()) {
				var15 += 25.0F;
			}

			GL11.glRotatef(6.0F + var16 / 2.0F + var15, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var17 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var17 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(0.0625F);
			GL11.glPopMatrix();
		}

		ItemStack var20 = var1.inventory.getCurrentItem();
		if(var20 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			if(var1.fishEntity != null) {
				var20 = new ItemStack(Item.stick);
			}

			if(var20.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var20.itemID].getRenderType())) {
				var5 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				var5 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var5, -var5, var5);
			} else if(Item.itemsList[var20.itemID].isFull3D()) {
				var5 = 0.625F;
				if(Item.itemsList[var20.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(var5, -var5, var5);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var5 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(var5, var5, var5);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(var1, var20);
			GL11.glPopMatrix();
		}

	}

	protected void func_186_b(EntityPlayer var1, float var2) {
		float var3 = 0.9375F;
		GL11.glScalef(var3, var3, var3);
	}

	public void drawFirstPersonHand() {
		this.modelBipedMain.onGround = 0.0F;
		this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		this.modelBipedMain.bipedRightArm.render(0.0625F);
	}

	protected void func_22016_b(EntityPlayer var1, double var2, double var4, double var6) {
		if(var1.isEntityAlive() && var1.isPlayerSleeping()) {
			super.func_22012_b(var1, var2 + (double)var1.field_22063_x, var4 + (double)var1.field_22062_y, var6 + (double)var1.field_22061_z);
		} else {
			super.func_22012_b(var1, var2, var4, var6);
		}

	}

	protected void func_22017_a(EntityPlayer var1, float var2, float var3, float var4) {
		if(var1.isEntityAlive() && var1.isPlayerSleeping()) {
			GL11.glRotatef(var1.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(var1), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.rotateCorpse(var1, var2, var3, var4);
		}

	}

	// $FF: synthetic method
	// $FF: bridge method
	protected void passSpecialRender(EntityLiving var1, double var2, double var4, double var6) {
		this.renderName((EntityPlayer)var1, var2, var4, var6);
	}

	// $FF: synthetic method
	// $FF: bridge method
	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.func_186_b((EntityPlayer)var1, var2);
	}

	// $FF: synthetic method
	// $FF: bridge method
	protected boolean shouldRenderPass(EntityLiving var1, int var2, float var3) {
		return this.setArmorModel((EntityPlayer)var1, var2, var3);
	}

	// $FF: synthetic method
	// $FF: bridge method
	protected void renderEquippedItems(EntityLiving var1, float var2) {
		this.renderSpecials((EntityPlayer)var1, var2);
	}

	// $FF: synthetic method
	// $FF: bridge method
	protected void rotateCorpse(EntityLiving var1, float var2, float var3, float var4) {
		this.func_22017_a((EntityPlayer)var1, var2, var3, var4);
	}

	// $FF: synthetic method
	// $FF: bridge method
	protected void func_22012_b(EntityLiving var1, double var2, double var4, double var6) {
		this.func_22016_b((EntityPlayer)var1, var2, var4, var6);
	}

	// $FF: synthetic method
	// $FF: bridge method
	public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}

	// $FF: synthetic method
	// $FF: bridge method
	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}

}
