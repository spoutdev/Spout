/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout;

import java.util.concurrent.LinkedBlockingDeque;

import org.getspout.spoutapi.packet.CompressablePacket;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PacketCompressionThread extends Thread{
	private static PacketCompressionThread instance = null;
	
	private static final int QUEUE_CAPACITY = 1024 * 10;
	private final LinkedBlockingDeque<QueuedPacket> queue = new LinkedBlockingDeque<QueuedPacket>(QUEUE_CAPACITY);
	
	private PacketCompressionThread() {
		
	}
	
	public static void startThread() {
		instance = new PacketCompressionThread();
		instance.start();
	}
	
	public static void endThread() {
		instance.interrupt();
		try {
			instance.join();
		} catch (InterruptedException ie) {
		}
		instance = null;
	}
	
	public static PacketCompressionThread getInstance() {
		return instance;
	}
	
	public static void add(CompressablePacket packet, SpoutPlayer player) {
		if (instance != null) {
			instance.queue.add(new QueuedPacket(player, packet));
		}
	}
	
	public void run() {
		while(!isInterrupted()) {
			try {
				QueuedPacket packet = queue.take();
				packet.packet.compress();
				packet.player.sendPacket(packet.packet);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class QueuedPacket {
		final CompressablePacket packet;
		final SpoutPlayer player;
		
		QueuedPacket(SpoutPlayer player, CompressablePacket packet) {
			this.player = player;
			this.packet = packet;
		}
	}

}
