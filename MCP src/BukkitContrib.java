package net.minecraft.src;
//BukkitContrib
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.BufferedReader;

public class BukkitContrib {
    private static int buildVersion = -1;
    private static int minorVersion = -1;
    private static int majorVersion = -1;
    private static int clientBuildVersion = 7;
    private static int clientMinorVersion = 0;
    private static int clientMajorVersion = 0;
    private static Minecraft game = null;
    private static PacketPluginReload reloadPacket = null;
    private static Object zanMinimap = null;
    private static boolean zanFailed = false;
    public static HashMap<Integer, String> entityLabel = new HashMap<Integer, String>();
    public static boolean runOnce = false;
    public static byte minView = -1;
    public static byte maxView = -1;
    
    public static void setVersion(String version) {
        try {
        
            String split[] = version.split("\\.");
            BukkitContrib.buildVersion = Integer.valueOf(split[2]);
            BukkitContrib.minorVersion = Integer.valueOf(split[1]);
            BukkitContrib.majorVersion = Integer.valueOf(split[0]);
        }
        catch (Exception e) {}
        System.out.println("Set BukkitContrib v. " + getVersionString());
        if (!runOnce) {
            CustomPacket.addClassMapping();
            runOnce = true;
        }
        if (isUpdateAvailable() && getVersion() > -1) {
            createBukkitContribAlert("Update Available!", "bit.ly/bukkitcontrib", 323);
        }
    }
    
    public static String versionToString(String version) {
        String split[] = version.split("\\.");
        return ChatColor.getByCode(Integer.parseInt(split[0])).toString() + ChatColor.WHITE.toString() +
            ChatColor.getByCode(Integer.parseInt(split[1])) + ChatColor.WHITE.toString() + 
            ChatColor.getByCode(Integer.parseInt(split[2]));
    }
    
    public static String colorToString(String color) {
        String s = "";
        String split[] = color.split(ChatColor.WHITE.toString());
        for (int i = 0; i < split.length; i++) {
            int code = 0;
            for (int j = 0; j < split[i].length(); j++) {
                code += (int)(split[i].charAt(j));
            }
            s += (char)(code - ChatColor.BLACK.toString().charAt(0));
            if (i < color.length() -1) {
                s += ".";
            }
        }
        return s;
    }
    
    public static int getMajorVersion() {
        return BukkitContrib.majorVersion;
    }
    
    public static int getMinorVersion() {
        return BukkitContrib.minorVersion;
    }
    
    public static int getBuildVersion() {
        return BukkitContrib.buildVersion;
    }
    
    public static int getVersion() {
        return  BukkitContrib.buildVersion +  BukkitContrib.minorVersion * 10 + BukkitContrib.majorVersion * 100;
    }
    
    public static int getClientMajorVersion() {
        return BukkitContrib.clientMajorVersion;
    }
    
    public static int getClientMinorVersion() {
        return BukkitContrib.clientMinorVersion;
    }
    
    public static int getClientBuildVersion() {
        return BukkitContrib.clientBuildVersion;
    }
    
    public static int getClientVersion() {
        return  BukkitContrib.clientBuildVersion +  BukkitContrib.clientMinorVersion * 10 + BukkitContrib.clientMajorVersion * 100;
    }
    
    public static String getClientVersionString() {
        return "" + BukkitContrib.clientMajorVersion + "." + BukkitContrib.clientMinorVersion + "." + BukkitContrib.clientBuildVersion;
    }
    
    public static String getVersionString() {
        return "" + BukkitContrib.majorVersion + "." + BukkitContrib.minorVersion + "." + BukkitContrib.buildVersion;
    }
    
    public static void resetBukkitContrib() {
        reset();
        game = null;
        entityLabel = new HashMap<Integer, String>();
    }
    
    private static void reset() {
        BukkitContrib.buildVersion = -1;
        BukkitContrib.minorVersion = -1;
        BukkitContrib.majorVersion = -1;
        System.out.println("Reset BukkitContrib");
    }
    
    public static boolean isEnabled() {
        return getBuildVersion() > -1 && getMinorVersion() > -1 && getMajorVersion() > -1;
    }
    
    public static Minecraft getGameInstance() {
        if (game == null) {
            Field f = null;
            try {
                //Try MC native method signature
                f = Minecraft.class.getDeclaredField("a");
            }
            catch (Exception e) {
                //Try MCP method signature
                try {
                    f = Minecraft.class.getDeclaredField("theMinecraft");
                }
                catch (Exception e1) {
                    //Failed both
                    e.printStackTrace();
                }
            }
            if (f != null) {
                f.setAccessible(true);
                try {
                    game = (Minecraft)f.get(null);
                } 
                catch (Exception e) {game = null;}
            }
        }
        return game;
    }
    
    public static Entity getEntityFromId(int id) {
        if (getGameInstance().thePlayer.entityId == id) {
            return getGameInstance().thePlayer;
        }
        WorldClient world = (WorldClient)getGameInstance().theWorld;
        return world.func_709_b(id);
    }
    
    public static EntityPlayer getPlayerFromId(int id) {
        if (getGameInstance().thePlayer.entityId == id) {
            return getGameInstance().thePlayer;
        }
        WorldClient world = (WorldClient)getGameInstance().theWorld;
        Entity e = world.func_709_b(id);
        if (e instanceof EntityPlayer) {
            return (EntityPlayer)e;
        }
        return null;
    }
    
    public static PacketPluginReload getReloadPacket() {
        return reloadPacket;
    }
    
    public static void setReloadPacket(PacketPluginReload packet) {
        reloadPacket = packet;
    }
    
    public static void createBukkitContribAlert(String title, String message, int toRender) {
        if (getGameInstance() != null) {
            getGameInstance().guiAchievement.queueNotification(title, message, toRender);
        }
    }
    
    public static Object getZanMinimap() {
        if (zanMinimap == null && !zanFailed) {
            try {
                Class<?> c = Class.forName("ZanMinimap");
                zanMinimap = c.getDeclaredConstructors()[0].newInstance((Object[])null);
            }
            catch (Exception e) {
                zanFailed = true;
            }
        }
        return zanMinimap;
    }
    
    
    public static byte getNextRenderDistance(int current) {
        //default behavior
        if (BukkitContrib.minView == -1 && BukkitContrib.maxView == -1) return (byte)((current + 1) & 3);
        int minView = BukkitContrib.minView == -1 ? 3 : BukkitContrib.minView;
        int maxView = BukkitContrib.maxView == -1 ? 0 : BukkitContrib.maxView;
        //System.out.println("Current: " + current);
        current++;
        if (current > minView) {
            current = Math.max(0, maxView);
        }
        //System.out.println("Max: " + maxView + " Min: " + minView + " New View: " + current);
        return (byte)current;
    }
    
    public static boolean isUpdateAvailable() {
        try {
            URL url = new URL("http://bit.ly/clientBukkitContribVersionCheck");
             
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                String[] split = str.split("\\.");
                int version = Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1]) * 10 + Integer.parseInt(split[2]);
                if (version > getClientVersion()){
                    in.close();
                    return true;
                }
            }
            in.close();
        }
        catch (Exception e) {}
        return false;
    }

}