// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
//Spout Start
import org.bukkitcontrib.gui.ScreenType;
import org.lwjgl.input.Keyboard;
//Spout End

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, GameSettings, GuiSmallButton, 
//            GuiButton

public class GuiControls extends GuiScreen
{

    public GuiControls(GuiScreen guiscreen, GameSettings gamesettings)
    {
        screenTitle = "Controls";
        buttonId = -1;
        parentScreen = guiscreen;
        options = gamesettings;
    }

    private int func_20080_j()
    {
        return width / 2 - 155;
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        int i = func_20080_j();
        for(int j = 0; j < options.keyBindings.length; j++)
        {
            controlList.add(new GuiSmallButton(j, i + (j % 2) * 160, height / 6 + 24 * (j >> 1), 70, 20, options.getOptionDisplayString(j)));
        }

        controlList.add(new GuiButton(200, width / 2 - 100, height / 6 + 168, stringtranslate.translateKey("gui.done")));
        screenTitle = stringtranslate.translateKey("controls.title");
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        for(int i = 0; i < options.keyBindings.length; i++)
        {
            ((GuiButton)controlList.get(i)).displayString = options.getOptionDisplayString(i);
        }

        if(guibutton.id == 200)
        {
            mc.displayGuiScreen(parentScreen);
        } else
        {
            buttonId = guibutton.id;
            guibutton.displayString = (new StringBuilder()).append("> ").append(options.getOptionDisplayString(guibutton.id)).append(" <").toString();
        }
    }

    protected void keyTyped(char c, int i)
    {
    	if(i<0){
    		i+=256;
    	}
        if(buttonId >= 0)
        {
            options.setKeyBinding(buttonId, i);
            ((GuiButton)controlList.get(buttonId)).displayString = options.getOptionDisplayString(buttonId);
            buttonId = -1;
        } else
        {
            super.keyTyped(c, i);
        }
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, screenTitle, width / 2, 20, 0xffffff);
        int k = func_20080_j();
        for(int l = 0; l < options.keyBindings.length; l++)
        {
            drawString(fontRenderer, options.getKeyBindingDescription(l), k + (l % 2) * 160 + 70 + 6, height / 6 + 24 * (l >> 1) + 7, -1);
        }

        super.drawScreen(i, j, f);
    }

    private GuiScreen parentScreen;
    protected String screenTitle;
    private GameSettings options;
    private int buttonId;
    
    //Spout Start
	@Override
	public void handleKeyboardInput() {
		super.handleKeyboardInput();
		Minecraft.handleKeyPress(Keyboard.getEventKey(), Keyboard.getEventKeyState(), ScreenType.CONTROLS_MENU);
	}
	//Spout End
}
