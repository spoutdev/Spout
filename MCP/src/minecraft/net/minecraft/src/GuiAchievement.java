package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Achievement;
import net.minecraft.src.Gui;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiAchievement extends Gui {

	private Minecraft theGame;
	private int achievementWindowWidth;
	private int achievementWindowHeight;
	private String field_25085_d;
	private String field_25084_e;
	private Achievement theAchievement;
	private long field_25083_f;
	private RenderItem itemRender;
	private boolean field_27103_i;
	//BukkitContrib Start
	private boolean customNotification = false;
	private int itemId;
	private short data = -1;
	private int time = -1;
	//BukkitContrib End


	public GuiAchievement(Minecraft var1) {
		this.theGame = var1;
		this.itemRender = new RenderItem();
	}

	public void queueTakenAchievement(Achievement var1) {
		this.field_25085_d = StatCollector.translateToLocal("achievement.get");
		this.field_25084_e = var1.statName;
		this.field_25083_f = System.currentTimeMillis();
		this.theAchievement = var1;
		this.field_27103_i = false;
		//BukkitContrib Start
		customNotification = false;
		time = -1;
		data = -1;
		//BukkitContrib End
	}

	public void queueAchievementInformation(Achievement var1) {
		this.field_25085_d = var1.statName;
		this.field_25084_e = var1.getDescription();
		this.field_25083_f = System.currentTimeMillis() - 2500L;
		this.theAchievement = var1;
		this.field_27103_i = true;
		//BukkitContrib Start
		customNotification = false;
		time = -1;
		data = -1;
		//BukkitContrib End
	}

	//BukkiContrib Start
	public void queueNotification(String title, String message, int toRender) {
		field_25085_d = title;
		field_25084_e = message;
		field_25083_f = System.currentTimeMillis();
		theAchievement = null;
		field_27103_i = false;
		customNotification = true;
		this.itemId = toRender;
		this.time = -1;
		data = -1;
	}

	public void queueNotification(String title, String message, int toRender, short data, int time) {
		field_25085_d = title;
		field_25084_e = message;
		field_25083_f = System.currentTimeMillis();
		theAchievement = null;
		field_27103_i = false;
		customNotification = true;
		this.itemId = toRender;
		this.time = time;
		this.data = data;
	}
	//BukkitContrib End

	private void updateAchievementWindowScale() {
		GL11.glViewport(0, 0, this.theGame.displayWidth, this.theGame.displayHeight);
		GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
		GL11.glLoadIdentity();
		this.achievementWindowWidth = this.theGame.displayWidth;
		this.achievementWindowHeight = this.theGame.displayHeight;
		ScaledResolution var1 = new ScaledResolution(this.theGame.gameSettings, this.theGame.displayWidth, this.theGame.displayHeight);
		this.achievementWindowWidth = var1.getScaledWidth();
		this.achievementWindowHeight = var1.getScaledHeight();
		GL11.glClear(256);
		GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)this.achievementWindowWidth, (double)this.achievementWindowHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	public void updateAchievementWindow() {
		if(Minecraft.hasPaidCheckTime > 0L) {
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			GL11.glDepthMask(false);
			RenderHelper.disableStandardItemLighting();
			this.updateAchievementWindowScale();
			String var1 = "Minecraft Beta 1.7.3	Unlicensed Copy :(";
			String var2 = "(Or logged in from another location)";
			String var3 = "Purchase at minecraft.net";
			this.theGame.fontRenderer.drawStringWithShadow(var1, 2, 2, 16777215);
			this.theGame.fontRenderer.drawStringWithShadow(var2, 2, 11, 16777215);
			this.theGame.fontRenderer.drawStringWithShadow(var3, 2, 20, 16777215);
			GL11.glDepthMask(true);
			GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
		}

		if(this.theAchievement != null && this.field_25083_f != 0L || customNotification) { //BukkitContrib
			//BukkitContrib Start
			double delayTime = 3000;
			if (customNotification) {
				if (time < 1) {
					delayTime = 7500;
				}
				else {
					delayTime = time;
				}
			}
			double var8 = (double)(System.currentTimeMillis() - this.field_25083_f) / delayTime;
			//BukkitContrib End
			
			if(!this.field_27103_i && !this.field_27103_i && (var8 < 0.0D || var8 > 1.0D)) {
				this.field_25083_f = 0L;
			} else {
				this.updateAchievementWindowScale();
				GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
				GL11.glDepthMask(false);
				double var9 = var8 * 2.0D;
				if(var9 > 1.0D) {
					var9 = 2.0D - var9;
				}

				var9 *= 4.0D;
				var9 = 1.0D - var9;
				if(var9 < 0.0D) {
					var9 = 0.0D;
				}

				var9 *= var9;
				var9 *= var9;
				int var5 = this.achievementWindowWidth - 160;
				int var6 = 0 - (int)(var9 * 36.0D);
				int var7 = this.theGame.renderEngine.getTexture("/achievement/bg.png");
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var7);
				GL11.glDisable(2896 /*GL_LIGHTING*/);
				this.drawTexturedModalRect(var5, var6, 96, 202, 160, 32);
				if(this.field_27103_i) {
					this.theGame.fontRenderer.func_27278_a(this.field_25084_e, var5 + 30, var6 + 7, 120, -1);
				} else {
					this.theGame.fontRenderer.drawString(this.field_25085_d, var5 + 30, var6 + 7, -256);
					this.theGame.fontRenderer.drawString(this.field_25084_e, var5 + 30, var6 + 18, -1);
				}

				GL11.glPushMatrix();
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				RenderHelper.enableStandardItemLighting();
				GL11.glPopMatrix();
				GL11.glDisable(2896 /*GL_LIGHTING*/);
				GL11.glEnable('\u803a');
				GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
				GL11.glEnable(2896 /*GL_LIGHTING*/);
				//BukkitContrib Start
				ItemStack toRender = theAchievement != null ? theAchievement.theItemStack : null;
				if (customNotification){
					if (data < 1) {
						toRender = new ItemStack(itemId, 1, 0);
					}
					else {
						toRender = new ItemStack(itemId, 1, data);
					}
				}
				if (toRender != null) {
					itemRender.renderItemIntoGUI(theGame.fontRenderer, theGame.renderEngine, toRender, var5 + 8, var6 + 8); 
				}
				//BukkitContrib End
				GL11.glDisable(2896 /*GL_LIGHTING*/);
				GL11.glDepthMask(true);
				GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			}
		}
	}
}
