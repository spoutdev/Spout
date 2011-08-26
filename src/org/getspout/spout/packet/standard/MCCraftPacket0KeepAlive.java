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

import net.minecraft.server.Packet0KeepAlive;

import org.getspout.spoutapi.packet.standard.MCPacket0KeepAlive;

public class MCCraftPacket0KeepAlive extends MCCraftPacket implements MCPacket0KeepAlive {

	public Packet0KeepAlive getPacket() {
		return (Packet0KeepAlive) packet;
	}
	
}
