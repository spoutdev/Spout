// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.STRING;

import net.minecraft.client.Minecraft;

import org.bukkitcontrib.gui.CustomScreen;
import org.bukkitcontrib.gui.InGameScreen;
import org.bukkitcontrib.gui.ScreenType;
import org.bukkitcontrib.packet.CustomPacket;
import org.bukkitcontrib.packet.PacketKeyPress;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            Gui, GuiButton, SoundManager, GuiParticle, 
//            Tessellator, RenderEngine, FontRenderer

public class GuiScreen extends Gui
{

    public GuiScreen()
    {
        controlList = new ArrayList();
        field_948_f = false;
        selectedButton = null;
    }

    public void drawScreen(int i, int j, float f)
    {
        for(int k = 0; k < controlList.size(); k++)
        {
            GuiButton guibutton = (GuiButton)controlList.get(k);
            guibutton.drawButton(mc, i, j);
        }

    }

    protected void keyTyped(char c, int i)
    {
        if(i == 1)
        {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }

    public static String getClipboardString()
    {
        try
        {
            java.awt.datatransfer.Transferable transferable = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if(transferable != null && transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor))
            {
                String s = (String)transferable.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                return s;
            }
        }
        catch(Exception exception) { }
        return null;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        if(k == 0)
        {
            for(int l = 0; l < controlList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.mousePressed(mc, i, j))
                {
                    selectedButton = guibutton;
                    mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    actionPerformed(guibutton);
                }
            }

        }
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        if(selectedButton != null && k == 0)
        {
            selectedButton.mouseReleased(i, j);
            selectedButton = null;
        }
    }

    protected void actionPerformed(GuiButton guibutton)
    {
    }

    public void setWorldAndResolution(Minecraft minecraft, int i, int j)
    {
        guiParticles = new GuiParticle(minecraft);
        mc = minecraft;
        fontRenderer = minecraft.fontRenderer;
        width = i;
        height = j;
        controlList.clear();
        initGui();
    }

    public void initGui()
    {
    }

    public void handleInput()
    {
        for(; Mouse.next(); handleMouseInput()) { }
        //Spout Start
        for(; Keyboard.next(); handleKeyboardInput()) { 
        	if(mc.thePlayer instanceof EntityClientPlayerMP){
        		EntityClientPlayerMP player = (EntityClientPlayerMP)mc.thePlayer;
        		ScreenType screen = ScreenType.UNKNOWN;
        		if(this instanceof GuiChat){
        			screen = ScreenType.CHAT_SCREEN;
        		}
        		if(this instanceof GuiSleepMP){
        			screen = ScreenType.SLEEP_SCREEN;
        		}
        		if(this instanceof CustomScreen){
        			screen = ScreenType.CUSTOM_SCREEN;
        		}
        		if(this instanceof GuiInventory){
        			screen = ScreenType.PLAYER_INVENTORY;
        		}
        		if(this instanceof GuiChest){
        			screen = ScreenType.CHEST_INVENTORY;
        		}
        		if(this instanceof GuiDispenser){
        			screen = ScreenType.DISPENSER_INVENTORY;
        		}
        		if(this instanceof GuiFurnace){
        			screen = ScreenType.FURNACE_INVENTORY;
        		}
        		if(this instanceof GuiIngameMenu){
        			screen = ScreenType.INGAME_MENU;
        		}
        		if(this instanceof GuiOptions){
        			screen = ScreenType.OPTIONS_MENU;
        		}
        		if(this instanceof GuiVideoSettings){
        			screen = ScreenType.VIDEO_SETTINGS_MENU;
        		}
        		if(this instanceof GuiControls){
        			screen = ScreenType.CONTROLS_MENU;
        		}
        		if(this instanceof GuiAchievements){
        			screen = ScreenType.ACHIEVEMENTS_SCREEN;
        		}
        		if(this instanceof GuiCrafting){
        			screen = ScreenType.WORKBENCH_INVENTORY;
        		}
        		if(this instanceof GuiGameOver){
        			screen = ScreenType.GAME_OVER_SCREEN;
        		}
        		if(this instanceof GuiEditSign){
        			screen = ScreenType.SIGN_SCREEN;
        		}
        		if(this instanceof GuiStats){
        			screen = ScreenType.STATISTICS_SCREEN;
        		}
        		int i = Keyboard.getEventKey();
        		boolean keyReleased = Keyboard.getEventKeyState();
        		PacketKeyPress packet = new PacketKeyPress((byte)i, keyReleased, (MovementInputFromOptions)player.movementInput, screen);
        		player.sendQueue.addToSendQueue(new CustomPacket(packet));
        	}
        }
        //Spout End
    }

    public void handleMouseInput()
    {
        if(Mouse.getEventButtonState())
        {
            int i = (Mouse.getEventX() * width) / mc.displayWidth;
            int k = height - (Mouse.getEventY() * height) / mc.displayHeight - 1;
            mouseClicked(i, k, Mouse.getEventButton());
        } else
        {
            int j = (Mouse.getEventX() * width) / mc.displayWidth;
            int l = height - (Mouse.getEventY() * height) / mc.displayHeight - 1;
            mouseMovedOrUp(j, l, Mouse.getEventButton());
        }
    }

    public void handleKeyboardInput()
    {
        if(Keyboard.getEventKeyState())
        {
            if(Keyboard.getEventKey() == 87)
            {
                mc.toggleFullscreen();
                return;
            }
            keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    public void updateScreen()
    {
    }

    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        drawWorldBackground(0);
    }

    public void drawWorldBackground(int i)
    {
        if(mc.theWorld != null)
        {
            drawGradientRect(0, 0, width, height, 0xc0101010, 0xd0101010);
        } else
        {
            drawBackground(i);
        }
    }

    public void drawBackground(int i)
    {
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        Tessellator tessellator = Tessellator.instance;
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0x404040);
        tessellator.addVertexWithUV(0.0D, height, 0.0D, 0.0D, (float)height / f + (float)i);
        tessellator.addVertexWithUV(width, height, 0.0D, (float)width / f, (float)height / f + (float)i);
        tessellator.addVertexWithUV(width, 0.0D, 0.0D, (float)width / f, 0 + i);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0 + i);
        tessellator.draw();
    }

    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void deleteWorld(boolean flag, int i)
    {
    }

    public void selectNextField()
    {
    }

    protected Minecraft mc;
    public int width;
    public int height;
    protected List controlList;
    public boolean field_948_f;
    protected FontRenderer fontRenderer;
    public GuiParticle guiParticles;
    private GuiButton selectedButton;
}
