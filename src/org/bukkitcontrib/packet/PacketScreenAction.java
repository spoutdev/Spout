package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.event.screen.ScreenCloseEvent;
import org.bukkitcontrib.gui.InGameScreen;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;

public class PacketScreenAction implements BukkitContribPacket{
	protected byte action = -1;
	protected byte accepted = 1;
	@Override
	public int getNumBytes() {
		return 2;
	}
	
	public PacketScreenAction() { }
	
	public PacketScreenAction(ScreenAction action) {
		this.action = (byte) action.getId();
		this.accepted = 2;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		action = input.readByte();
		accepted = input.readByte();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeByte(action);
		output.writeByte(accepted);
	}

	@Override
	public void run(int playerId) {
		ContribPlayer player = BukkitContrib.getPlayerFromId(playerId);
		if (player != null && action == ScreenAction.ScreenClose.getId()) {
			if (player.getMainScreen().getActivePopup() != null) {
				ScreenCloseEvent event = new ScreenCloseEvent(player.getMainScreen().getActivePopup());
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					accepted = 0;
				}
				else {
					((InGameScreen)player.getMainScreen()).clearPopup();
					accepted = 1;
				}
				((ContribCraftPlayer)player).sendPacket(this); //return the response
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketScreenAction;
	}

}
