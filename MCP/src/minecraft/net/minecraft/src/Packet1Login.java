package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;
//BukkitContrib Start
import org.getspout.gui.*;
//BukkitContrib End

public class Packet1Login extends Packet {

	public int protocolVersion;
	public String username;
	public long mapSeed;
	public byte dimension;


	public Packet1Login() {}

	public Packet1Login(String var1, int var2) {
		this.username = var1;
		this.protocolVersion = var2;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.protocolVersion = var1.readInt();
		this.username = readString(var1, 16);
		this.mapSeed = var1.readLong();
		this.dimension = var1.readByte();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeInt(this.protocolVersion);
		writeString(this.username, var1);
		var1.writeLong(this.mapSeed);
		var1.writeByte(this.dimension);
	}

	public void processPacket(NetHandler var1) {
		//BukkitContrib Start
		BukkitContrib.resetBukkitContrib();
		BukkitContrib.dataMining.onLogin();
		//BukkitContrib End
		var1.handleLogin(this);
	}

	public int getPacketSize() {
		return 4 + this.username.length() + 4 + 5;
	}
}
