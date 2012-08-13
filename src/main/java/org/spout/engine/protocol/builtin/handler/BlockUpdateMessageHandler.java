/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.protocol.builtin.handler;

import org.spout.api.material.Material;
import org.spout.api.entity.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.builtin.message.BlockUpdateMessage;

/**
 *
 */
public class BlockUpdateMessageHandler extends MessageHandler<BlockUpdateMessage> {
	@Override
	public void handleClient(Session session, BlockUpdateMessage message) {
		if(!session.hasPlayer()) {
			return;
		}

		Player player = session.getPlayer();
		player.getWorld().getBlock(message.getX(), message.getY(), message.getZ(), player)
		.setMaterial(Material.get(message.getType()), message.getData())
		.setBlockLight(message.getBlockLight()).setSkyLight(message.getSkyLight());
	}
}
