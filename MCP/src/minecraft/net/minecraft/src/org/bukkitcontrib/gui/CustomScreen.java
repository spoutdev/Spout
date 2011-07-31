package org.bukkitcontrib.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.bukkitcontrib.packet.*;
import net.minecraft.src.*;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class CustomScreen extends GuiScreen {
	protected PopupScreen screen;
	public boolean waiting = false;
	public CustomScreen(PopupScreen screen) {
		update(screen);
		this.setWorldAndResolution(BukkitContrib.getGameInstance(), screen.getWidth(), screen.getHeight());
	}
	
	public void update(PopupScreen screen) {
		this.screen = screen;
	}
	
	public void testScreenClose() {
		if (waiting) {
			return;
		}	
		if (this.mc.thePlayer instanceof EntityClientPlayerMP) {
			waiting = true;
			((EntityClientPlayerMP)this.mc.thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketScreenAction(ScreenAction.ScreenClose)));
		}
	}
	
	public void closeScreen() {
		if (!waiting){
			testScreenClose();
			return;
		}
		this.mc.displayGuiScreen(null);
		this.mc.setIngameFocus();
	}
	
	public void failedCloseScreen() {
		waiting = false;
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if (button instanceof CustomGuiButton){
			((EntityClientPlayerMP)this.mc.thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketControlAction(screen, ((CustomGuiButton)button).getWidget(), 1)));
		}
		else if (button instanceof CustomGuiSlider) {
			//This fires before the new position is set, so no good
		}	
	}
	
	@Override
	public void handleKeyboardInput() {
		boolean handled = false;
		//Spout start
		Minecraft.handleKeyPress(Keyboard.getEventKey(), Keyboard.getEventKeyState(), ScreenType.CUSTOM_SCREEN);
		//Spout end
		if(Keyboard.getEventKeyState()) {
            
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				handled = true;
				testScreenClose();
			}
			else {
				for (GuiButton control : getControlList()) {
					if (control instanceof CustomTextField) {
						if (((CustomTextField)control).isFocused()) {
							((CustomTextField)control).textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
							handled = true;
							break;
						}
					}
				}
			}
		}
		if (!handled) {
			super.handleKeyboardInput();
		}
	}
	
	public ArrayList<GuiButton> getControlList() {
		return (ArrayList<GuiButton>)this.controlList;
	}
	
	public void drawScreen(int x, int y, float z) {
		if (!screen.isTransparent()) {
			this.drawDefaultBackground();
		}
		for (Widget widget : screen.getAttachedWidgets()) {
			if (widget instanceof GenericButton) {
				((GenericButton)widget).setup(x, y);
			}
			else if (widget instanceof GenericTextField) {
				((GenericTextField)widget).setup(x, y);
			}
			else if (widget instanceof GenericSlider) {
				((GenericSlider)widget).setup(x, y);
			}
		}
		screen.render();
	}
}