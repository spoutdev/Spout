package org.bukkitcontrib;

import net.minecraft.server.WorldServer;
import net.minecraft.server.PlayerList;
import net.minecraft.server.PlayerManager;

import java.util.List;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;

import org.bukkitcontrib.util.ReflectUtil;

public class ContribPlayerManager extends net.minecraft.server.PlayerManager {

	public static void replacePlayerManager(WorldServer world) {
		if (!world.manager.getClass().equals(ContribPlayerManager.class)) {
			world.manager = new ContribPlayerManager(world.manager);
			ContribPlayerInstance.replacePlayerInstances((ContribPlayerManager) world.manager);
		}
	}

	public ContribPlayerManager(PlayerManager manager) {
		super(null, 0, 10);
		this.managedPlayers = manager.managedPlayers;
		ReflectUtil.transferField(manager, this, "b");
		ReflectUtil.transferField(manager, this, "c");
		ReflectUtil.transferField(manager, this, "server");
		ReflectUtil.transferField(manager, this, "e");
		ReflectUtil.transferField(manager, this, "f");
		ReflectUtil.transferField(manager, this, "g");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Object runMethod(String methodName, Class clazz, Object input, Class inputClass) {
		try {
			Method m = clazz.getDeclaredMethod(methodName, new Class[] { inputClass });
			m.setAccessible(true);
			return m.invoke(null, new Object[] { input });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static PlayerList a(ContribPlayerManager manager) {
		return (PlayerList) runMethod("a", PlayerManager.class, manager, PlayerManager.class);
	}

	@SuppressWarnings("rawtypes")
	public static List b(ContribPlayerManager manager) {
		return (List) runMethod("b", PlayerManager.class, manager, PlayerManager.class);
	}

}
