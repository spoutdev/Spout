//BukkitContrib
package net.minecraft.src;

import java.io.*;

public class Packet195KeyPress extends Packet
{

    public Packet195KeyPress()
    {
    }

    public Packet195KeyPress(int key, boolean pressDown, MovementInputFromOptions input)
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

    public void readPacketData(DataInputStream datainputstream) throws IOException
    {
		this.key = datainputstream.readInt();
        this.pressDown = datainputstream.readBoolean();
		for (int i = 0; i < 10; i++) {
        	this.settingKeys[i] = datainputstream.readByte();
		}
    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException
    {
		dataoutputstream.writeInt(this.key);
        dataoutputstream.writeBoolean(this.pressDown);
		for (int i = 0; i < 10; i++) {
			dataoutputstream.writeByte(this.settingKeys[i]);
		}
    }

    public void processPacket(NetHandler nethandler)
    {
        
    }

    public int getPacketSize()
    {
        return 4 + 1 + 10;
    }

    public boolean pressDown;
	public int key;
	public byte settingKeys[] = new byte[10];
	static 
    {
        addIdClassMapping(195, true, true, net.minecraft.src.Packet195KeyPress.class);
    }
}
