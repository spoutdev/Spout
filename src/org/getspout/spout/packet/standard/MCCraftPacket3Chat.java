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

import net.minecraft.server.Packet3Chat;

import org.getspout.spoutapi.packet.standard.MCPacket3Chat;

public class MCCraftPacket3Chat extends MCCraftPacket implements MCPacket3Chat {

	public Packet3Chat getPacket() {
		return (Packet3Chat)packet;
	}
	
	public String getMessage() {
		return getPacket().message;
	}

	public void setMessage(String message) {
		getPacket().message = message;
	}

}
