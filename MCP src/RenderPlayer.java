// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            RenderLiving, ModelBiped, EntityPlayer, InventoryPlayer, 
//            ItemStack, ItemArmor, ModelRenderer, EntityPlayerSP, 
//            RenderManager, Tessellator, FontRenderer, Item, 
//            Block, RenderBlocks, ItemRenderer, MathHelper, 
//            EntityLiving, Entity

public class RenderPlayer extends RenderLiving
{

    public RenderPlayer()
    {
        super(new ModelBiped(0.0F), 0.5F);
        modelBipedMain = (ModelBiped)mainModel;
        modelArmorChestplate = new ModelBiped(1.0F);
        modelArmor = new ModelBiped(0.5F);
    }

    protected boolean setArmorModel(EntityPlayer entityplayer, int i, float f)
    {
        ItemStack itemstack = entityplayer.inventory.armorItemInSlot(3 - i);
        if(itemstack != null)
        {
            Item item = itemstack.getItem();
            if(item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                loadTexture((new StringBuilder()).append("/armor/").append(armorFilenamePrefix[itemarmor.renderIndex]).append("_").append(i != 2 ? 1 : 2).append(".png").toString());
                ModelBiped modelbiped = i != 2 ? modelArmorChestplate : modelArmor;
                modelbiped.bipedHead.showModel = i == 0;
                modelbiped.bipedHeadwear.showModel = i == 0;
                modelbiped.bipedBody.showModel = i == 1 || i == 2;
                modelbiped.bipedRightArm.showModel = i == 1;
                modelbiped.bipedLeftArm.showModel = i == 1;
                modelbiped.bipedRightLeg.showModel = i == 2 || i == 3;
                modelbiped.bipedLeftLeg.showModel = i == 2 || i == 3;
                setRenderPassModel(modelbiped);
                return true;
            }
        }
        return false;
    }

    public void func_188_a(EntityPlayer entityplayer, double d, double d1, double d2, 
            float f, float f1)
    {
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        modelArmorChestplate.field_1278_i = modelArmor.field_1278_i = modelBipedMain.field_1278_i = itemstack != null;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = entityplayer.isSneaking();
        double d3 = d1 - (double)entityplayer.yOffset;
        if(entityplayer.isSneaking() && !(entityplayer instanceof EntityPlayerSP))
        {
            d3 -= 0.125D;
        }
        super.doRenderLiving(entityplayer, d, d3, d2, f, f1);
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.field_1278_i = modelArmor.field_1278_i = modelBipedMain.field_1278_i = false;
    }

    protected void renderName(EntityPlayer entityplayer, double d, double d1, double d2)
    {
        if(Minecraft.isGuiEnabled() && entityplayer != renderManager.livingPlayer)
        {
            float f = 1.6F;
            float f1 = 0.01666667F * f;
            float f2 = entityplayer.getDistanceToEntity(renderManager.livingPlayer);
            float f3 = entityplayer.isSneaking() ? 32F : 64F;
            if(f2 < f3)
            {
                String s = entityplayer.username;
                //BukkitContrib Start
                String title = BukkitContrib.entityLabel.get(entityplayer.entityId);
                if (title != null) {
                    s = title;
                }
                if (!s.equals("[hide]")) {
                //BukkitContrib End
                if(!entityplayer.isSneaking())
                {
                    if(entityplayer.isPlayerSleeping())
                    {
                        renderLivingLabel(entityplayer, s, d, d1 - 1.5D, d2, 64);
                    } else
                    {
                        renderLivingLabel(entityplayer, s, d, d1, d2, 64);
                    }
                } else
                {
                    FontRenderer fontrenderer = getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)d + 0.0F, (float)d1 + 2.3F, (float)d2);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f1, -f1, f1);
                    GL11.glDisable(2896 /*GL_LIGHTING*/);
                    GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(3042 /*GL_BLEND*/);
                    GL11.glBlendFunc(770, 771);
                    Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                    tessellator.startDrawingQuads();
                    int i = fontrenderer.getStringWidth(s) / 2;
                    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    tessellator.addVertex(-i - 1, -1D, 0.0D);
                    tessellator.addVertex(-i - 1, 8D, 0.0D);
                    tessellator.addVertex(i + 1, 8D, 0.0D);
                    tessellator.addVertex(i + 1, -1D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                    GL11.glDepthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 0x20ffffff);
                    GL11.glEnable(2896 /*GL_LIGHTING*/);
                    GL11.glDisable(3042 /*GL_BLEND*/);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
                //BukkitContrib Start
                }
                //BukkitContrib End
            }
        }
    }

    protected void renderSpecials(EntityPlayer entityplayer, float f)
    {
        ItemStack itemstack = entityplayer.inventory.armorItemInSlot(3);
        if(itemstack != null && itemstack.getItem().shiftedIndex < 256)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedHead.postRender(0.0625F);
            if(RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
            {
                float f1 = 0.625F;
                GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f1, -f1, f1);
            }
            renderManager.itemRenderer.renderItem(entityplayer, itemstack);
            GL11.glPopMatrix();
        }
        if(entityplayer.username.equals("deadmau5") && loadDownloadableImageTexture(entityplayer.skinUrl, null))
        {
            for(int i = 0; i < 2; i++)
            {
                float f2 = (entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f) - (entityplayer.prevRenderYawOffset + (entityplayer.renderYawOffset - entityplayer.prevRenderYawOffset) * f);
                float f6 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f;
                GL11.glPushMatrix();
                GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f6, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.375F * (float)(i * 2 - 1), 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
                GL11.glRotatef(-f6, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
                float f7 = 1.333333F;
                GL11.glScalef(f7, f7, f7);
                modelBipedMain.renderEars(0.0625F);
                GL11.glPopMatrix();
            }

        }
        if(loadDownloadableImageTexture(entityplayer.playerCloakUrl, null))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double d = (entityplayer.field_20066_r + (entityplayer.field_20063_u - entityplayer.field_20066_r) * (double)f) - (entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double)f);
            double d1 = (entityplayer.field_20065_s + (entityplayer.field_20062_v - entityplayer.field_20065_s) * (double)f) - (entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * (double)f);
            double d2 = (entityplayer.field_20064_t + (entityplayer.field_20061_w - entityplayer.field_20064_t) * (double)f) - (entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double)f);
            float f8 = entityplayer.prevRenderYawOffset + (entityplayer.renderYawOffset - entityplayer.prevRenderYawOffset) * f;
            double d3 = MathHelper.sin((f8 * 3.141593F) / 180F);
            double d4 = -MathHelper.cos((f8 * 3.141593F) / 180F);
            float f9 = (float)d1 * 10F;
            if(f9 < -6F)
            {
                f9 = -6F;
            }
            if(f9 > 32F)
            {
                f9 = 32F;
            }
            float f10 = (float)(d * d3 + d2 * d4) * 100F;
            float f11 = (float)(d * d4 - d2 * d3) * 100F;
            if(f10 < 0.0F)
            {
                f10 = 0.0F;
            }
            float f12 = entityplayer.field_775_e + (entityplayer.field_774_f - entityplayer.field_775_e) * f;
            f9 += MathHelper.sin((entityplayer.prevDistanceWalkedModified + (entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified) * f) * 6F) * 32F * f12;
            if(entityplayer.isSneaking())
            {
                f9 += 25F;
            }
            GL11.glRotatef(6F + f10 / 2.0F + f9, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f11 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f11 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            modelBipedMain.renderCloak(0.0625F);
            GL11.glPopMatrix();
        }
        ItemStack itemstack1 = entityplayer.inventory.getCurrentItem();
        if(itemstack1 != null)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            if(entityplayer.fishEntity != null)
            {
                itemstack1 = new ItemStack(Item.stick);
            }
            if(itemstack1.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack1.itemID].getRenderType()))
            {
                float f3 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f3 *= 0.75F;
                GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f3, -f3, f3);
            } else
            if(Item.itemsList[itemstack1.itemID].isFull3D())
            {
                float f4 = 0.625F;
                if(Item.itemsList[itemstack1.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }
                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(f4, -f4, f4);
                GL11.glRotatef(-100F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
            } else
            {
                float f5 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f5, f5, f5);
                GL11.glRotatef(60F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20F, 0.0F, 0.0F, 1.0F);
            }
            renderManager.itemRenderer.renderItem(entityplayer, itemstack1);
            GL11.glPopMatrix();
        }
    }

    protected void func_186_b(EntityPlayer entityplayer, float f)
    {
        float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
    }

    public void drawFirstPersonHand()
    {
        modelBipedMain.onGround = 0.0F;
        modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        modelBipedMain.bipedRightArm.render(0.0625F);
    }

    protected void func_22016_b(EntityPlayer entityplayer, double d, double d1, double d2)
    {
        if(entityplayer.isEntityAlive() && entityplayer.isPlayerSleeping())
        {
            super.func_22012_b(entityplayer, d + (double)entityplayer.field_22063_x, d1 + (double)entityplayer.field_22062_y, d2 + (double)entityplayer.field_22061_z);
        } else
        {
            super.func_22012_b(entityplayer, d, d1, d2);
        }
    }

    protected void func_22017_a(EntityPlayer entityplayer, float f, float f1, float f2)
    {
        if(entityplayer.isEntityAlive() && entityplayer.isPlayerSleeping())
        {
            GL11.glRotatef(entityplayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(getDeathMaxRotation(entityplayer), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270F, 0.0F, 1.0F, 0.0F);
        } else
        {
            super.rotateCorpse(entityplayer, f, f1, f2);
        }
    }

    protected void passSpecialRender(EntityLiving entityliving, double d, double d1, double d2)
    {
        renderName((EntityPlayer)entityliving, d, d1, d2);
    }

    protected void preRenderCallback(EntityLiving entityliving, float f)
    {
        func_186_b((EntityPlayer)entityliving, f);
    }

    protected boolean shouldRenderPass(EntityLiving entityliving, int i, float f)
    {
        return setArmorModel((EntityPlayer)entityliving, i, f);
    }

    protected void renderEquippedItems(EntityLiving entityliving, float f)
    {
        renderSpecials((EntityPlayer)entityliving, f);
    }

    protected void rotateCorpse(EntityLiving entityliving, float f, float f1, float f2)
    {
        func_22017_a((EntityPlayer)entityliving, f, f1, f2);
    }

    protected void func_22012_b(EntityLiving entityliving, double d, double d1, double d2)
    {
        func_22016_b((EntityPlayer)entityliving, d, d1, d2);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2, 
            float f, float f1)
    {
        func_188_a((EntityPlayer)entityliving, d, d1, d2, f, f1);
    }

    public void doRender(Entity entity, double d, double d1, double d2, 
            float f, float f1)
    {
        func_188_a((EntityPlayer)entity, d, d1, d2, f, f1);
    }

    private ModelBiped modelBipedMain;
    private ModelBiped modelArmorChestplate;
    private ModelBiped modelArmor;
    private static final String armorFilenamePrefix[] = {
        "cloth", "chain", "iron", "diamond", "gold"
    };

}
