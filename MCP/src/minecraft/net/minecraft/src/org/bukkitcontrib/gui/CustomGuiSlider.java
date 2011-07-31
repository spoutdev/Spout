package org.getspout.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;
import net.minecraft.client.Minecraft;
import org.getspout.packet.*;

public class CustomGuiSlider extends GuiSlider {
	protected Screen screen;
	protected Slider slider;
	public CustomGuiSlider(Screen screen, Slider slider) {
		super(0, 0, 0, null, null, 0);
		this.screen = screen;
		this.slider = slider;
	}
	
	@Override
	protected void mouseDragged(Minecraft game, int mouseX, int mouseY) {
		if(slider.isVisible()) {
			if(this.dragging) {
				slider.setSliderPosition((float)(mouseX - (slider.getX()+ 4)) / (float)(slider.getWidth() - 8));
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(slider.getX()+ (int)(slider.getSliderPosition() * (float)(slider.getWidth() - 8)), slider.getY(), 0, 66, 4, slider.getHeight());
			this.drawTexturedModalRect(slider.getX()+ (int)(slider.getSliderPosition() * (float)(slider.getWidth() - 8)) + 4, slider.getY(), 196, 66, 4, slider.getHeight());
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft game, int mouseX, int mouseY) {
		if(mousePressedWidget(game, mouseX, mouseY)) {
			slider.setSliderPosition((float)(mouseX - (slider.getX() + 4)) / (float)(slider.getWidth() - 8));
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void drawButton(Minecraft game, int mouseX, int mouseY) {
		if(slider.isVisible()) {
			FontRenderer font = game.fontRenderer;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, game.renderEngine.getTexture("/gui/gui.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean hovering = mouseX >= slider.getX() && mouseY >= slider.getY() && mouseX < slider.getX() + slider.getWidth() && mouseY < slider.getY() + slider.getHeight();
			int hoverState = this.getHoverState(hovering);
			this.drawTexturedModalRect(slider.getX(), slider.getY(), 0, 46 + hoverState * 20, slider.getWidth() / 2, slider.getHeight());
			this.drawTexturedModalRect(slider.getX() + slider.getWidth() / 2, slider.getY(), 200 - slider.getWidth() / 2, 46 + hoverState * 20, slider.getWidth() / 2, slider.getHeight());
			this.mouseDragged(game, mouseX, mouseY);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		super.mouseReleased(mouseX, mouseY);
		((EntityClientPlayerMP)BukkitContrib.getGameInstance().thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketControlAction(screen, slider, slider.getSliderPosition())));
	}
	
	public boolean mousePressedWidget(Minecraft game, int mouseX, int mouseY) {
		return slider.isEnabled() && mouseX >= slider.getX() && mouseY >= slider.getY() && mouseX < slider.getX() + slider.getWidth() && mouseY < slider.getY() + slider.getHeight();
	}
	
	public Slider getWidget() {
		return slider;
	}
	
	public boolean equals(Widget widget) {
		return widget.getId().equals(slider.getId());
	}
	
	public void updateWidget(Slider widget) {
		this.slider = widget;
	}
}