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

import java.net.Socket;
import java.util.Iterator;

import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.player.SpoutCraftPlayer;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.Packet6SpawnPosition;
import net.minecraft.server.WorldServer;

public class SpoutNetLoginHandler extends NetLoginHandler{

	public final MinecraftServer server;
	public SpoutNetLoginHandler(MinecraftServer minecraftserver, Socket socket,	String s) {
		super(minecraftserver, socket, s);
		this.server = minecraftserver;
	}
	
	@Override
	public void b(Packet1Login packet1login) {
		EntityPlayer entityplayer = this.server.serverConfigurationManager.attemptLogin(this, packet1login.name);
		if (entityplayer != null) {
			this.server.serverConfigurationManager.b(entityplayer);
			entityplayer.spawnIn(this.server.getWorldServer(entityplayer.dimension));
			entityplayer.itemInWorldManager.a((WorldServer) entityplayer.world);
			a.info(this.b() + " logged in with entity id " + entityplayer.id + " at (" + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
			WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);
			ChunkCoordinates chunkcoordinates = worldserver.getSpawn();

			entityplayer.itemInWorldManager.b(worldserver.getWorldData().getGameType());
			
			SpoutCraftPlayer.updateBukkitEntity(entityplayer);
			
			SpoutNetServerHandler netserverhandler = new SpoutNetServerHandler(this.server, this.networkManager, entityplayer);

			worldserver.getClass();
			int maxPlayers = this.server.serverConfigurationManager.getMaxPlayers();
			if (maxPlayers > 60) {
				maxPlayers = 60;
			}
			Packet1Login packet1login1 = new Packet1Login("", entityplayer.id, worldserver.getSeed(), entityplayer.itemInWorldManager.a(), (byte) worldserver.worldProvider.dimension, (byte) worldserver.difficulty, (byte) worldserver.height, (byte) maxPlayers);

			networkManager.queue(packet1login1);
			
			if (ConfigReader.authenticateSpoutcraft()) {
				Packet18ArmAnimation packet = new Packet18ArmAnimation();
				packet.a = -42;
				networkManager.queue(packet);
			}
			
			netserverhandler.sendPacket(new Packet6SpawnPosition(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z));
			this.server.serverConfigurationManager.a(entityplayer, worldserver);
			this.server.serverConfigurationManager.c(entityplayer);
			netserverhandler.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
			this.server.networkListenThread.a(netserverhandler);
			netserverhandler.sendPacket(new Packet4UpdateTime(worldserver.getTime()));
			@SuppressWarnings("rawtypes")
			Iterator iterator = entityplayer.getEffects().iterator();

			while (iterator.hasNext()) {
				MobEffect mobeffect = (MobEffect) iterator.next();

				netserverhandler.sendPacket(new Packet41MobEffect(entityplayer.id, mobeffect));
			}

			entityplayer.syncInventory();
		}

		this.c = true;
	}

}
