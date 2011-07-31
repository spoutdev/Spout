package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;

import org.getspout.BukkitContrib;
import org.getspout.event.screen.ButtonClickEvent;
import org.getspout.event.screen.SliderDragEvent;
import org.getspout.event.screen.TextFieldChangeEvent;
import org.getspout.gui.Button;
import org.getspout.gui.Screen;
import org.getspout.gui.Slider;
import org.getspout.gui.TextField;
import org.getspout.gui.Widget;
import org.getspout.player.ContribPlayer;

public class PacketControlAction implements BukkitContribPacket{
	protected UUID screen;
	protected UUID widget;
	protected float state;
	protected String data = "";
	public PacketControlAction() {
		
	}
	
	public PacketControlAction(Screen screen, Widget widget, float state) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = state;
	}
	
	public PacketControlAction(Screen screen, Widget widget, String data) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = 0F;
		this.data = data;
	}

	@Override
	public int getNumBytes() {
		return 36 + PacketUtil.getNumBytes(data);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		long msb = input.readLong();
		long lsb = input.readLong();
		this.screen = new UUID(msb, lsb);
		msb = input.readLong();
		lsb = input.readLong();
		this.widget = new UUID(msb, lsb);
		this.state = input.readFloat();
		this.data = PacketUtil.readString(input);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeLong(screen.getMostSignificantBits());
		output.writeLong(screen.getLeastSignificantBits());
		output.writeLong(widget.getMostSignificantBits());
		output.writeLong(widget.getLeastSignificantBits());
		output.writeFloat(state);
		PacketUtil.writeString(output, data);
	}

	@Override
	public void run(int playerId) {
		ContribPlayer player = BukkitContrib.getPlayerFromId(playerId);
		if (player != null) {
			Screen screen = null;
			if (player.getMainScreen().getId().equals(this.screen)) {
				screen = player.getMainScreen();
			}
			if (player.getMainScreen().getActivePopup() != null && player.getMainScreen().getActivePopup().getId().equals(this.screen)) {
				screen = player.getMainScreen().getActivePopup();
			}
			if (screen != null) {
				Widget control = screen.getWidget(widget);
				if (control != null) {
					if (control instanceof Button) {
						ButtonClickEvent event = new ButtonClickEvent(player, screen, (Button)control);
						Bukkit.getServer().getPluginManager().callEvent(event);
					}
					else if (control instanceof Slider) {
						SliderDragEvent event = new SliderDragEvent(player, screen, (Slider)control, state);
						Bukkit.getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							((Slider)control).setSliderPosition(event.getOldPosition());
							control.setDirty(true);
						}
						else if (event.getNewPosition() != state) {
							((Slider)control).setSliderPosition(event.getNewPosition());
							control.setDirty(true);
						}
						else {
							((Slider)control).setSliderPosition(event.getNewPosition());
						}
					}
					else if (control instanceof TextField) {
						TextFieldChangeEvent event = new TextFieldChangeEvent(player, screen, (TextField)control, data);
						Bukkit.getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							((TextField)control).setText(event.getOldText());
							control.setDirty(true);
						}
						else if (!event.getNewText().equals(data)) {
							((TextField)control).setText(event.getNewText());
							control.setDirty(true);
						}
						else {
							((TextField)control).setText(event.getNewText());
							((TextField)control).setCursorPosition((int)state);
						}
					}
				}
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketControlAction;
	}

}
