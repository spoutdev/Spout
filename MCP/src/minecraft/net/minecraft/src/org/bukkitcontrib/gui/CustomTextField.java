package org.getspout.Spout.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.getspout.Spout.packet.*;
import java.util.UUID;

public class CustomTextField extends GuiButton {
	protected Screen screen;
	protected TextField field;
	private int count = 0;
	private boolean focus = false;
	public CustomTextField(Screen screen, TextField field) {
		super(0, 0, 0, 0, 0, null);
		this.screen = screen;
		this.field = field;
	}
	
	public void textboxKeyTyped(char key, int keyId) {
		try {
			if(field.isEnabled() && this.focus) {
				String old = field.getText();
				if(key == 22) {
					String clipboard = GuiScreen.getClipboardString();
					if(clipboard == null) {
						clipboard = "";
					}

					int max = 32 - field.getText().length();
					if(max > clipboard.length()) {
						max = clipboard.length();
					}

					if(max > 0) {
						field.setText(field.getText() + clipboard.substring(0, max));
					}
				}
				
				if (keyId == Keyboard.KEY_RIGHT && field.getCursorPosition() < field.getText().length()) {
					field.setCursorPosition(field.getCursorPosition() + 1);
				}
				else if (keyId == Keyboard.KEY_LEFT && field.getCursorPosition() > 0) {
					field.setCursorPosition(field.getCursorPosition() - 1);
				}
				else if (keyId == Keyboard.KEY_DELETE && field.getCursorPosition() > 0 && field.getCursorPosition() < field.getText().length()) {
					field.setText(field.getText().substring(0, field.getCursorPosition()) + field.getText().substring(field.getCursorPosition() + 1));
				}

				if(keyId == Keyboard.KEY_BACK && field.getText().length() > 0 && field.getCursorPosition() > 0) {
					field.setText(field.getText().substring(0, field.getText().length() - 1));
					field.setCursorPosition(field.getCursorPosition() - 1);
				}

				if(ChatAllowedCharacters.allowedCharacters.indexOf(key) > -1 && (field.getText().length() < field.getMaximumCharacters() || field.getMaximumCharacters() == 0)) {
					String newText = "";
					if (field.getCursorPosition() > 0) {
						newText += field.getText().substring(0, field.getCursorPosition());
					}
					newText += key;
					if (field.getCursorPosition() < field.getText().length()) {
						newText += field.getText().substring(field.getCursorPosition());
					}
					field.setText(newText);
					field.setCursorPosition(field.getCursorPosition() + 1);
				}
				if (!old.equals(field.getText())) {
					((EntityClientPlayerMP)BukkitContrib.getGameInstance().thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketControlAction(screen, field, field.getText(), field.getCursorPosition())));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft game, int mouseX, int mouseY) {
		this.setFocused(field.isEnabled() && mouseX >= field.getX() && mouseX < field.getX() + field.getWidth() && mouseY >= field.getY() && mouseY < field.getY() + field.getHeight());
		return isFocused();
	}
	
	public boolean isFocused() {
		return focus;
	}
	
	public void setFocused(boolean focus) {
		if(focus && !this.focus) {
			field.setCursorPosition(field.getText().length());
		}

		this.focus = focus;
	}
	
	@Override
	public void drawButton(Minecraft game, int mouseX, int mouseY) {
		this.drawRect(field.getX() - 1, field.getY() - 1, field.getX() + field.getWidth() + 1, field.getY() + field.getHeight() + 1, field.getBorderColor());
		this.drawRect(field.getX(), field.getY(), field.getX() + field.getWidth(), field.getY() + field.getHeight(), field.getFieldColor());
		if(field.isEnabled()) {
			count++;
			boolean showCursor = this.focus && count % 40 < 15;
			String text = field.getText();
			if (field.getCursorPosition() < 0) field.setCursorPosition(0);
			if (field.getCursorPosition() > text.length()) field.setCursorPosition(text.length());
			if (showCursor) {
				text = "";
				if (field.getCursorPosition() > 0) {
					text += field.getText().substring(0, field.getCursorPosition());
				}
				text += "_";
				if (field.getCursorPosition() < field.getText().length()) {
					text += field.getText().substring(field.getCursorPosition());
				}
			}
			this.drawString(game.fontRenderer, text, field.getX() + 4, field.getY() + (field.getHeight() - 8) / 2, field.getColor());
		} else {
			this.drawString(game.fontRenderer, field.getText(), field.getX() + 4, field.getY() + (field.getHeight() - 8) / 2, field.getDisabledColor());
		}

	}

	public TextField getWidget() {
		return field;
	}
	
	public UUID getId() {
		return field.getId();
	}
	
	public boolean isEqual(Widget widget) {
		return widget.getId().equals(field.getId());
	}
	
	public void updateWidget(TextField widget) {
		this.field = widget;
		if (field.getCursorPosition() < 0) field.setCursorPosition(0);
		if (field.getCursorPosition() > field.getText().length()) field.setCursorPosition(field.getText().length());
	}
}