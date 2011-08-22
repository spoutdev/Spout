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
package org.getspout.spout.packet.standard;

import net.minecraft.server.Packet17;

import org.getspout.spoutapi.packet.standard.MCPacket17;

public class MCCraftPacket17 extends MCCraftPacket implements MCPacket17 {

	public Packet17 getPacket() {
		return (Packet17)packet;
	}
	
	public int getBed() {
		return getPacket().b;
	}

	public int getBlockX() {
		return getPacket().c;
	}

	public int getBlockY() {
		return getPacket().d;
	}

	public int getBlockZ() {
		return getPacket().e;
	}

	public int getEntityId() {
		return getPacket().a;
	}

	public void setBed(int bed) {
		getPacket().b = bed;
	}

	public void setBlockX(int x) {
		getPacket().c = x;
	}

	public void setBlockY(int y) {
		getPacket().d = y;
	}

	public void setBlockZ(int z) {
		getPacket().e = z;
	}

	public void setEntityId(int id) {
		getPacket().a = id;
	}

}
