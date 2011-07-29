package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.src.*;

import org.bukkitcontrib.gui.*;

public class PacketWidget implements BukkitContribPacket {
	protected Widget widget;
	protected UUID screen;
	public PacketWidget() {

	}
	
	public PacketWidget(Widget widget, UUID screen) {
		this.widget = widget;
		this.screen = screen;
	}

	@Override
	public int getNumBytes() {
		return widget.getNumBytes() + 20;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		int id = input.readInt();
		long msb = input.readLong();
		long lsb = input.readLong();
		screen = new UUID(msb, lsb);
		WidgetType widgetType = WidgetType.getWidgetFromId(id);
		if (widgetType != null) {
			try {
				widget = widgetType.getWidgetClass().newInstance();
				widget.readData(input);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(widget.getType().getId());
		output.writeLong(screen.getMostSignificantBits());
		output.writeLong(screen.getLeastSignificantBits());
		widget.writeData(output);
	}

	@Override
	public void run(int playerId) {
		InGameScreen mainScreen = BukkitContrib.getMainScreen();
		PopupScreen popup = mainScreen.getActivePopup();
		//Determine if this is a popup screen and if we need to update it
		if (widget instanceof PopupScreen) {
			if (popup != null){
				if (widget.getId().equals(popup)) {
					if (BukkitContrib.getGameInstance().currentScreen instanceof CustomScreen) {
						((CustomScreen)BukkitContrib.getGameInstance().currentScreen).update((PopupScreen)widget);
					}
				}
			}
			else {
				mainScreen.attachPopupScreen((PopupScreen)widget);
			}
		}
		//Determine if this is a widget on the main screen
		else if (screen.equals(mainScreen.getId())) {
			if (mainScreen.containsWidget(widget)) {
				mainScreen.updateWidget(widget);
				widget.setScreen(mainScreen);
			}
			else {
				mainScreen.attachWidget(widget);
			}
		}
		//Determine if this is a widget on the popup screen
		else if (popup != null && screen.equals(popup.getId())) {
			if (popup.containsWidget(widget)) {
				popup.updateWidget(widget);
				widget.setScreen(popup);
			}
			else {
				popup.attachWidget(widget);
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketWidget;
	}

}
