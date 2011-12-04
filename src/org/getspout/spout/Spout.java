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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.server.Packet18ArmAnimation;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.FileUtil;
import org.bukkit.util.config.Configuration;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.block.mcblock.CustomBlock;
import org.getspout.spout.chunkcache.SimpleCacheManager;
import org.getspout.spout.command.SpoutCommand;
import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.entity.tracker.EntityTrackerManager;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spout.inventory.SpoutInventoryBuilder;
import org.getspout.spout.item.mcitem.CustomItemPickaxe;
import org.getspout.spout.item.mcitem.CustomItemSpade;
import org.getspout.spout.keyboard.SimpleKeyBindingManager;
import org.getspout.spout.keyboard.SimpleKeyboardManager;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.packet.SimplePacketManager;
import org.getspout.spout.player.SimpleAppearanceManager;
import org.getspout.spout.player.SimpleBiomeManager;
import org.getspout.spout.player.SimpleFileManager;
import org.getspout.spout.player.SimplePlayerManager;
import org.getspout.spout.player.SimpleSkyManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spout.sound.SimpleSoundManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkstore.PlayerTrackingManager;
import org.getspout.spoutapi.chunkstore.SimpleChunkDataManager;
import org.getspout.spoutapi.io.CRCStore;
import org.getspout.spoutapi.packet.PacketRenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.plugin.SpoutPlugin;
import org.getspout.spoutapi.util.UniqueItemStringMap;

@SuppressWarnings("deprecation")
public class Spout extends SpoutPlugin{
	public final SpoutPlayerListener playerListener;
	protected final EntityTrackerManager entityTrackingManager;
	protected final PlayerTrackingManager playerTrackingManager;
	protected final SpoutWorldListener chunkListener;
	protected final SpoutWorldMonitorListener chunkMonitorListener;
	protected final SpoutBlockListener blockListener;
	protected final SpoutEntityListener entityListener;
	protected final PluginListener pluginListener;
	protected final SpoutCustomBlockMonitor blockMonitor;
	protected static Spout instance;
	protected Configuration CRCConfig;
	protected Configuration itemMapConfig;
	protected List<SpoutPlayer> playersOnline = new ArrayList<SpoutPlayer>();
	protected Thread shutdownThread = null;
	
	public Spout() {
		super();
		playerListener = new SpoutPlayerListener();
		chunkListener = new SpoutWorldListener();
		chunkMonitorListener = new SpoutWorldMonitorListener();
		pluginListener = new PluginListener();
		entityListener = new SpoutEntityListener();
		blockMonitor = new SpoutCustomBlockMonitor();
		SpoutManager.getInstance().setKeyboardManager(new SimpleKeyboardManager());
		SpoutManager.getInstance().setAppearanceManager(new SimpleAppearanceManager());
		SpoutManager.getInstance().setSoundManager(new SimpleSoundManager());
		SpoutManager.getInstance().setSkyManager(new SimpleSkyManager());
		SpoutManager.getInstance().setInventoryBuilder(new SpoutInventoryBuilder());
		SpoutManager.getInstance().setPacketManager(new SimplePacketManager());
		SpoutManager.getInstance().setPlayerManager(new SimplePlayerManager());
		SpoutManager.getInstance().setCacheManager(new SimpleCacheManager());
		SpoutManager.getInstance().setChunkDataManager(new SimpleChunkDataManager());
		SpoutManager.getInstance().setBiomeManager(new SimpleBiomeManager());
		SpoutManager.getInstance().setFileManager(new SimpleFileManager());
		SpoutManager.getInstance().setKeyBindingManager(new SimpleKeyBindingManager());
		SpoutManager.getInstance().setMaterialManager(new SimpleMaterialManager());
		SpoutManager.getInstance().setWorldManager(new SimpleWorldManager());
		blockListener = new SpoutBlockListener();
		entityTrackingManager = new EntityTrackerManager();
		playerTrackingManager = new PlayerTrackingManager();
		shutdownThread = new ShutdownThread();
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

	@Override
	public void onDisable() {
		//order matters
		CustomBlock.resetBlocks();
		((SimpleMaterialManager)SpoutManager.getMaterialManager()).reset();
		((SimpleSkyManager)SpoutManager.getSkyManager()).reset();
		((SimplePlayerManager)SpoutManager.getPlayerManager()).onPluginDisable();
		Player[] online = getServer().getOnlinePlayers();
		for (Player player : online) {
			try {
				SpoutCraftPlayer scp = (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player);
				scp.resetMovement();
				if (scp.isSpoutCraftEnabled()) {
					scp.sendPacket(new PacketRenderDistance(true, true));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		//for (Player player : online) {
		//	SpoutCraftPlayer.removeBukkitEntity(player);
		//	SpoutCraftPlayer.resetNetServerHandler(player);
		//}
		SpoutCraftChunk.resetAllBukkitChunks();
		
		getServer().getScheduler().cancelTasks(this);
		
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.unloadAllChunks();
		dm.closeAllFiles();
		
		CRCConfig.save();
		
		if (itemMapConfig != null) {
			synchronized(itemMapConfig) {
				itemMapConfig.save();
			}
		}
		
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
		
		SimpleFileManager.clearTempDirectory();
		
		//end the thread
		MapChunkThread.endThread();
		PacketCompressionThread.endThread();
		ChunkCompressionThread.endThread();		
		
		Runtime.getRuntime().removeShutdownHook(shutdownThread);
		
		Logger.getLogger("Minecraft").info("Spout " + getVersion() + " has been disabled");

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
		
		registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest);
		registerEvent(Type.PLAYER_KICK, playerListener, Priority.Normal);
		registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest);
		registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor);
		registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor);
		registerEvent(Type.PLAYER_INTERACT, blockMonitor, Priority.High);
		registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Monitor);
		registerEvent(Type.CHUNK_LOAD, chunkListener, Priority.Lowest);
		registerEvent(Type.WORLD_LOAD, chunkListener, Priority.Lowest);
		registerEvent(Type.WORLD_SAVE, chunkMonitorListener, Priority.Monitor);
		registerEvent(Type.WORLD_UNLOAD, chunkMonitorListener, Priority.Monitor);
		registerEvent(Type.CHUNK_UNLOAD, chunkMonitorListener, Priority.Monitor);
		registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Normal);
		registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal);
		registerEvent(Type.BLOCK_CANBUILD, blockListener, Priority.Lowest);
		registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Lowest);
		registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest);
		registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Monitor);
		registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Monitor);
		registerEvent(Type.ITEM_SPAWN, entityListener, Priority.Monitor);

		getCommand("spout").setExecutor(new SpoutCommand(this));

		for (SpoutPlayer player : getSpoutServer().getOnlinePlayers()) {
			SpoutCraftPlayer.resetNetServerHandler(player);
			SpoutCraftPlayer.updateNetServerHandler(player);
			SpoutCraftPlayer.updateBukkitEntity(player);
			authenticate(player);
			playerListener.manager.onPlayerJoin(player);
			((SimplePlayerManager)SpoutManager.getPlayerManager()).onPlayerJoin(player);
			player.setPreCachingComplete(true); //already done if we are already online!
			synchronized(playersOnline) {
				playersOnline.add(player);
			}
		}

		SpoutCraftChunk.replaceAllBukkitChunks();
		((SimplePlayerManager)SpoutManager.getPlayerManager()).onPluginEnable();
		
		CustomItemSpade.replaceSpades();
		CustomItemPickaxe.replacePickaxes();
		CustomBlock.replaceBlocks();
		
		ChunkCompressionThread.startThread();
		MapChunkThread.startThread();
		PacketCompressionThread.startThread();
		
		//Start counting ticks
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ServerTickTask(), 0, 1);

		//Remove mappings from previous loads
		//Can not remove them on disable because the packets will still be in the send queue
		CustomPacket.removeClassMapping();
		CustomPacket.addClassMapping();
		
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.loadAllChunks();
		
		CRCConfig = new Configuration(new File(this.getDataFolder(), "CRCCache.yml"));
		CRCConfig.load();
		
		CRCStore.setConfigFile(CRCConfig);
		
		itemMapConfig = new Configuration(new File(this.getDataFolder(), "itemMap.yml"));
		itemMapConfig.load();
		
		UniqueItemStringMap.setConfigFile(itemMapConfig);
		
		SimpleMaterialManager.disableFlintStackMix();
		
		this.log("Spout %s has been initialized", getVersion());
		
		/*try {
			MinecraftServer server = ((CraftServer)getServer()).getServer();
			NetworkListenThread thread = server.networkListenThread;
			SpoutNetworkAcceptThread acceptThread = new SpoutNetworkAcceptThread(thread, "Spout Network Accept Thread", server);
			acceptThread.start();
			
			Field e = NetworkListenThread.class.getDeclaredField("e");
			e.setAccessible(true);
			Thread old = (Thread) e.get(thread);
			e.set(thread, acceptThread);
			
			old.interrupt();
			old.join(100);
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	
	@Override
	public String getVersion() {
		return getVersion(true);
	}
	
	public String getVersion(boolean verbose) {
		if (this.getDescription().getVersion().contains("${build.number}")) {
			return "-1" + (verbose ? " [Custom Build]" : "");
		}
		return this.getDescription().getVersion();
	}

	/**
	 * Gets the singleton instance of the Spout plugin
	 * @return Spout plugin
	 */
	public static Spout getInstance() {
		return instance;
	}
	
	public EntityTrackerManager getEntityTrackingManager() {
		return entityTrackingManager;
	}
	
	public PlayerTrackingManager getPlayerTrackingManager() {
		return playerTrackingManager;
	}
	
	public void authenticate(Player player) {
		if (ConfigReader.authenticateSpoutcraft()) {
			Packet18ArmAnimation packet = new Packet18ArmAnimation();
			packet.a = -42;
			((SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(player)).getNetServerHandler().sendImmediatePacket(packet);
		}
	}
	
	protected boolean isUpdateAvailable() {
		File runOnce = new File(getDataFolder(), "runonce");
		runOnce.delete();
		if (!ConfigReader.isAutoUpdate()) {
			return false;
		}
		String latest = getRBVersion();

		if (latest != null) {
			try {
				int current = Integer.parseInt(getVersion());
				if (current != -1) { // -1 == custom build
					int newest = Integer.parseInt(latest);
					return current < newest;
				}
			}
			catch (NumberFormatException e){
				return false;
			}
			
		}
		return false;
	}
	
	public String getRBVersion() {
		try {
			String version = "-1";
			URL url = new URL("http://ci.getspout.org/job/Spout/Recommended/buildNumber");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				 version = str;
				 return version;
			}
			in.close();
		} catch (Exception e) {}
		return null;
	 }
	
	protected void update() {
		//test install once
		File runOnce = new File(getDataFolder(), "spout_runonce");
		if (!runOnce.exists()) {
			try {
				runOnce.createNewFile();
				pingLink("http://bit.ly/spoutserverinstalls");
			}
			catch (Exception e) {}
		}
		if (!isUpdateAvailable()) {
			return;
		}
		pingLink("http://bit.ly/spoutserverupdated");
		FileOutputStream fos = null;
		try {
			File directory = new File(Bukkit.getServer().getUpdateFolder());
			if (!directory.exists()) {
				try {
					directory.mkdir();
				}
				catch (SecurityException e1) {}
			}
			File tempDirectory = new File(directory, "temp");
			if (!tempDirectory.exists()) {
				try {
					tempDirectory.mkdir();
				}
				catch (SecurityException e1) {}
			}
			File plugin = new File(directory.getPath(), "Spout.jar");
			File temp = new File(tempDirectory.getPath(), "Spout.jar");
			if (!plugin.exists()) {
				URL spout = new URL("http://ci.getspout.org/view/SpoutDev/job/Spout/promotion/latest/Recommended/artifact/target/spout-dev-SNAPSHOT.jar");
				HttpURLConnection con = (HttpURLConnection)(spout.openConnection());
				System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
				fos = new FileOutputStream(temp);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			}
			if (temp.exists()) {
				FileUtils.moveFile(temp, plugin);
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

class ShutdownThread extends Thread {
	public void run() {
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.unloadAllChunks();
		dm.closeAllFiles();
	}
}
