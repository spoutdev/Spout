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
	public void run(int PlayerId) {
		if (BukkitContrib.mainScreen.containsWidget(widget)) {
			BukkitContrib.mainScreen.updateWidget(widget);
			//System.out.println(widget.getType() + " updated");
		}
		else {
			//System.out.println(widget.getType() + " added");
			BukkitContrib.mainScreen.attachWidget(widget);
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketWidget;
	}

}
