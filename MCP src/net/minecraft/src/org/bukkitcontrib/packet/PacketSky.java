package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketSky implements BukkitContribPacket{
	private int cloudY, stars, sunPercent, moonPercent;
	String sun = "";
	String moon = "";
	public PacketSky() {
		
	}
	
	public PacketSky(int cloudY, int stars, int sunPercent, int moonPercent) {
		this.cloudY = cloudY;
		this.stars = stars;
		this.sunPercent = sunPercent;
		this.moonPercent = moonPercent;
	}
	
	public PacketSky(String sunUrl, String moonUrl) {
		this.cloudY = 0;
		this.stars = 0;
		this.sunPercent = 0;
		this.moonPercent = 0;
		this.sun = sunUrl;
		this.moon = moonUrl;
	}

	@Override
	public int getNumBytes() {
		return 16 + PacketUtil.getNumBytes(sun) + PacketUtil.getNumBytes(moon);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		cloudY = input.readInt();
		stars = input.readInt();
		sunPercent = input.readInt();
		moonPercent = input.readInt();
		sun = PacketUtil.readString(input, 256);
		moon = PacketUtil.readString(input, 256);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(cloudY);
		output.writeInt(stars);
		output.writeInt(sunPercent);
		output.writeInt(moonPercent);
		PacketUtil.writeString(output, sun);
		PacketUtil.writeString(output, moon);
	}

	@Override
	public void run(int PlayerId) {
		if (cloudY != 0) {
			BukkitContrib.getSkyManager().setCloudHeight(cloudY);
		}
		if (stars != 0) {
			BukkitContrib.getSkyManager().setStarFrequency(stars);
		}
		if (sunPercent != 0) {
			BukkitContrib.getSkyManager().setSunSizePercent(sunPercent);
		}
		if (moonPercent != 0) {
			BukkitContrib.getSkyManager().setMoonSizePercent(moonPercent);
		}
		if (sun != null) {
			if (sun.equals("[reset]")) {
				BukkitContrib.getSkyManager().setSunTextureUrl(null);
			}
			else if (sun.length() > 5) {
				BukkitContrib.getSkyManager().setSunTextureUrl(sun);
			}
		}
		if (moon != null) {
			if (moon.equals("[reset]")) {
				BukkitContrib.getSkyManager().setMoonTextureUrl(null);
			}
			else if (moon.length() > 5) {
				BukkitContrib.getSkyManager().setMoonTextureUrl(moon);
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketSky;
	}

}
