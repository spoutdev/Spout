package org.bukkitcontrib;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitcontrib.block.ContribCraftChunk;
import org.bukkitcontrib.keyboard.KeyboardManager;
import org.bukkitcontrib.keyboard.SimpleKeyboardManager;
import org.bukkitcontrib.packet.CustomPacket;
import org.bukkitcontrib.player.AppearanceManager;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.player.SimpleAppearanceManager;

public class BukkitContrib extends JavaPlugin{
    private static final ContribPlayerListener playerListener = new ContribPlayerListener();
    private static final ContribChunkListener chunkListener = new ContribChunkListener();
    private static final PluginListener pluginListener = new PluginListener();
    private static final SimpleKeyboardManager keyManager = new SimpleKeyboardManager();
    private static final SimpleAppearanceManager appearanceManager = new SimpleAppearanceManager();
    private static BukkitContrib instance;
    @Override
    public void onDisable() {
        //order matters
        appearanceManager.onPluginDisable();
        Player[] online = getServer().getOnlinePlayers();
        //Force ALL packets to be sent before continuing
        //Can't have custom packets in the queue when the plugin disables
        for (Player player : online) {
        	//TODO send plugin reload packet
            ContribCraftPlayer.sendAllPackets(player);
        }
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
            ContribCraftPlayer.resetNetServerHandler(player);
        }
        ContribCraftChunk.resetAllBukkitChunks();
        
        
    }

    @Override
    public void onEnable() {
        BukkitContrib.instance = this;
        getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.CHUNK_LOAD, chunkListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Normal, this);

        Player[] online = getServer().getOnlinePlayers();
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
            ContribCraftPlayer.resetNetServerHandler(player);
            ContribCraftPlayer.updateNetServerHandler(player);
            ContribCraftPlayer.updateBukkitEntity(player);
            sendBukkitContribVersionChat(player);
        }
        
        ContribCraftChunk.replaceAllBukkitChunks();
        appearanceManager.onPluginEnable();
        
        //Remove mappings from previous loads
        //Can not remove them on disable because the packets will still be in the send queue
        CustomPacket.removeClassMapping();
        CustomPacket.addClassMapping();
        Logger.getLogger("Minecraft").info("BukkitContrib " + this.getDescription().getVersion() + " has been initialized");
    }

    public static BukkitContrib getInstance() {
        return instance;
    }
    
    public static KeyboardManager getKeyboardManager() {
        return keyManager;
    }
    
    public static AppearanceManager getAppearanceManager() {
        return appearanceManager;
    }
    
    public static ContribPlayer getPlayerFromId(int entityId) {
    	Player[] online = Bukkit.getServer().getOnlinePlayers();
        for (Player player : online) {
        	if (player.getEntityId() == entityId) {
        		return (ContribPlayer)player;
        	}
        }
        return null;
    }
    
    private static String versionToString(String version) {
        String split[] = version.split("\\.");
        return ChatColor.getByCode(Integer.parseInt(split[0])).toString() + ChatColor.WHITE.toString() +
            ChatColor.getByCode(Integer.parseInt(split[1])) + ChatColor.WHITE.toString() + 
            ChatColor.getByCode(Integer.parseInt(split[2]));
    }
    
    @SuppressWarnings("unused")
    private static String colorToString(String color) {
        String s = "";
        String split[] = color.split(ChatColor.WHITE.toString());
        for (int i = 0; i < split.length; i++) {
            int code = 0;
            for (int j = 0; j < split[i].length(); j++) {
                code += (int)(split[i].charAt(j));
            }
            s += (char)(code - ChatColor.BLACK.toString().charAt(0));
        }
        return s;
    }
    
    protected static void sendBukkitContribVersionChat(Player player) {
        player.sendRawMessage(versionToString(BukkitContrib.getInstance().getDescription().getVersion()));
    }
}
