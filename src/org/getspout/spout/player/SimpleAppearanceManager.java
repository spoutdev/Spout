package org.getspout.spout.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.PacketEntityTitle;
import org.getspout.spoutapi.packet.PacketSkinURL;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleAppearanceManager implements AppearanceManager{
	HashMap<String, String> globalSkinMap = new HashMap<String, String>();
	HashMap<String, HashMap<String, String>> skinMap = new HashMap<String, HashMap<String, String>>();
	HashMap<String, String> globalCloakMap = new HashMap<String, String>();
	HashMap<String, HashMap<String, String>> cloakMap = new HashMap<String, HashMap<String, String>>();
	HashMap<Integer, String> globalTitleMap = new HashMap<Integer, String>();
	HashMap<String, HashMap<Integer, String>> titleMap = new HashMap<String, HashMap<Integer, String>>();
	public SimpleAppearanceManager() {
		
	}
	
	@Override
	public void setGlobalSkin(HumanEntity target, String Url) {
		checkUrl(Url);
		globalSkinMap.put(target.getName(), Url);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			HashMap<String, String> map = skinMap.get(p.getName());
			if (map == null) {
				map = new HashMap<String, String>();
				skinMap.put(p.getName(), map);
			}
			map.put(target.getName(), Url);
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4)
				player.sendPacket(new PacketSkinURL(target.getEntityId(), Url));
		}
	}

	@Override
	public void setPlayerSkin(SpoutPlayer viewingPlayer, HumanEntity target, String Url) {
		checkUrl(Url);
		HashMap<String, String> map = skinMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			skinMap.put(viewingPlayer.getName(), map);
		}
		map.put(target.getName(), Url);
		SpoutCraftPlayer player = (SpoutCraftPlayer) viewingPlayer;
		if (player.getVersion() > 4)
			player.sendPacket(new PacketSkinURL(target.getEntityId(), Url));
	}

	@Override
	public String getSkinUrl(SpoutPlayer viewingPlayer, HumanEntity target) {
		HashMap<String, String> map = skinMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			skinMap.put(viewingPlayer.getName(), map);
		}
		String Url = map.get(target.getName());
		if (Url == null) {
			Url = globalSkinMap.get(target.getName());
			if (Url == null) {
				return getDefaultSkin(target);
			}
		}
		return Url;
	}
	
	@Override
	public String getCloakUrl(SpoutPlayer viewingPlayer, HumanEntity target) {
		HashMap<String, String> map = cloakMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			cloakMap.put(viewingPlayer.getName(), map);
		}
		String Url = map.get(target.getName());
		if (Url == null) {
			Url = globalCloakMap.get(target.getName());
			if (Url == null) {
				return getDefaultCloak(target);
			}
		}
		return Url;
	}
	
	@Override
	public String getTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
		HashMap<Integer, String> map = titleMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<Integer, String>();
			titleMap.put(viewingPlayer.getName(), map);
		}
		String title = map.get(target.getEntityId());
		if (title == null) {
			title = globalTitleMap.get(target.getEntityId());
			if (title == null) {
				return viewingPlayer.getName();
			}
		}
		return title;
	}

	@Override
	public void resetGlobalSkin(HumanEntity target) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			String Url = getDefaultSkin(target);
			globalSkinMap.put(target.getName(), Url);
			HashMap<String, String> map = skinMap.get(p.getName());
			if (map == null) {
				map = new HashMap<String, String>();
				skinMap.put(p.getName(), map);
			}
			map.put(target.getName(), Url);
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4)
				player.sendPacket(new PacketSkinURL(target.getEntityId(), Url));
		}
	}

	@Override
	public void resetPlayerSkin(SpoutPlayer viewingPlayer, HumanEntity target) {
		HashMap<String, String> map = skinMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			skinMap.put(viewingPlayer.getName(), map);
		}
		String Url = getDefaultSkin(target);
		map.put(target.getName(), Url);
		SpoutCraftPlayer player = (SpoutCraftPlayer)viewingPlayer;
		if (player.getVersion() > 4)
			player.sendPacket(new PacketSkinURL(target.getEntityId(), Url));
	}

	@Override
	public void resetAllSkins() {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (LivingEntity lv : w.getLivingEntities()) {
				if (lv instanceof HumanEntity) {
					resetGlobalSkin((HumanEntity)lv);
				}
			}
		}
	}

	@Override
	public void setGlobalCloak(HumanEntity target, String Url) {
		checkUrl(Url);
		globalCloakMap.put(target.getName(), Url);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			HashMap<String, String> map = cloakMap.get(p.getName());
			if (map == null) {
				map = new HashMap<String, String>();
				cloakMap.put(p.getName(), map);
			}
			map.put(target.getName(), Url);
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4)
				player.sendPacket(new PacketSkinURL(Url, target.getEntityId()));
		}
	}

	@Override
	public void setPlayerCloak(SpoutPlayer viewingPlayer, HumanEntity target, String Url) {
		checkUrl(Url);
		HashMap<String, String> map = cloakMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			cloakMap.put(viewingPlayer.getName(), map);
		}
		map.put(target.getName(), Url);
		SpoutCraftPlayer player = (SpoutCraftPlayer) viewingPlayer;
		if (player.isSpoutCraftEnabled())
			player.sendPacket(new PacketSkinURL(Url, target.getEntityId()));
	}

	@Override
	public void resetGlobalCloak(HumanEntity target) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			String Url = getDefaultCloak(target);
			globalCloakMap.put(target.getName(), Url);
			HashMap<String, String> map = cloakMap.get(p.getName());
			if (map == null) {
				map = new HashMap<String, String>();
				cloakMap.put(p.getName(), map);
			}
			map.put(target.getName(), Url);
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4)
				player.sendPacket(new PacketSkinURL(Url, target.getEntityId()));
		}
	}

	@Override
	public void resetPlayerCloak(SpoutPlayer viewingPlayer, HumanEntity target) {
		HashMap<String, String> map = cloakMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<String, String>();
			cloakMap.put(viewingPlayer.getName(), map);
		}
		String Url = getDefaultCloak(target);
		map.put(target.getName(), Url);
		SpoutCraftPlayer player = (SpoutCraftPlayer)viewingPlayer;
		if (player.getVersion() > 4)
			player.sendPacket(new PacketSkinURL(Url, target.getEntityId()));
	}

	@Override
	public void resetAllCloaks() {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (LivingEntity lv : w.getLivingEntities()) {
				if (lv instanceof HumanEntity) {
					resetGlobalCloak((HumanEntity)lv);
				}
			}
		}
	}

	@Override
	public void resetAll() {
		resetAllCloaks();
		resetAllSkins();
		resetAllTitles();
	}
	
	public void onPlayerJoin(SpoutPlayer player) {
		if (player.getVersion() > 4) {
			HashMap<Integer, String> tmap = titleMap.get(player.getName());
			if (tmap == null) {
				tmap = new HashMap<Integer, String>();
				titleMap.put(player.getName(), tmap);
			}
			HashMap<String, String> smap = skinMap.get(player.getName());
			if (smap == null) {
				smap = new HashMap<String, String>();
				skinMap.put(player.getName(), smap);
			}
			HashMap<String, String> cmap = cloakMap.get(player.getName());
			if (cmap == null) {
				cmap = new HashMap<String, String>();
				cloakMap.put(player.getName(), cmap);
			}
			for (World w : Bukkit.getServer().getWorlds()) {
				for (LivingEntity lv : w.getLivingEntities()) {
					String title = tmap.get(lv.getEntityId());
					if (title == null) {
						title = globalTitleMap.get(lv.getEntityId());
					}
					if (title != null) {
						((SpoutCraftPlayer)player).sendPacket(new PacketEntityTitle(lv.getEntityId(), title));
					}
					if (lv instanceof HumanEntity) {
						String Url = smap.get(((HumanEntity)lv).getName());
						if (Url == null) {
							Url = globalSkinMap.get(((HumanEntity)lv).getName());
							if (Url == null) {
								Url = "none";
							}
						}
						
						String cloakUrl = cmap.get(((HumanEntity)lv).getName());
						if (cloakUrl == null) {
							cloakUrl = globalCloakMap.get(((HumanEntity)lv).getName());
							if (cloakUrl == null) {
								cloakUrl = "none";
							}
						}
						((SpoutCraftPlayer)player).sendPacket(new PacketSkinURL(lv.getEntityId(), Url, cloakUrl));
					}
				}
			}
		}
	}
	
	public void onPluginEnable() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			skinMap.put(p.getName(), new HashMap<String, String>());
			cloakMap.put(p.getName(), new HashMap<String, String>());
			titleMap.put(p.getName(), new HashMap<Integer, String>());
		}
	}
	
	public void onPluginDisable() {
		resetAll();
	}
	
	private void checkUrl(String Url) {
		if (Url == null || Url.length() < 5) {
			throw new UnsupportedOperationException("Invalid URL");
		}
		if (!Url.substring(Url.length() - 4, Url.length()).equalsIgnoreCase(".png")) {
			throw new UnsupportedOperationException("All skins must be a PNG image");
		}
		if (Url.length() > 255) {
			throw new UnsupportedOperationException("All Url's must be shorter than 256 characters");
		}
	}
	
	private String getDefaultSkin(HumanEntity human) {
		return "http://s3.amazonaws.com/MinecraftSkins/" + human.getName() + ".png";
	}
	
	private String getDefaultCloak(HumanEntity human) {
		return "http://s3.amazonaws.com/MinecraftCloaks/" + human.getName() + ".png";
	}

	@Override
	public void setPlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target, String title) {
		HashMap<Integer, String> map = titleMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<Integer, String>();
			titleMap.put(viewingPlayer.getName(), map);
		}
		map.put(target.getEntityId(), title);
		SpoutCraftPlayer player = (SpoutCraftPlayer) viewingPlayer;
		if (player.getVersion() > 4)
			player.sendPacket(new PacketEntityTitle(target.getEntityId(), title));
	}

	@Override
	public void setGlobalTitle(LivingEntity target, String title) {
		globalTitleMap.put(target.getEntityId(), title);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			HashMap<Integer, String> map = titleMap.get(p.getName());
			if (map == null) {
				map = new HashMap<Integer, String>();
				titleMap.put(p.getName(), map);
			}
			map.put(target.getEntityId(), title);
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4)
				player.sendPacket(new PacketEntityTitle(target.getEntityId(), title));
		}
	}
	
	@Override
	public void hidePlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
	   setPlayerTitle(viewingPlayer, target, "[hide]");
	}
	
	@Override
	public void hideGlobalTitle(LivingEntity target) {
		setGlobalTitle(target, "[hide]");
	}

	@Override
	public void resetPlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
		HashMap<Integer, String> map = titleMap.get(viewingPlayer.getName());
		if (map == null) {
			map = new HashMap<Integer, String>();
			titleMap.put(viewingPlayer.getName(), map);
		}
		map.remove(target.getEntityId());
		SpoutCraftPlayer player = (SpoutCraftPlayer)viewingPlayer;
		if (player.getVersion() > 4)
			player.sendPacket(new PacketEntityTitle(target.getEntityId(), "reset"));
	}

	@Override
	public void resetGlobalTitle(LivingEntity target) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			globalTitleMap.remove(target.getEntityId());
			HashMap<Integer, String> map = titleMap.get(p.getName());
			if (map == null) {
				map = new HashMap<Integer, String>();
				titleMap.put(p.getName(), map);
			}
			map.remove(target.getEntityId());
			SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getContribPlayer(p);
			if (player.getVersion() > 4){
				player.sendPacket(new PacketEntityTitle(target.getEntityId(), "reset"));
			}
		}
	}

	@Override
	public void resetAllTitles() {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (LivingEntity lv : w.getLivingEntities()) {
				resetGlobalTitle(lv);
			}
		}
	}
}
