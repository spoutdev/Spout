package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;
//BukkitContrib Start
import net.minecraft.client.Minecraft;
import org.bukkitcontrib.packet.*;
//BukkitContrib End

public class Packet3Chat extends Packet {

	public String message;


	public Packet3Chat() {}

	public Packet3Chat(String var1) {
		if(var1.length() > 119) {
			var1 = var1.substring(0, 119);
		}

		this.message = var1;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.message = readString(var1, 119);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.message, var1);
	}

	public void processPacket(NetHandler nethandler) {
		//BukkitContrib Start
		boolean proc = false;
		if (!BukkitContrib.isEnabled() || BukkitContrib.getReloadPacket() != null) {
			String processed = BukkitContrib.colorToString(message);
			System.out.println(processed);
			if (processed.split("\\.").length == 3) {
				BukkitContrib.setVersion(processed);
				if (BukkitContrib.isEnabled()) {
					proc = true;
					System.out.println("BukkitContrib SP Enabled");
					((NetClientHandler)nethandler).addToSendQueue(new Packet3Chat("/" + BukkitContrib.getClientVersionString()));
					//Let BukkitContrib know we just reloaded
					if (BukkitContrib.getReloadPacket() != null) {
						((NetClientHandler)nethandler).addToSendQueue(new CustomPacket(BukkitContrib.getReloadPacket()));
						BukkitContrib.setReloadPacket(null);
					}
					//Also need to send the render distance
					Minecraft game = BukkitContrib.getGameInstance();
					if (game != null && BukkitContrib.getVersion() > 5) {
						final GameSettings settings = game.gameSettings;
						((NetClientHandler)nethandler).addToSendQueue(new CustomPacket(new PacketRenderDistance((byte)settings.renderDistance)));
					}
				}
			}
		}
		if (!proc) {
			//Normal message handling
			nethandler.handleChat(this);
		}
		//BukkitContrib End
	}

	public int getPacketSize() {
		return this.message.length();
	}
}
