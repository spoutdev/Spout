package org.bukkitcontrib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.bukkitcontrib.block.ContribCraftChunk;
import org.bukkitcontrib.config.ConfigReader;
import org.bukkitcontrib.event.bukkitcontrib.ServerTickEvent;
import org.bukkitcontrib.keyboard.KeyboardManager;
import org.bukkitcontrib.keyboard.SimpleKeyboardManager;
import org.bukkitcontrib.packet.CustomPacket;
import org.bukkitcontrib.packet.PacketPluginReload;
import org.bukkitcontrib.packet.PacketRenderDistance;
import org.bukkitcontrib.player.AppearanceManager;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.player.SimpleAppearanceManager;
import org.bukkitcontrib.sound.SimpleSoundManager;
import org.bukkitcontrib.sound.SoundManager;

public class BukkitContrib extends JavaPlugin{
    public static final ContribPlayerListener playerListener = new ContribPlayerListener();
    private static final ContribChunkListener chunkListener = new ContribChunkListener();
    private static final PluginListener pluginListener = new PluginListener();
    private static final SimpleKeyboardManager keyManager = new SimpleKeyboardManager();
    private static final SimpleAppearanceManager appearanceManager = new SimpleAppearanceManager();
    private static final SimpleSoundManager soundManager = new SimpleSoundManager();
    private static BukkitContrib instance;
    @Override
    public void onDisable() {
        //order matters
        appearanceManager.onPluginDisable();
        Player[] online = getServer().getOnlinePlayers();
        for (Player player : online) {
            try {
                ContribCraftPlayer ccp = (ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player);
                if (ccp.getVersion() > 5)
                    ccp.sendPacket(new PacketRenderDistance(true, true));
                if (ccp.getVersion() > 4)
                    ccp.sendPacket(new PacketPluginReload((ContribCraftPlayer)player));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
            ContribCraftPlayer.resetNetServerHandler(player);
        }
        ContribCraftChunk.resetAllBukkitChunks();
        
        getServer().getScheduler().cancelTasks(this);
        
        //Attempt to auto update if file is available
        try {
            File directory = new File(Bukkit.getServer().getUpdateFolder());
            if (directory.exists()) {
                File plugin = new File(directory.getPath(), "BukkitContrib.jar");
                if (plugin.exists()) {
                    FileUtil.copy(plugin, this.getFile());
                    plugin.delete();
                }
            }
        }
        catch (Exception e) {}
        
    }

    @Override
    public void onEnable() {
        BukkitContrib.instance = this;
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
        getServer().getPluginManager().registerEvent(Type.CHUNK_LOAD, chunkListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Normal, this);

        Player[] online = getServer().getOnlinePlayers();
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
            ContribCraftPlayer.resetNetServerHandler(player);
            ContribCraftPlayer.updateNetServerHandler(player);
            ContribCraftPlayer.updateBukkitEntity(player);
            sendBukkitContribVersionChat(player);
            playerListener.manager.onPlayerJoin(player);
        }
        
        ContribCraftChunk.replaceAllBukkitChunks();
        appearanceManager.onPluginEnable();
        
        //Start counting ticks
        Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent());

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
    
    public static SoundManager getSoundManager() {
        return soundManager;
    }
    
    public static ContribPlayer getPlayerFromId(int entityId) {
        Player[] online = Bukkit.getServer().getOnlinePlayers();
        for (Player player : online) {
            if (player.getEntityId() == entityId) {
                return (ContribPlayer)ContribCraftPlayer.getContribPlayer(player);
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
    
    protected int getVersion() {
        try {
            String[] split = this.getDescription().getVersion().split("\\.");
            return Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1]) * 10 + Integer.parseInt(split[2]);
        }
        catch (Exception e) {}
        return -1;
    }
    
    protected boolean isUpdateAvailable() {
        if (!ConfigReader.isAutoUpdate()) {
            return false;
        }
        try {
            URL url = new URL("http://dl.dropbox.com/u/49805/BukkitContribVersion.txt");
             
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
        if (!isUpdateAvailable()) {
            return;
        }
        try {
            File directory = new File(Bukkit.getServer().getUpdateFolder());
            if (!directory.exists()) {
                directory.mkdir();
            }
            File plugin = new File(directory.getPath(), "BukkitContrib.jar");
            if (!plugin.exists()) {
                URL bukkitContrib = new URL("http://bit.ly/autoupdateBukkitContrib");
                ReadableByteChannel rbc = Channels.newChannel(bukkitContrib.openStream());
                FileOutputStream fos = new FileOutputStream(plugin);
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            }
        }
        catch (Exception e) {}
    }
}
