package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.gui.ScreenType;
import net.minecraft.src.*;

public class PacketKeyPress implements BukkitContribPacket{
	public boolean pressDown;
	public byte key;
	public byte settingKeys[] = new byte[10];
	public int screenType = 0;
	public PacketKeyPress(){
	}

	public PacketKeyPress(byte key, boolean pressDown, MovementInputFromOptions input)
	{
		this.key = key;
		this.pressDown = pressDown;
		this.settingKeys[0] = (byte)input.gameSettings.keyBindForward.keyCode;
		this.settingKeys[1] = (byte)input.gameSettings.keyBindLeft.keyCode;
		this.settingKeys[2] = (byte)input.gameSettings.keyBindBack.keyCode;
		this.settingKeys[3] = (byte)input.gameSettings.keyBindRight.keyCode;
		this.settingKeys[4] = (byte)input.gameSettings.keyBindJump.keyCode;
		this.settingKeys[5] = (byte)input.gameSettings.keyBindInventory.keyCode;
		this.settingKeys[6] = (byte)input.gameSettings.keyBindDrop.keyCode;
		this.settingKeys[7] = (byte)input.gameSettings.keyBindChat.keyCode;
		this.settingKeys[8] = (byte)input.gameSettings.keyBindToggleFog.keyCode;
		this.settingKeys[9] = (byte)input.gameSettings.keyBindSneak.keyCode;
	}

	public PacketKeyPress(byte key, boolean pressDown,
			MovementInputFromOptions input, ScreenType type) {
		this.key = key;
		this.pressDown = pressDown;
		this.settingKeys[0] = (byte)input.gameSettings.keyBindForward.keyCode;
		this.settingKeys[1] = (byte)input.gameSettings.keyBindLeft.keyCode;
		this.settingKeys[2] = (byte)input.gameSettings.keyBindBack.keyCode;
		this.settingKeys[3] = (byte)input.gameSettings.keyBindRight.keyCode;
		this.settingKeys[4] = (byte)input.gameSettings.keyBindJump.keyCode;
		this.settingKeys[5] = (byte)input.gameSettings.keyBindInventory.keyCode;
		this.settingKeys[6] = (byte)input.gameSettings.keyBindDrop.keyCode;
		this.settingKeys[7] = (byte)input.gameSettings.keyBindChat.keyCode;
		this.settingKeys[8] = (byte)input.gameSettings.keyBindToggleFog.keyCode;
		this.settingKeys[9] = (byte)input.gameSettings.keyBindSneak.keyCode;
		this.screenType = type.getCode();
	}

	public void readData(DataInputStream datainputstream) throws IOException {
		this.key = datainputstream.readByte();
		this.pressDown = datainputstream.readBoolean();
		this.screenType = datainputstream.readInt();
		for (int i = 0; i < 10; i++) {
				this.settingKeys[i] = datainputstream.readByte();
		}
	}

	public void writeData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeByte(this.key);
		dataoutputstream.writeBoolean(this.pressDown);
		dataoutputstream.writeInt(this.screenType);
		for (int i = 0; i < 10; i++) {
				dataoutputstream.writeByte(this.settingKeys[i]);
		}
	}

	public void run(int id) {

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
