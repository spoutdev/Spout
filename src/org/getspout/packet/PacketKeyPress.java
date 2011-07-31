package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.getspout.BukkitContrib;
import org.getspout.event.input.KeyPressedEvent;
import org.getspout.event.input.KeyReleasedEvent;
import org.getspout.keyboard.Keyboard;
import org.getspout.keyboard.SimpleKeyboardManager;
import org.getspout.player.ContribCraftPlayer;

public class PacketKeyPress implements BukkitContribPacket{
	public boolean pressDown;
	public byte key;
	public byte settingKeys[] = new byte[10];
	public PacketKeyPress(){
	}

	public PacketKeyPress(byte key, boolean pressDown) {
		this.key = key;
		this.pressDown = pressDown;
	}

	public void readData(DataInputStream datainputstream) throws IOException {
		this.key = datainputstream.readByte();
		this.pressDown = datainputstream.readBoolean();
		for (int i = 0; i < 10; i++) {
			this.settingKeys[i] = datainputstream.readByte();
		}
	}

	public void writeData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeByte(this.key);
		dataoutputstream.writeBoolean(this.pressDown);
		for (int i = 0; i < 10; i++) {
			dataoutputstream.writeByte(this.settingKeys[i]);
		}
	}

	public void run(int id) {
		ContribCraftPlayer ccp = (ContribCraftPlayer)BukkitContrib.getPlayerFromId(id);
		if (ccp != null) {
			ccp.updateKeys(settingKeys);
			Keyboard pressed = Keyboard.getKey(this.key);
			SimpleKeyboardManager manager = (SimpleKeyboardManager)BukkitContrib.getKeyboardManager();
			if (pressDown) {
				manager.onPreKeyPress(pressed, ccp);
				Bukkit.getServer().getPluginManager().callEvent(new KeyPressedEvent(this.key, ccp));
				manager.onPostKeyPress(pressed, ccp);
			}
			else {
				manager.onPreKeyRelease(pressed, ccp);
				Bukkit.getServer().getPluginManager().callEvent(new KeyReleasedEvent(this.key, ccp));
				manager.onPostKeyPress(pressed, ccp);
			}
		}
	}

	public int getNumBytes()
	{
		return 1 + 1 + 10;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketKeyPress;
	}
}
