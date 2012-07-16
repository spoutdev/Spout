package org.spout.engine.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spout.api.Spout;
import org.spout.api.keyboard.Input;
import org.spout.engine.SpoutEngine;

import gnu.trove.map.hash.TIntObjectHashMap;

public class SpoutInput implements Input {
	
	TIntObjectHashMap<String> keyCommands = new TIntObjectHashMap<String>();
	TIntObjectHashMap<String> mouseCommands = new TIntObjectHashMap<String>();
	
	
	
	public SpoutInput(){
		bind("KEY_W", "+Forward");
	}
	
	public void doKeypress(int key, boolean pressed){
		String cmd = keyCommands.get(key);
		if(cmd == null) return;
		if(cmd.startsWith("+")){
			if(pressed) Spout.getEngine().processCommand(((SpoutEngine)Spout.getEngine()).getCommandSource(), cmd);
			else {
				cmd = cmd.replaceFirst("+", "-");
				Spout.getEngine().processCommand(((SpoutEngine)Spout.getEngine()).getCommandSource(), cmd);				
			}
		}
		else{
			Spout.getEngine().processCommand(((SpoutEngine)Spout.getEngine()).getCommandSource(), cmd);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.spout.engine.input.Input#bind(java.lang.String, java.lang.String)
	 */
	public void bind(String key, String command){		
		if(key.startsWith("KEY")){
			int k = Keyboard.getKeyIndex(key);
			keyCommands.put(k, command);
		}
		if(key.startsWith("MOUSE")){
			int k = Mouse.getButtonIndex(key);
			mouseCommands.put(k, command);
		}
	}
	
	
	public void pollInput(){
		
	}
	
	
}
