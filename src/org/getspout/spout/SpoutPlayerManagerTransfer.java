package org.getspout.spout;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.PlayerList;
import net.minecraft.server.WorldServer;

import org.getspout.spout.util.ReflectUtil;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpoutPlayerManagerTransfer {

	public static SpoutPlayerManager copyPlayerManager(PlayerManager manager) {
		SpoutPlayerManager newPlayerManager = new SpoutPlayerManager(null, 0, 10);
		newPlayerManager.managedPlayers = manager.managedPlayers;
		ReflectUtil.transferField(manager, newPlayerManager, "b");
		ReflectUtil.transferField(manager, newPlayerManager, "c");
		ReflectUtil.transferField(manager, newPlayerManager, "server");
		ReflectUtil.transferField(manager, newPlayerManager, "e");
		ReflectUtil.transferField(manager, newPlayerManager, "f");
		ReflectUtil.transferField(manager, newPlayerManager, "g");
		return newPlayerManager;
	}

	public static SpoutPlayerInstance copyPlayerInstance(SpoutPlayerManager manager, Object playerInstance) {
		SpoutPlayerInstance newPlayerInstance = new SpoutPlayerInstance(manager, 0, 0);
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "b");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "chunkX");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "chunkZ");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "location");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "dirtyBlocks");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "dirtyCount");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "h");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "i");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "j");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "k");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "l");
		ReflectUtil.transferField(playerInstance, newPlayerInstance, "m");
		return newPlayerInstance;
	}

	public static SpoutPlayerInstance cachedCopyPlayerInstance(SpoutPlayerManager manager, Object playerInstance, List<Object> oldPlayerInstances,
			List<SpoutPlayerInstance> newPlayerInstances) {
		if (playerInstance == null) {
			return null;
		}

		// Ensure player instances in PlayerList and PlayerManager.c still match
		for (int i = 0; i < oldPlayerInstances.size(); i++) {
			if (playerInstance == oldPlayerInstances.get(i)) {
				return newPlayerInstances.get(i);
			}
		}

		SpoutPlayerInstance newPlayerInstance = copyPlayerInstance(manager, playerInstance);
		oldPlayerInstances.add(playerInstance);
		newPlayerInstances.add(newPlayerInstance);
		return newPlayerInstance;
	}

	public static void replacePlayerManager(WorldServer world) {
		world.manager = copyPlayerManager(world.manager);
		replacePlayerInstances((SpoutPlayerManager) world.manager);
	}

	public static void replacePlayerInstances(SpoutPlayerManager manager) {

		ArrayList<Object> oldPlayerInstances = new ArrayList<Object>();
		ArrayList<SpoutPlayerInstance> newPlayerInstances = new ArrayList<SpoutPlayerInstance>();

		List<Object> playerInstances;
		try {
			Field fc = manager.getClass().getDeclaredField("c");
			fc.setAccessible(true);
			playerInstances = (List<Object>) fc.get(manager);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		List<Object> updatedPlayerInstances = new ArrayList<Object>();
		for (Object instance : playerInstances) {
			updatedPlayerInstances.add(cachedCopyPlayerInstance(manager, instance, oldPlayerInstances, newPlayerInstances));
		}
		try {
			Class clazz = Class.forName("net.minecraft.server.PlayerManager");
			Field fc = clazz.getDeclaredField("c");
			fc.setAccessible(true);
			fc.set(manager, updatedPlayerInstances);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		try {
			Field pmb = manager.getClass().getDeclaredField("b");
			pmb.setAccessible(true);
			PlayerList playerList = (PlayerList) pmb.get(manager);
			Field pla = playerList.getClass().getDeclaredField("a");
			pla.setAccessible(true);
			Object[] playerListEntries = (Object[]) pla.get(playerList);
			for (Object o : playerListEntries) {
				while (o != null) {
					Field pleb = o.getClass().getDeclaredField("b");
					pleb.setAccessible(true);
					Object oldPlayerInstance = pleb.get(o);
					if (oldPlayerInstance != null) {
						SpoutPlayerInstance cpi = cachedCopyPlayerInstance(manager, oldPlayerInstance, oldPlayerInstances, newPlayerInstances);
						pleb.set(o, cpi);
					}
					Field plec = o.getClass().getDeclaredField("c");
					plec.setAccessible(true);
					o = plec.get(o);
				}
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

}
