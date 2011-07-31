package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketMusicChange implements BukkitContribPacket{
	protected int id;
	protected int volumePercent;
	boolean cancel = false;
	
	public PacketMusicChange() {
		
	}
	
	public PacketMusicChange(int music, int volumePercent) {
		this.id = music;
		this.volumePercent = volumePercent;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public int getNumBytes() {
		return 9;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		id = input.readInt();
		volumePercent = input.readInt();
		cancel =  input.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(id);
		output.writeInt(volumePercent);
		output.writeBoolean(cancel);
	}

	@Override
	public void run(int playerId) {
		if (cancel)
			BukkitContrib.getGameInstance().sndManager.cancelled = true;
		else
			BukkitContrib.getGameInstance().sndManager.allowed = true;
	}		

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketMusicChange;
	}

}
