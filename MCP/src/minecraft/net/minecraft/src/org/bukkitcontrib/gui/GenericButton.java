package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.GuiButton;
import net.minecraft.src.BukkitContrib;
import org.bukkitcontrib.packet.PacketUtil;

public class GenericButton extends GenericControl implements Button {

	protected GenericLabel label = new GenericLabel();
	protected String disabledText = "";
	private GuiButton button = null;
	public GenericButton() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + label.getNumBytes() + PacketUtil.getNumBytes(getDisabledText());
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		label.readData(input);
		setDisabledText(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		label.writeData(output);
		PacketUtil.writeString(output, getDisabledText());
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public Label setText(String text) {
		label.setText(text);
		return this;
	}

	@Override
	public boolean isCentered() {
		return label.isCentered();
	}

	@Override
	public Label setCentered(boolean center) {
		label.setCentered(center);
		return this;
	}

	@Override
	public int getHexColor() {
		return label.getHexColor();
	}

	@Override
	public Label setHexColor(int hex) {
		label.setHexColor(hex);
		return this;
	}

	@Override
	public String getDisabledText() {
		return disabledText;
	}

	@Override
	public Button setDisabledText(String text) {
		disabledText = text;
		return this;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.Button;
	}
	
	@Override
	public void render() {
		if (button == null) {
			button = new GuiButton(hashCode(), getX(), getY(), getWidth(), getHeight(), getText());
			if (button.enabled != isEnabled()){
				button.enabled = isEnabled();
				if (!isEnabled()) {
					button.displayString = getDisabledText();
				}
				else {
					button.displayString = getText();
				}
			}
			if (BukkitContrib.getGameInstance().currentScreen instanceof CustomScreen) {
				CustomScreen popup = (CustomScreen)BukkitContrib.getGameInstance().currentScreen;
				int index = popup.getControlList().indexOf(button);
				if (index > -1) {
					popup.getControlList().remove(index);
					popup.getControlList().add(index, button);
				}
				else {
					popup.getControlList().add(button);
				}
			}
		}
	}

}
