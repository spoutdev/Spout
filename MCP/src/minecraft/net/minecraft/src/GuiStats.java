// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

//Spout Start
import org.bukkitcontrib.gui.ScreenType;
import org.lwjgl.input.Keyboard;
//Spout End

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StatCollector, GuiSlotStatsGeneral, GuiSlotStatsItem, 
//            GuiSlotStatsBlock, StringTranslate, GuiButton, GuiSlot, 
//            RenderHelper, Item, RenderItem, RenderEngine, 
//            Tessellator, StatFileWriter, FontRenderer

public class GuiStats extends GuiScreen
{

    public GuiStats(GuiScreen guiscreen, StatFileWriter statfilewriter)
    {
        field_27154_i = "Select world";
        field_27155_p = null;
        field_27152_a = guiscreen;
        field_27156_o = statfilewriter;
    }

    public void initGui()
    {
        field_27154_i = StatCollector.translateToLocal("gui.stats");
        field_27151_l = new GuiSlotStatsGeneral(this);
        field_27151_l.registerScrollButtons(controlList, 1, 1);
        field_27150_m = new GuiSlotStatsItem(this);
        field_27150_m.registerScrollButtons(controlList, 1, 1);
        field_27157_n = new GuiSlotStatsBlock(this);
        field_27157_n.registerScrollButtons(controlList, 1, 1);
        field_27155_p = field_27151_l;
        func_27130_k();
    }

    public void func_27130_k()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        controlList.add(new GuiButton(0, width / 2 + 4, height - 28, 150, 20, stringtranslate.translateKey("gui.done")));
        controlList.add(new GuiButton(1, width / 2 - 154, height - 52, 100, 20, stringtranslate.translateKey("stat.generalButton")));
        GuiButton guibutton;
        controlList.add(guibutton = new GuiButton(2, width / 2 - 46, height - 52, 100, 20, stringtranslate.translateKey("stat.blocksButton")));
        GuiButton guibutton1;
        controlList.add(guibutton1 = new GuiButton(3, width / 2 + 62, height - 52, 100, 20, stringtranslate.translateKey("stat.itemsButton")));
        if(field_27157_n.getSize() == 0)
        {
            guibutton.enabled = false;
        }
        if(field_27150_m.getSize() == 0)
        {
            guibutton1.enabled = false;
        }
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 0)
        {
            mc.displayGuiScreen(field_27152_a);
        } else
        if(guibutton.id == 1)
        {
            field_27155_p = field_27151_l;
        } else
        if(guibutton.id == 3)
        {
            field_27155_p = field_27150_m;
        } else
        if(guibutton.id == 2)
        {
            field_27155_p = field_27157_n;
        } else
        {
            field_27155_p.actionPerformed(guibutton);
        }
    }

    public void drawScreen(int i, int j, float f)
    {
        field_27155_p.drawScreen(i, j, f);
        drawCenteredString(fontRenderer, field_27154_i, width / 2, 20, 0xffffff);
        super.drawScreen(i, j, f);
    }

    private void func_27138_c(int i, int j, int k)
    {
        func_27147_a(i + 1, j + 1);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        field_27153_j.drawItemIntoGui(fontRenderer, mc.renderEngine, k, 0, Item.itemsList[k].getIconFromDamage(0), i + 2, j + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
    }

    private void func_27147_a(int i, int j)
    {
        func_27136_c(i, j, 0, 0);
    }

    private void func_27136_c(int i, int j, int k, int l)
    {
        int i1 = mc.renderEngine.getTexture("/gui/slot.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i1);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(i + 0, j + 18, zLevel, (float)(k + 0) * 0.0078125F, (float)(l + 18) * 0.0078125F);
        tessellator.addVertexWithUV(i + 18, j + 18, zLevel, (float)(k + 18) * 0.0078125F, (float)(l + 18) * 0.0078125F);
        tessellator.addVertexWithUV(i + 18, j + 0, zLevel, (float)(k + 18) * 0.0078125F, (float)(l + 0) * 0.0078125F);
        tessellator.addVertexWithUV(i + 0, j + 0, zLevel, (float)(k + 0) * 0.0078125F, (float)(l + 0) * 0.0078125F);
        tessellator.draw();
    }

    static Minecraft func_27141_a(GuiStats guistats)
    {
        return guistats.mc;
    }

    static FontRenderer func_27145_b(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static StatFileWriter func_27142_c(GuiStats guistats)
    {
        return guistats.field_27156_o;
    }

    static FontRenderer func_27140_d(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27146_e(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static Minecraft func_27143_f(GuiStats guistats)
    {
        return guistats.mc;
    }

    static void func_27128_a(GuiStats guistats, int i, int j, int k, int l)
    {
        guistats.func_27136_c(i, j, k, l);
    }

    static Minecraft func_27149_g(GuiStats guistats)
    {
        return guistats.mc;
    }

    static FontRenderer func_27133_h(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27137_i(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27132_j(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27134_k(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27139_l(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static void func_27129_a(GuiStats guistats, int i, int j, int k, int l, int i1, int j1)
    {
        guistats.drawGradientRect(i, j, k, l, i1, j1);
    }

    static FontRenderer func_27144_m(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static FontRenderer func_27127_n(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static void func_27135_b(GuiStats guistats, int i, int j, int k, int l, int i1, int j1)
    {
        guistats.drawGradientRect(i, j, k, l, i1, j1);
    }

    static FontRenderer func_27131_o(GuiStats guistats)
    {
        return guistats.fontRenderer;
    }

    static void func_27148_a(GuiStats guistats, int i, int j, int k)
    {
        guistats.func_27138_c(i, j, k);
    }

    private static RenderItem field_27153_j = new RenderItem();
    protected GuiScreen field_27152_a;
    protected String field_27154_i;
    private GuiSlotStatsGeneral field_27151_l;
    private GuiSlotStatsItem field_27150_m;
    private GuiSlotStatsBlock field_27157_n;
    private StatFileWriter field_27156_o;
    private GuiSlot field_27155_p;

    
    //Spout Start
	@Override
	public void handleKeyboardInput() {
		super.handleKeyboardInput();
		Minecraft.handleKeyPress(Keyboard.getEventKey(), Keyboard.getEventKeyState(), ScreenType.STATISTICS_MENU);
	}
	//Spout End
}
