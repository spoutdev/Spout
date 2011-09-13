package org.getspout.spout.keyboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.KeyBinding;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.PacketKeyBinding;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleKeyBindingManager implements KeyBindingManager {
	private List<KeyBinding> bindings = new ArrayList<KeyBinding>();
	@Override
	public void registerBinding(String id, Keyboard defaultKey, String description, BindingExecutionDelegate callback, Plugin plugin) throws Exception {
		
		System.out.println("Plugin ["+plugin.getDescription().getName()+"] registered key ["+defaultKey+"]!");
		
		if(searchBinding(id, plugin) != null){
			throw new Exception("This binding is already registered: "+id+" for plugin ["+plugin.getDescription().getName()+"]");
		}
		KeyBinding binding = new KeyBinding(id, defaultKey, description, plugin, callback);
		bindings.add(binding);
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(p instanceof SpoutPlayer){
				sendKeybinding((SpoutPlayer)p, binding);
			}
		}
	}
	
	@Override
	public void summonKey(String id, Plugin plugin, SpoutPlayer player, Keyboard key, ScreenType screenType, boolean pressed) {
		KeyBinding binding = searchBinding(id, plugin);
		if(binding == null)
			return;
		if(pressed)
		{
			KeyPressedEvent event = new KeyPressedEvent(key.getKeyCode(), player, screenType);
			try{
				binding.getDelegate().keyPressed(event);
			} catch(Exception e){
				System.out.println("Could not execute Key Press Delegate of plugin ["+plugin.getDescription().getName()+"] for action ["+id+"]!");
				e.printStackTrace();
			}
		} else {
			KeyReleasedEvent event = new KeyReleasedEvent(key.getKeyCode(), player, screenType);
			try{
				binding.getDelegate().keyReleased(event);
			} catch(Exception e){
				System.out.println("Could not execute Key Release Delegate of plugin ["+plugin.getDescription().getName()+"] for action ["+id+"]!");
				e.printStackTrace();
			}
		}
	}

	private void sendKeybinding(SpoutPlayer p, KeyBinding binding) {
		if(p.isSpoutCraftEnabled())
			p.sendPacket(new PacketKeyBinding(binding));
	}
	
	private KeyBinding searchBinding(String id, Plugin plugin)
	{
		for(KeyBinding binding:bindings)
		{
			if(binding.getId().equals(id) && binding.getPlugin().equals(plugin)){
				return binding;
			}
		}
		return null;
	}
	
	public void onPlayerJoin(SpoutPlayer player){
		for(KeyBinding binding:bindings)
		{
			sendKeybinding(player, binding);
		}
	}
}
