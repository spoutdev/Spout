package org.getspout.spout.keyboard;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.KeyBinding;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.PacketKeyBinding;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleKeyBindingManager implements KeyBindingManager {
	private HashMap<UUID, KeyBinding> bindings = new HashMap<UUID, KeyBinding>();
	@Override
	public void registerBinding(String id, Keyboard defaultKey, String description, BindingExecutionDelegate callback, Plugin plugin) throws IllegalArgumentException {
				
		if(searchBinding(id, plugin) != null){
			throw new IllegalArgumentException("This binding is already registered: "+id+" for plugin ["+plugin.getDescription().getName()+"]");
		}
		KeyBinding binding = new KeyBinding(id, defaultKey, description, plugin, callback);
		bindings.put(binding.getUniqueId(), binding);
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(p instanceof SpoutPlayer){
				sendKeybinding((SpoutPlayer)p, binding);
			}
		}
	}
	
	private KeyBinding searchBinding(String id, Plugin plugin) {
		for(KeyBinding binding:bindings.values()) {
			if(binding.getId().equals(id) && binding.getPlugin().equals(plugin))
				return binding;
		}
		return null;
	}

	@Override
	public void summonKey(UUID uniqueId, SpoutPlayer player, Keyboard key, boolean pressed) {
		KeyBinding binding = searchBinding(uniqueId);
		if(binding == null)
			return;
		String id = binding.getId();
		Plugin plugin = binding.getPlugin();
		if(pressed)
		{
			
			try{
				binding.getDelegate().keyPressed(new KeyBindingEvent(player, binding));
			} catch(Exception e){
				System.out.println("Could not execute Key Press Delegate of plugin ["+plugin.getDescription().getName()+"] for action ["+id+"]!");
				e.printStackTrace();
			}
		} else {
			
			try{
				binding.getDelegate().keyReleased(new KeyBindingEvent(player, binding));
			} catch(Exception e){
				System.out.println("Could not execute Key Release Delegate of plugin ["+plugin.getDescription().getName()+"] for action ["+id+"]!");
				e.printStackTrace();
			}
		}
	}

	private KeyBinding searchBinding(UUID uniqueId) {
		return bindings.get(uniqueId);
	}

	private void sendKeybinding(SpoutPlayer p, KeyBinding binding) {
		if(p.isSpoutCraftEnabled())
			p.sendPacket(new PacketKeyBinding(binding));
	}
	
	public void onPlayerJoin(SpoutPlayer player){
		for(KeyBinding binding:bindings.values())
		{
			sendKeybinding(player, binding);
		}
	}
}
