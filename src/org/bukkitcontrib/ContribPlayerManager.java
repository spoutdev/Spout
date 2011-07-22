package org.bukkitcontrib;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import net.minecraft.server.PlayerList;
//import net.minecraft.server.PlayerInstance;
import net.minecraft.server.PlayerManager;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;

public class ContribPlayerManager extends net.minecraft.server.PlayerManager {

	public static void replacePlayerManager(WorldServer world) {
		if (!(world.manager instanceof ContribPlayerManager)) {
			world.manager = new ContribPlayerManager(world.manager);
			System.out.println("Replaced " + world.worldData.name);
			ContribPlayerInstance.replacePlayerInstances((ContribPlayerManager) world.manager);
		}
	}

	public ContribPlayerManager(PlayerManager manager) {
		super(null, 0, 10);
		transferField(manager, this, "b");
		transferField(manager, this, "c");
		transferField(manager, this, "server");
		transferField(manager, this, "e");
		transferField(manager, this, "f");
		transferField(manager, this, "g");
	}

	private static void transferField(Object src, Object dest, String fieldName) {
		try {
			Class clazz = Class.forName("net.minecraft.server.PlayerManager");
			Field[] fields = clazz.getDeclaredFields();
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Object temp = field.get(src);
			field.set(dest, temp);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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

	public static List b(ContribPlayerManager manager) {
		return (List) runMethod("b", PlayerManager.class, manager, PlayerManager.class);
	}

}
