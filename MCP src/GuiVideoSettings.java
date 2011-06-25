// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, EnumOptions, GuiSmallButton, 
//            GameSettings, GuiSlider, GuiButton, ScaledResolution

public class GuiVideoSettings extends GuiScreen
{

    public GuiVideoSettings(GuiScreen guiscreen, GameSettings gamesettings)
    {
        field_22107_a = "Video Settings";
        field_22110_h = guiscreen;
        field_22109_i = gamesettings;
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        field_22107_a = stringtranslate.translateKey("options.videoTitle");
        int i = 0;
        EnumOptions aenumoptions[] = field_22108_k;
        int j = aenumoptions.length;
        for(int k = 0; k < j; k++)
        {
            EnumOptions enumoptions = aenumoptions[k];
            if(!enumoptions.getEnumFloat())
            {
                controlList.add(new GuiSmallButton(enumoptions.returnEnumOrdinal(), (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), enumoptions, field_22109_i.getKeyBinding(enumoptions)));
            } else
            {
                controlList.add(new GuiSlider(enumoptions.returnEnumOrdinal(), (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), enumoptions, field_22109_i.getKeyBinding(enumoptions), field_22109_i.getOptionFloatValue(enumoptions)));
            }
            i++;
        }
        controlList.add(new GuiButton(200, width / 2 - 100, height / 6 + 168, stringtranslate.translateKey("gui.done")));
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id < 100 && (guibutton instanceof GuiSmallButton))
        {
            //BukkitContrib Start
            int change = 1;
            if (EnumOptions.getEnumOptions(guibutton.id) == EnumOptions.RENDER_DISTANCE && BukkitContrib.getVersion() > 5) {
                byte view = (byte)field_22109_i.renderDistance;
                byte newView = BukkitContrib.getNextRenderDistance(view);
                field_22109_i.renderDistance = newView;
                change = 0;
                if (view != newView) {
                    ((EntityClientPlayerMP)BukkitContrib.getGameInstance().thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketRenderDistance((byte)newView)));
				}
            }
            if (change != 0) {
				field_22109_i.setOptionValue(((GuiSmallButton)guibutton).returnEnumOptions(), change);
				guibutton.displayString = field_22109_i.getKeyBinding(EnumOptions.getEnumOptions(guibutton.id));
			}
        }
		 //BukkitContrib End
        if(guibutton.id == 200)
        {
            mc.gameSettings.saveOptions();
            mc.displayGuiScreen(field_22110_h);
        }
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        setWorldAndResolution(mc, i, j);
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, field_22107_a, width / 2, 20, 0xffffff);
        super.drawScreen(i, j, f);
    }

    private GuiScreen field_22110_h;
    protected String field_22107_a;
    private GameSettings field_22109_i;
    private static EnumOptions field_22108_k[];

    static 
    {
        field_22108_k = (new EnumOptions[] {
            EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.LIMIT_FRAMERATE, EnumOptions.ANAGLYPH, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL
        });
    }
}
