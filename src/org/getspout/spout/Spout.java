package org.getspout.spout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.inventory.SimpleItemManager;
import org.getspout.spout.inventory.SpoutInventoryBuilder;
import org.getspout.spout.keyboard.SimpleKeyboardManager;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.player.SimpleAppearanceManager;
import org.getspout.spout.player.SimplePlayerManager;
import org.getspout.spout.player.SimpleSkyManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spout.sound.SimpleSoundManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketPluginReload;
import org.getspout.spoutapi.packet.PacketRenderDistance;

public class Spout extends JavaPlugin{
	public final SpoutPlayerListener playerListener;
	private final SpoutWorldListener chunkListener;
	private final PluginListener pluginListener;
	private static Spout instance;
	private final static int VERSION = 100;
	
	public Spout() {
		super();
		playerListener = new SpoutPlayerListener();
		chunkListener = new SpoutWorldListener();
		pluginListener = new PluginListener();
		SpoutManager.getInstance().setKeyboardManager(new SimpleKeyboardManager());
		SpoutManager.getInstance().setAppearanceManager(new SimpleAppearanceManager());
		SpoutManager.getInstance().setSoundManager(new SimpleSoundManager());
		SpoutManager.getInstance().setItemManager(new SimpleItemManager());
		SpoutManager.getInstance().setSkyManager(new SimpleSkyManager());
		SpoutManager.getInstance().setInventoryBuilder(new SpoutInventoryBuilder());
		SpoutManager.getInstance().setPlayerManager(new SimplePlayerManager());
	}
	@Override
	public void onDisable() {
		//order matters
		((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPluginDisable();
		((SimpleItemManager)SpoutManager.getItemManager()).reset();
		((SimpleSkyManager)SpoutManager.getSkyManager()).reset();
		Player[] online = getServer().getOnlinePlayers();
		for (Player player : online) {
			try {
				SpoutCraftPlayer scp = (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player);
				if (scp.isSpoutCraftEnabled()) {
					scp.sendPacket(new PacketRenderDistance(true, true));
					scp.sendPacket(new PacketPluginReload((SpoutCraftPlayer)player));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Player player : online) {
			SpoutCraftPlayer.removeBukkitEntity(player);
			SpoutCraftPlayer.resetNetServerHandler(player);
		}
		SpoutCraftChunk.resetAllBukkitChunks();
		
		getServer().getScheduler().cancelTasks(this);
		
		//Attempt to auto update if file is available
		try {
			File directory = new File(Bukkit.getServer().getUpdateFolder());
			if (directory.exists()) {
				File plugin = new File(directory.getPath(), "Spout.jar");
				if (plugin.exists()) {
					FileUtil.copy(plugin, this.getFile());
					try {
						plugin.delete();
					}
					catch (SecurityException e1) {}
				}
			}
		}
		catch (Exception e) {}
		
		//end the thread
		MapChunkThread.endThread();
	}

	@Override
	public void onEnable() {
		Spout.instance = this;
		(new ConfigReader()).read();
		(new Thread() {
			public void run() {
				update();
			}
		}).start();
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.CHUNK_LOAD, chunkListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.WORLD_LOAD, chunkListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Normal, this);

		Player[] online = getServer().getOnlinePlayers();
		for (Player player : online) {
			SpoutCraftPlayer.removeBukkitEntity(player);
			SpoutCraftPlayer.resetNetServerHandler(player);
			SpoutCraftPlayer.updateNetServerHandler(player);
			SpoutCraftPlayer.updateBukkitEntity(player);
			sendBukkitContribVersionChat(player);
			playerListener.manager.onPlayerJoin(player);
		}

		List<World> worlds = getServer().getWorlds();
		for (World world : worlds) {
			SpoutPlayerManagerTransfer.replacePlayerManager(((CraftWorld)world).getHandle());
		}
		
		SpoutCraftChunk.replaceAllBukkitChunks();
		((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPluginEnable();

		MapChunkThread.startThread(); // Always on
		
		//Start counting ticks
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ServerTickTask(), 0, 1);

		//Remove mappings from previous loads
		//Can not remove them on disable because the packets will still be in the send queue
		CustomPacket.removeClassMapping();
		CustomPacket.addClassMapping();
		Logger.getLogger("Minecraft").info("Spout " + this.getDescription().getVersion() + " has been initialized");
	}

	/**
	 * Gets the singleton instance of the bukkitcontrib plugin
	 * @return bukkitcontrib plugin
	 */
	public static Spout getInstance() {
		return instance;
	}
	
	private static String versionToString(String version) {
		String split[] = version.split("\\.");
		return ChatColor.getByCode(Integer.parseInt(split[0])).toString() + ChatColor.WHITE.toString() +
			ChatColor.getByCode(Integer.parseInt(split[1])) + ChatColor.WHITE.toString() + 
			ChatColor.getByCode(Integer.parseInt(split[2]));
	}
	
	@SuppressWarnings("unused")
	private static String colorToString(String color) {
		StringBuffer buffer = new StringBuffer();
		String split[] = color.split(ChatColor.WHITE.toString());
		for (int i = 0; i < split.length; i++) {
			int code = 0;
			for (int j = 0; j < split[i].length(); j++) {
				code += (int)(split[i].charAt(j));
			}
			buffer.append((char)(code - ChatColor.BLACK.toString().charAt(0)));
		}
		return buffer.toString();
	}
	
	protected static void sendBukkitContribVersionChat(Player player) {
		player.sendRawMessage(versionToString(getVersionString()));
	}
	
	protected static String getVersionString() {
		int version = getVersion();
		return (version / 100) + "." + ((version / 10) % 10) + "." + (version % 10);
	}
	
	protected static int getVersion() {
		try {
			String[] split = Spout.getInstance().getDescription().getVersion().split("\\.");
			return Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1]) * 10 + Integer.parseInt(split[2]);
		}
		catch (Exception e) {}
		return VERSION;
	}
	
	protected boolean isUpdateAvailable() {
		if (!ConfigReader.isAutoUpdate()) {
			return false;
		}
		try {
			URL url = new URL("http://dl.dropbox.com/u/49805/SpoutCraftVersion.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				String[] split = str.split("\\.");
				int version = Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1]) * 10 + Integer.parseInt(split[2]);
				if (version > getVersion()){
					in.close();
					return true;
				}
			}
			in.close();
		}
		catch (Exception e) {}
		return false;
	}
	
	protected void update() {
		//test install once
		File runOnce = new File(getDataFolder(), "runonce");
		if (!runOnce.exists()) {
			try {
				runOnce.createNewFile();
				pingLink("http://bit.ly/spoutserverrunonce");
			}
			catch (Exception e) {}
		}
		if (!isUpdateAvailable()) {
			return;
		}
		FileOutputStream fos = null;
		try {
			File directory = new File(Bukkit.getServer().getUpdateFolder());
			if (!directory.exists()) {
				try {
					directory.mkdir();
				}
				catch (SecurityException e1) {}
			}
			File plugin = new File(directory.getPath(), "Spout.jar");
			if (!plugin.exists()) {
				URL bukkitContrib = new URL("http://bit.ly/spoutautoupdate  ");
				HttpURLConnection con = (HttpURLConnection)(bukkitContrib.openConnection());
				System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
				fos = new FileOutputStream(plugin);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			}
		}
		catch (Exception e) {}
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void pingLink(String Url) {
		try {
			URL url = new URL(Url);
			HttpURLConnection con = (HttpURLConnection)(url.openConnection());
			System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String str = "";
			while ((str = in.readLine()) != null);
			in.close();
		}
		catch (Exception e) {}
	}
}
