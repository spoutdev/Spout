package org.bukkitcontrib.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.bukkitcontrib.packet.CustomPacket;
import org.bukkitcontrib.packet.PacketScreenAction;
import org.bukkitcontrib.packet.ScreenAction;
import net.minecraft.src.*;
import java.util.ArrayList;

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
		System.out.println("button pressed!");
	}
	
	@Override
	public void handleKeyboardInput() {
		boolean handled = false;
		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				handled = true;
				testScreenClose();
			}
		}
		if (!handled) {
			super.handleKeyboardInput();
		}
	}
	
	public ArrayList<GuiButton> getControlList() {
		return (ArrayList<GuiButton>)this.controlList;
	}
	
	public void drawScreen(int var1, int var2, float var3) {
		if (screen.isTransparent()) {
			this.drawDefaultBackground();
		}
		screen.render();
		super.drawScreen(var1, var2, var3);
	}
}