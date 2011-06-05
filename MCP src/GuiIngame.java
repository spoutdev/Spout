// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            Gui, ScaledResolution, EntityRenderer, EntityPlayerSP, 
//            InventoryPlayer, GameSettings, ItemStack, Block, 
//            RenderEngine, PlayerController, Material, RenderHelper, 
//            FontRenderer, GuiChat, ChatLine, Tessellator, 
//            BlockPortal, RenderItem, StringTranslate

public class GuiIngame extends Gui
{

    public GuiIngame(Minecraft minecraft)
    {
        chatMessageList = new ArrayList();
        rand = new Random();
        field_933_a = null;
        updateCounter = 0;
        recordPlaying = "";
        recordPlayingUpFor = 0;
        field_22065_l = false;
        prevVignetteBrightness = 1.0F;
        mc = minecraft;
    }

    public void renderGameOverlay(float f, boolean flag, int i, int j)
    {
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int k = scaledresolution.getScaledWidth();
        int l = scaledresolution.getScaledHeight();
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.entityRenderer.func_905_b();
        GL11.glEnable(3042 /*GL_BLEND*/);
        if(Minecraft.isFancyGraphicsEnabled())
        {
            renderVignette(mc.thePlayer.getEntityBrightness(f), k, l);
        }
        ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(3);
        if(!mc.gameSettings.thirdPersonView && itemstack != null && itemstack.itemID == Block.pumpkin.blockID)
        {
            renderPumpkinBlur(k, l);
        }
        float f1 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * f;
        if(f1 > 0.0F)
        {
            renderPortalOverlay(f1, k, l);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/gui.png"));
        InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
        zLevel = -90F;
        drawTexturedModalRect(k / 2 - 91, l - 22, 0, 0, 182, 22);
        drawTexturedModalRect((k / 2 - 91 - 1) + inventoryplayer.currentItem * 20, l - 22 - 1, 0, 22, 24, 22);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/icons.png"));
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(775, 769);
        drawTexturedModalRect(k / 2 - 7, l / 2 - 7, 0, 0, 16, 16);
        GL11.glDisable(3042 /*GL_BLEND*/);
        boolean flag1 = (mc.thePlayer.field_9306_bj / 3) % 2 == 1;
        if(mc.thePlayer.field_9306_bj < 10)
        {
            flag1 = false;
        }
        int i1 = mc.thePlayer.health;
        int j1 = mc.thePlayer.prevHealth;
        rand.setSeed(updateCounter * 0x4c627);
        if(mc.playerController.shouldDrawHUD())
        {
            int k1 = mc.thePlayer.getPlayerArmorValue();
            for(int j2 = 0; j2 < 10; j2++)
            {
                int k3 = l - 32;
                if(k1 > 0)
                {
                    int j5 = (k / 2 + 91) - j2 * 8 - 9;
                    if(j2 * 2 + 1 < k1)
                    {
                        drawTexturedModalRect(j5, k3, 34, 9, 9, 9);
                    }
                    if(j2 * 2 + 1 == k1)
                    {
                        drawTexturedModalRect(j5, k3, 25, 9, 9, 9);
                    }
                    if(j2 * 2 + 1 > k1)
                    {
                        drawTexturedModalRect(j5, k3, 16, 9, 9, 9);
                    }
                }
                int k5 = 0;
                if(flag1)
                {
                    k5 = 1;
                }
                int i6 = (k / 2 - 91) + j2 * 8;
                if(i1 <= 4)
                {
                    k3 += rand.nextInt(2);
                }
                drawTexturedModalRect(i6, k3, 16 + k5 * 9, 0, 9, 9);
                if(flag1)
                {
                    if(j2 * 2 + 1 < j1)
                    {
                        drawTexturedModalRect(i6, k3, 70, 0, 9, 9);
                    }
                    if(j2 * 2 + 1 == j1)
                    {
                        drawTexturedModalRect(i6, k3, 79, 0, 9, 9);
                    }
                }
                if(j2 * 2 + 1 < i1)
                {
                    drawTexturedModalRect(i6, k3, 52, 0, 9, 9);
                }
                if(j2 * 2 + 1 == i1)
                {
                    drawTexturedModalRect(i6, k3, 61, 0, 9, 9);
                }
            }

            if(mc.thePlayer.isInsideOfMaterial(Material.water))
            {
				//BukkitContrib Start
                int k2 = (int)Math.ceil(((double)(mc.thePlayer.air - 2) * 10D) / (mc.thePlayer.maxAir * 1D));
                int l3 = (int)Math.ceil(((double)mc.thePlayer.air * 10D) / (mc.thePlayer.maxAir * 1D)) - k2;
				//BukkitContrib End
                for(int l5 = 0; l5 < k2 + l3; l5++)
                {
                    if(l5 < k2)
                    {
                        drawTexturedModalRect((k / 2 - 91) + l5 * 8, l - 32 - 9, 16, 18, 9, 9);
                    } else
                    {
                        drawTexturedModalRect((k / 2 - 91) + l5 * 8, l - 32 - 9, 25, 18, 9, 9);
                    }
                }

            }
        }
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPushMatrix();
        GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        for(int l1 = 0; l1 < 9; l1++)
        {
            int i3 = (k / 2 - 90) + l1 * 20 + 2;
            int i4 = l - 16 - 3;
            renderInventorySlot(l1, i3, i4, f);
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        if(mc.thePlayer.func_22060_M() > 0)
        {
            GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
            GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
            int i2 = mc.thePlayer.func_22060_M();
            float f3 = (float)i2 / 100F;
            if(f3 > 1.0F)
            {
                f3 = 1.0F - (float)(i2 - 100) / 10F;
            }
            int j4 = (int)(220F * f3) << 24 | 0x101020;
            drawRect(0, 0, k, l, j4);
            GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        }
        if(mc.gameSettings.showDebugInfo)
        {
            GL11.glPushMatrix();
            if(Minecraft.hasPaidCheckTime > 0L)
            {
                GL11.glTranslatef(0.0F, 32F, 0.0F);
            }
            fontrenderer.drawStringWithShadow((new StringBuilder()).append("Minecraft Beta 1.6.6 (").append(mc.debug).append(")").toString(), 2, 2, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_6241_m(), 2, 12, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_6262_n(), 2, 22, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_6245_o(), 2, 32, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_21002_o(), 2, 42, 0xffffff);
            long l2 = Runtime.getRuntime().maxMemory();
            long l4 = Runtime.getRuntime().totalMemory();
            long l6 = Runtime.getRuntime().freeMemory();
            long l7 = l4 - l6;
            String s = (new StringBuilder()).append("Used memory: ").append((l7 * 100L) / l2).append("% (").append(l7 / 1024L / 1024L).append("MB) of ").append(l2 / 1024L / 1024L).append("MB").toString();
            drawString(fontrenderer, s, k - fontrenderer.getStringWidth(s) - 2, 2, 0xe0e0e0);
            s = (new StringBuilder()).append("Allocated memory: ").append((l4 * 100L) / l2).append("% (").append(l4 / 1024L / 1024L).append("MB)").toString();
            drawString(fontrenderer, s, k - fontrenderer.getStringWidth(s) - 2, 12, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("x: ").append(mc.thePlayer.posX).toString(), 2, 64, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("y: ").append(mc.thePlayer.posY).toString(), 2, 72, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("z: ").append(mc.thePlayer.posZ).toString(), 2, 80, 0xe0e0e0);
            GL11.glPopMatrix();
        }
        if(recordPlayingUpFor > 0)
        {
            float f2 = (float)recordPlayingUpFor - f;
            int j3 = (int)((f2 * 256F) / 20F);
            if(j3 > 255)
            {
                j3 = 255;
            }
            if(j3 > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(k / 2, l - 48, 0.0F);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
                int k4 = 0xffffff;
                if(field_22065_l)
                {
                    k4 = Color.HSBtoRGB(f2 / 50F, 0.7F, 0.6F) & 0xffffff;
                }
                fontrenderer.drawString(recordPlaying, -fontrenderer.getStringWidth(recordPlaying) / 2, -4, k4 + (j3 << 24));
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glPopMatrix();
            }
        }
        byte byte0 = 10;
        boolean flag2 = false;
        if(mc.currentScreen instanceof GuiChat)
        {
            byte0 = 20;
            flag2 = true;
        }
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, l - 48, 0.0F);
        for(int i5 = 0; i5 < chatMessageList.size() && i5 < byte0; i5++)
        {
            if(((ChatLine)chatMessageList.get(i5)).updateCounter >= 200 && !flag2)
            {
                continue;
            }
            double d = (double)((ChatLine)chatMessageList.get(i5)).updateCounter / 200D;
            d = 1.0D - d;
            d *= 10D;
            if(d < 0.0D)
            {
                d = 0.0D;
            }
            if(d > 1.0D)
            {
                d = 1.0D;
            }
            d *= d;
            int j6 = (int)(255D * d);
            if(flag2)
            {
                j6 = 255;
            }
            if(j6 > 0)
            {
                byte byte1 = 2;
                int k6 = -i5 * 9;
                String s1 = ((ChatLine)chatMessageList.get(i5)).message;
                drawRect(byte1, k6 - 1, byte1 + 320, k6 + 8, j6 / 2 << 24);
                GL11.glEnable(3042 /*GL_BLEND*/);
                fontrenderer.drawStringWithShadow(s1, byte1, k6, 0xffffff + (j6 << 24));
            }
        }

        GL11.glPopMatrix();
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }

    private void renderPumpkinBlur(int i, int j)
    {
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(i, j, -90D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(i, 0.0D, -90D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderVignette(float f, int i, int j)
    {
        f = 1.0F - f;
        if(f < 0.0F)
        {
            f = 0.0F;
        }
        if(f > 1.0F)
        {
            f = 1.0F;
        }
        prevVignetteBrightness += (double)(f - prevVignetteBrightness) * 0.01D;
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(0, 769);
        GL11.glColor4f(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(i, j, -90D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(i, 0.0D, -90D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(770, 771);
    }

    private void renderPortalOverlay(float f, int i, int j)
    {
        if(f < 1.0F)
        {
            f *= f;
            f *= f;
            f = f * 0.8F + 0.2F;
        }
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
        float f1 = (float)(Block.portal.blockIndexInTexture % 16) / 16F;
        float f2 = (float)(Block.portal.blockIndexInTexture / 16) / 16F;
        float f3 = (float)(Block.portal.blockIndexInTexture % 16 + 1) / 16F;
        float f4 = (float)(Block.portal.blockIndexInTexture / 16 + 1) / 16F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, f1, f4);
        tessellator.addVertexWithUV(i, j, -90D, f3, f4);
        tessellator.addVertexWithUV(i, 0.0D, -90D, f3, f2);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, f1, f2);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderInventorySlot(int i, int j, int k, float f)
    {
        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[i];
        if(itemstack == null)
        {
            return;
        }
        float f1 = (float)itemstack.animationsToGo - f;
        if(f1 > 0.0F)
        {
            GL11.glPushMatrix();
            float f2 = 1.0F + f1 / 5F;
            GL11.glTranslatef(j + 8, k + 12, 0.0F);
            GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
            GL11.glTranslatef(-(j + 8), -(k + 12), 0.0F);
        }
        itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, j, k);
        if(f1 > 0.0F)
        {
            GL11.glPopMatrix();
        }
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, j, k);
    }

    public void updateTick()
    {
        if(recordPlayingUpFor > 0)
        {
            recordPlayingUpFor--;
        }
        updateCounter++;
        for(int i = 0; i < chatMessageList.size(); i++)
        {
            ((ChatLine)chatMessageList.get(i)).updateCounter++;
        }

    }

    public void func_28097_b()
    {
        chatMessageList.clear();
    }

    public void addChatMessage(String s)
    {
        int i;
        for(; mc.fontRenderer.getStringWidth(s) > 320; s = s.substring(i))
        {
            for(i = 1; i < s.length() && mc.fontRenderer.getStringWidth(s.substring(0, i + 1)) <= 320; i++) { }
            addChatMessage(s.substring(0, i));
        }

        chatMessageList.add(0, new ChatLine(s));
        for(; chatMessageList.size() > 50; chatMessageList.remove(chatMessageList.size() - 1)) { }
    }

    public void setRecordPlayingMessage(String s)
    {
        recordPlaying = (new StringBuilder()).append("Now playing: ").append(s).toString();
        recordPlayingUpFor = 60;
        field_22065_l = true;
    }

    public void func_22064_c(String s)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        String s1 = stringtranslate.translateKey(s);
        addChatMessage(s1);
    }

    private static RenderItem itemRenderer = new RenderItem();
    private java.util.List chatMessageList;
    private Random rand;
    private Minecraft mc;
    public String field_933_a;
    private int updateCounter;
    private String recordPlaying;
    private int recordPlayingUpFor;
    private boolean field_22065_l;
    public float damageGuiPartialTime;
    float prevVignetteBrightness;

}
