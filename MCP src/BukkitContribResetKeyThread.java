package net.minecraft.src;
import net.minecraft.client.Minecraft;
public class BukkitContribResetKeyThread extends Thread {
	 
	 protected GameSettings settings;
	 protected KeyBinding binding;
	 public BukkitContribResetKeyThread(GameSettings settings, KeyBinding binding) {
		  this.settings = settings;
		  this.binding = binding;
	 }

	 public void run() {
		  try {
				sleep(5);
		  }
		  catch (Exception e) { }
		  settings.keyBindToggleFog = binding;
	 }
}