package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.event.input.RenderDistance;
import org.bukkitcontrib.event.input.RenderDistanceChangeEvent;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class PacketRenderDistance implements BukkitContribPacket{
	protected byte view = -1;
	protected byte max = -1;
	protected byte min = -1;
	public PacketRenderDistance() {
		
	}
	
	public PacketRenderDistance(boolean resetMax, boolean resetMin) {
		if (resetMax)
			max = -2;
		if (resetMin)
			min = -2;
	}
	
	public PacketRenderDistance(RenderDistance view, RenderDistance max, RenderDistance min) {
		if (view != null)
			this.view = (byte) view.getValue();
		if (max != null)
			this.max = (byte) max.getValue();
		if (min != null)
			this.min = (byte) min.getValue();
	}

	@Override
	public int getNumBytes() {
		return 3;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		view = input.readByte();
		max = input.readByte();
		min = input.readByte();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.write(view);
		output.write(max);
		output.write(min);
	}

	@Override
	public void run(int PlayerId) {
		RenderDistance currentRender = RenderDistance.getRenderDistanceFromValue(view);
		if (currentRender != null) {
			ContribCraftPlayer ccp = (ContribCraftPlayer)BukkitContrib.getPlayerFromId(PlayerId);
			if (ccp != null) {
				RenderDistanceChangeEvent event = new RenderDistanceChangeEvent(ccp, currentRender);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					ccp.currentRender = currentRender;
				}
				else {
					ccp.sendPacket(new PacketRenderDistance(ccp.currentRender, null, null));
				}
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketRenderDistance;
	}

}
