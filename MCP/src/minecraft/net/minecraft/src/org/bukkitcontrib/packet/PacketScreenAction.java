package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.*;

import org.bukkitcontrib.gui.CustomScreen;

public class PacketScreenAction implements BukkitContribPacket{
	protected byte action = -1;
	protected byte accepted = 1;
	
	public PacketScreenAction() {
	
	}
	
	public PacketScreenAction(ScreenAction action) {
		this.action = (byte)action.getId();
	}
	
	@Override
	public int getNumBytes() {
		return 2;
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
		if (action == ScreenAction.ScreenClose.getId()) {
			if (BukkitContrib.getMainScreen().getActivePopup() != null) {
				if (BukkitContrib.getGameInstance().currentScreen != null && BukkitContrib.getGameInstance().currentScreen instanceof CustomScreen) {
					if (accepted == 1) {
						((CustomScreen)BukkitContrib.getGameInstance().currentScreen).closeScreen();
					}
					else if (accepted == 2) {
						((CustomScreen)BukkitContrib.getGameInstance().currentScreen).waiting = true;
						((CustomScreen)BukkitContrib.getGameInstance().currentScreen).closeScreen();
					}
					else {
						((CustomScreen)BukkitContrib.getGameInstance().currentScreen).failedCloseScreen();
					}
				}
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketScreenAction;
	}

}
