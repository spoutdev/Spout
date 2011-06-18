// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            Gui, RenderItem, StatCollector, Achievement, 
//            ScaledResolution, RenderHelper, FontRenderer, RenderEngine

public class GuiAchievement extends Gui
{

    public GuiAchievement(Minecraft minecraft)
    {
        theGame = minecraft;
        itemRender = new RenderItem();
    }

    public void queueTakenAchievement(Achievement achievement)
    {
        field_25085_d = StatCollector.translateToLocal("achievement.get");
        field_25084_e = achievement.statName;
        field_25083_f = System.currentTimeMillis();
        theAchievement = achievement;
        field_27103_i = false;
        customNotification = false; //BukkitContrib
    }

    public void queueAchievementInformation(Achievement achievement)
    {
        field_25085_d = achievement.statName;
        field_25084_e = achievement.getDescription();
        field_25083_f = System.currentTimeMillis() - 2500L;
        theAchievement = achievement;
        field_27103_i = true;
        customNotification = false; //BukkitContrib
    }
    
    public void queueNotification(String title, String message, int toRender) {
        field_25085_d = title;
        field_25084_e = message;
        field_25083_f = System.currentTimeMillis();
        theAchievement = null;
        field_27103_i = false;
        customNotification = true;
        this.itemId = toRender;
    }

    private void updateAchievementWindowScale()
    {
        GL11.glViewport(0, 0, theGame.displayWidth, theGame.displayHeight);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        achievementWindowWidth = theGame.displayWidth;
        achievementWindowHeight = theGame.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(theGame.gameSettings, theGame.displayWidth, theGame.displayHeight);
        achievementWindowWidth = scaledresolution.getScaledWidth();
        achievementWindowHeight = scaledresolution.getScaledHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, achievementWindowWidth, achievementWindowHeight, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
    }

    public void updateAchievementWindow()
    {
        if(Minecraft.hasPaidCheckTime > 0L)
        {
            GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
            GL11.glDepthMask(false);
            RenderHelper.disableStandardItemLighting();
            updateAchievementWindowScale();
            String s = "Minecraft Beta 1.6.6   Unlicensed Copy :(";
            String s1 = "(Or logged in from another location)";
            String s2 = "Purchase at minecraft.net";
            theGame.fontRenderer.drawStringWithShadow(s, 2, 2, 0xffffff);
            theGame.fontRenderer.drawStringWithShadow(s1, 2, 11, 0xffffff);
            theGame.fontRenderer.drawStringWithShadow(s2, 2, 20, 0xffffff);
            GL11.glDepthMask(true);
            GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        }
        if(!customNotification && theAchievement == null || field_25083_f == 0L) //BukkitContrib
        {
            return;
        }
        double d = (double)(System.currentTimeMillis() - field_25083_f) / 3000D;
        if(!field_27103_i && !field_27103_i && (d < 0.0D || d > 1.0D))
        {
            field_25083_f = 0L;
            return;
        }
        updateAchievementWindowScale();
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        double d1 = d * 2D;
        if(d1 > 1.0D)
        {
            d1 = 2D - d1;
        }
        d1 *= 4D;
        d1 = 1.0D - d1;
        if(d1 < 0.0D)
        {
            d1 = 0.0D;
        }
        d1 *= d1;
        d1 *= d1;
        int i = achievementWindowWidth - 160;
        int j = 0 - (int)(d1 * 36D);
        int k = theGame.renderEngine.getTexture("/achievement/bg.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, k);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        drawTexturedModalRect(i, j, 96, 202, 160, 32);
        if(field_27103_i)
        {
            theGame.fontRenderer.func_27278_a(field_25084_e, i + 30, j + 7, 120, -1);
        } else
        {
            theGame.fontRenderer.drawString(field_25085_d, i + 30, j + 7, -256);
            theGame.fontRenderer.drawString(field_25084_e, i + 30, j + 18, -1);
        }
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        itemRender.renderItemIntoGUI(theGame.fontRenderer, theGame.renderEngine, customNotification ? new ItemStack(itemId, 64, 0) : theAchievement.theItemStack, i + 8, j + 8); //BukkitContrib
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    private Minecraft theGame;
    private int achievementWindowWidth;
    private int achievementWindowHeight;
    private String field_25085_d;
    private String field_25084_e;
    private Achievement theAchievement;
    private long field_25083_f;
    private RenderItem itemRender;
    private boolean field_27103_i;
    //BukkitContrib
    private boolean customNotification = false;
    private int itemId;
}
