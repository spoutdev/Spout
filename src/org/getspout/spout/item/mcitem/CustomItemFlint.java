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
package org.getspout.spout.item.mcitem;

import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.Food;
import org.getspout.spoutapi.material.MaterialData;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumAnimation;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class CustomItemFlint extends Item{

	protected CustomItemFlint() {
		super(62);
		a(6, 0).a("flint");
	}
	
	@Override
	public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
		CustomItem item = MaterialData.getCustomItem(itemstack.getData());
		if (item instanceof Food) {
			--itemstack.count;
			entityhuman.getFoodData().a(((Food)item).getHungerRestored(), 0.6F);
		}
		return itemstack;
	}
	
	@Override
    public EnumAnimation d(ItemStack itemstack) {
		CustomItem item = MaterialData.getCustomItem(itemstack.getData());
		if (item instanceof Food) {
			return EnumAnimation.b;
		}
		return super.d(itemstack);
    }
	
	@Override
	public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
		CustomItem item = MaterialData.getCustomItem(itemstack.getData());
		if (item instanceof Food) {
			if (entityhuman.b(false)) {
				entityhuman.a(itemstack, 32);
			}
		}
	
		return itemstack;
	}
	
	public static void replaceFlint() {
		Item.byId[MaterialData.flint.getRawId()] = null;
		Item.byId[MaterialData.flint.getRawId()] = new CustomItemFlint();
	}

}
