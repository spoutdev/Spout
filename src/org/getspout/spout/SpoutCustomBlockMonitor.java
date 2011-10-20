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

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutCustomBlockMonitor extends PlayerListener{
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		if (event.getClickedBlock() == null) {
		//	return;
		}
		ItemStack inHand = event.getItem();
		if (inHand == null) {
			return;
		}
		Material mat = MaterialData.getMaterial(inHand.getTypeId(), inHand.getDurability());
		if (mat instanceof CustomItem) {
			CustomItem item = (CustomItem)mat;
			event.setCancelled(!item.onItemInteract((SpoutPlayer)event.getPlayer(), (SpoutBlock)event.getClickedBlock(), event.getBlockFace()));
		}
	}

}
