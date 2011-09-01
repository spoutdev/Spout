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

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet103SetSlot;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.packet.standard.MCPacket103SetSlot;

/**
 * Implementation of MCPacket103SetSlot
 * @author Richard Robertson
 */
public class MCCraftPacket103SetSlot extends MCCraftPacket implements MCPacket103SetSlot
{
	private static Field __WindowField, __SlotField, __ItemStackField;
	
	private int __RawWindow, __RawSlot;
	private net.minecraft.server.ItemStack __NotchStack;
	private ItemStack __BukkitStack;
	private Slot __Slot;
	private Window __Window;
	
	// Glom onto Notch's packet
	static
	{
		Class<Packet103SetSlot> packetclass = Packet103SetSlot.class;
		try
		{
			__WindowField = packetclass.getDeclaredField("a");
			__SlotField = packetclass.getDeclaredField("b");
			__ItemStackField = packetclass.getDeclaredField("c");
			
			__WindowField.setAccessible(true);
			__SlotField.setAccessible(true);
			__ItemStackField.setAccessible(true);
		}
		catch (Exception ex)
		{
			Logger.getLogger("Minecraft").log(Level.WARNING, "org.getspout.spout.packet.standard.MCCraftPacket103SetSlot\nError accessing net.minecraft.server.Packet103SetSlot.");
		}
	}
	
	@Override
	public Slot getSlot()
	{
		return __Slot;
	}

	@Override
	public void setSlot(Slot slot)
	{
		__Slot = slot;
		try
		{
			__RawWindow = __Slot.RawWindowId;
			__Window = Window.getWindowById(__RawWindow);
			__RawSlot = __Slot.RawSlotId;
			__WindowField.set(packet, __RawWindow);
			__SlotField.set(packet, __RawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public Window getWindow()
	{
		return __Window;
	}

	@Override
	public int getRawSlot()
	{
		return __RawSlot;
	}

	@Override
	public void setRawSlot(int slot)
	{
		__RawSlot = slot;
		try
		{
			__SlotField.set(packet, slot);
			__Slot = Slot.getSlotByRawValues(__RawWindow, __RawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public int getRawWindow()
	{
		return __RawWindow;
	}

	@Override
	public void setRawWindow(int window)
	{
		__RawWindow = window;
		try
		{
			__WindowField.set(packet, window);
			__Window = Window.getWindowById(window);
			__Slot = Slot.getSlotByRawValues(__RawWindow, __RawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public ItemStack getItemStack()
	{
		return __BukkitStack;
	}

	@Override
	public void setItemStack(ItemStack itemStack)
	{
		__BukkitStack = itemStack;
		if (__BukkitStack == null)
			__NotchStack = null;
		else
			__NotchStack = new net.minecraft.server.ItemStack(__BukkitStack.getTypeId(), __BukkitStack.getAmount(), __BukkitStack.getDurability());
		try
		{
			__ItemStackField.set(packet, __NotchStack);
		}
		catch (Exception ex)
		{
		}
	}
	
	@Override
	public void setPacket(Packet packet, int packetId)
	{
		super.setPacket(packet, packetId);
		try
		{
			__RawWindow = ((Integer)__WindowField.get(packet)).intValue();
			__Window = Window.getWindowById(__RawWindow);
			__RawSlot = ((Integer)__SlotField.get(packet)).intValue();
			__Slot = Slot.getSlotByRawValues(__RawWindow, __RawSlot);
			__NotchStack = (net.minecraft.server.ItemStack)__ItemStackField.get(packet);
			if (__NotchStack != null)
				__BukkitStack = new ItemStack(__NotchStack.id, __NotchStack.count, (short)__NotchStack.damage);
		}
		catch (Exception ex)
		{
		}
	}
	
	@Override
	public String toString()
	{
		if (__NotchStack == null)
			return "{" + __Slot.toString() + ", null}";
		else
			return "{" + __Slot.toString() + ", " + __BukkitStack.toString() + "}";
	}
}
