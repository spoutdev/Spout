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
package org.getspout.spout.util;

import org.bukkit.entity.Player;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

public class GhostBlock {
	private static boolean removenext = false;
	
	public static void clearCustomBlock(SpoutBlock block, Player player) {
		if (block.isCustomBlock()) {
			SimpleMaterialManager mm = (SimpleMaterialManager)SpoutManager.getMaterialManager();
			mm.removeBlockOverride(block);
			player.sendMessage("Custom Block was removed from coords: " + block.getX() + ", " + block.getY() + ", " + block.getZ() + " in world: " + block.getWorld().getName());
		} else {
			player.sendMessage("No custom block was found at that position");
		}
		toggleGhostBlock();
	}
	
	public static void toggleGhostBlock() {
		GhostBlock.removenext = !GhostBlock.removenext;
	}
	
	public static boolean isRemoveGhostBlock() {
		return removenext;
	}
}
