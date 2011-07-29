package org.bukkitcontrib;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldServer;
import net.minecraft.server.PlayerList;
import net.minecraft.server.PlayerManager;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;

import org.bukkitcontrib.util.ReflectUtil;

public class ContribPlayerManager extends net.minecraft.server.PlayerManager {

	private int f;
	private final int[][] g = new int[][] { { 1, 0}, { 0, 1}, { -1, 0}, { 0, -1}};
	public static void replacePlayerManager(WorldServer world) {
		if (!world.manager.getClass().equals(ContribPlayerManager.class)) {
			world.manager = new ContribPlayerManager(world.manager);
			ContribPlayerInstance.replacePlayerInstances((ContribPlayerManager) world.manager);
			//System.out.println("World Manager Replaced!");
		}
		//System.out.println("Loaded Custom World Manager!");
	}

	@SuppressWarnings("rawtypes")
	public ContribPlayerManager(PlayerManager manager) {
		super(null, 0, 10);
		this.managedPlayers = manager.managedPlayers;
		ReflectUtil.transferField(manager, this, "b");
		ReflectUtil.transferField(manager, this, "c");
		ReflectUtil.transferField(manager, this, "server");
		ReflectUtil.transferField(manager, this, "e");
		ReflectUtil.transferField(manager, this, "f");
		ReflectUtil.transferField(manager, this, "g");
		try {
			Field f = PlayerManager.class.getDeclaredField("f");
			f.setAccessible(true);
			this.f = (Integer) f.get(this);
			
			f = PlayerManager.class.getDeclaredField("b");
			f.setAccessible(true);
			PlayerList list = (PlayerList) f.get(manager);
			f = PlayerList.class.getDeclaredField("a");
			f.setAccessible(true);
			Object[] entries = (Object[]) f.get(list);
			Class clazz = Class.forName("net.minecraft.server.PlayerListEntry");
			f = clazz.getDeclaredField("b");
			f.setAccessible(true);
			for (Object o : entries) {
				Object entry = o != null ? f.get(o) : null;
				//if (entry != null) System.out.println("Found PLE: " + entry);
				if (entry != null) {
					f.set(o, new ContribPlayerInstance(this, entry));
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	   @SuppressWarnings("rawtypes")
	public void flush() {
		   List c = null;
		   try {
			   Field f = PlayerManager.class.getDeclaredField("c");
			   f.setAccessible(true);
			   c = (List) f.get(this);
		   }
		   catch (Exception e) {
			   e.printStackTrace();
		   }
			for (int i = 0; i < c.size(); ++i) {
				((ContribPlayerInstance) c.get(i)).a();
			}

			c.clear();
			try {
			   Field f = PlayerManager.class.getDeclaredField("c");
			   f.setAccessible(true);
			   f.set(this, c);
		   }
		   catch (Exception e) {
			   e.printStackTrace();
		   }
		}
	
	public void flagDirty(int i, int j, int k) {
		int l = i >> 4;
		int i1 = k >> 4;
		ContribPlayerInstance playerinstance = this.a(l, i1, false);

		if (playerinstance != null) {
			playerinstance.a(i & 15, j, k & 15);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ContribPlayerInstance a(int i, int j, boolean flag) {
		long k = (long) i + 2147483647L | (long) j + 2147483647L << 32;
		Object result = null;
		try {
			Field playerlist = PlayerManager.class.getDeclaredField("b");
			playerlist.setAccessible(true);
			PlayerList list = (PlayerList) playerlist.get(this);
			Class[] params = { long.class };
			Method a = PlayerList.class.getDeclaredMethod("a", params);
			a.setAccessible(true);
			result = a.invoke(list, k);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		Class clazz = null;
		try {
			clazz = Class.forName("net.minecraft.server.PlayerInstance");
		}
		catch (Exception e) { e.printStackTrace(); }
		if (clazz != null && result != null && clazz.isAssignableFrom(result.getClass())) {
			result = new ContribPlayerInstance(this, result);
		}
		if (result != null && !result.getClass().equals(ContribPlayerInstance.class)) {
			result = new ContribPlayerInstance(this, result);
		}
	
		if (result == null && flag) {
			result = new ContribPlayerInstance(this, i, j);
			try {
				Field playerlist = PlayerManager.class.getDeclaredField("b");
				playerlist.setAccessible(true);
				PlayerList list = (PlayerList) playerlist.get(this);
				Class[] params = { long.class, Object.class };
				Method a = PlayerList.class.getDeclaredMethod("a", params);
				a.setAccessible(true);
				a.invoke(list, k, result);
			}
			catch (Exception e) { e.printStackTrace(); }
		}

		return (ContribPlayerInstance) result;
	}
	
	@SuppressWarnings("unchecked")
	public void addPlayer(EntityPlayer entityplayer) {
		int i = (int) entityplayer.locX >> 4;
		int j = (int) entityplayer.locZ >> 4;

		entityplayer.d = entityplayer.locX;
		entityplayer.e = entityplayer.locZ;
		int k = 0;
		int l = this.f;
		int i1 = 0;
		int j1 = 0;

		this.a(i, j, true).a(entityplayer);

		int k1;

		for (k1 = 1; k1 <= l * 2; ++k1) {
			for (int l1 = 0; l1 < 2; ++l1) {
				int[] aint = this.g[k++ % 4];

				for (int i2 = 0; i2 < k1; ++i2) {
					i1 += aint[0];
					j1 += aint[1];
					this.a(i + i1, j + j1, true).a(entityplayer);
				}
			}
		}

		k %= 4;

		for (k1 = 0; k1 < l * 2; ++k1) {
			i1 += this.g[k][0];
			j1 += this.g[k][1];
			this.a(i + i1, j + j1, true).a(entityplayer);
		}

		this.managedPlayers.add(entityplayer);
	}

	public void removePlayer(EntityPlayer entityplayer) {
		int i = (int) entityplayer.d >> 4;
		int j = (int) entityplayer.e >> 4;

		for (int k = i - this.f; k <= i + this.f; ++k) {
			for (int l = j - this.f; l <= j + this.f; ++l) {
				ContribPlayerInstance playerinstance = this.a(k, l, false);

				if (playerinstance != null) {
					playerinstance.b(entityplayer);
				}
			}
		}

		this.managedPlayers.remove(entityplayer);
	}
	
	private boolean a(int i, int j, int k, int l) {
		int i1 = i - k;
		int j1 = j - l;

		return i1 >= -this.f && i1 <= this.f ? j1 >= -this.f && j1 <= this.f : false;
	}
	
	@SuppressWarnings("unchecked")
	public void movePlayer(EntityPlayer entityplayer) {
		int i = (int) entityplayer.locX >> 4;
		int j = (int) entityplayer.locZ >> 4;
		double d0 = entityplayer.d - entityplayer.locX;
		double d1 = entityplayer.e - entityplayer.locZ;
		double d2 = d0 * d0 + d1 * d1;

		if (d2 >= 64.0D) {
			int k = (int) entityplayer.d >> 4;
			int l = (int) entityplayer.e >> 4;
			int i1 = i - k;
			int j1 = j - l;

			if (i1 != 0 || j1 != 0) {
				for (int k1 = i - this.f; k1 <= i + this.f; ++k1) {
					for (int l1 = j - this.f; l1 <= j + this.f; ++l1) {
						if (!this.a(k1, l1, k, l)) {
							this.a(k1, l1, true).a(entityplayer);
						}

						if (!this.a(k1 - i1, l1 - j1, i, j)) {
							ContribPlayerInstance playerinstance = this.a(k1 - i1, l1 - j1, false);

							if (playerinstance != null) {
								playerinstance.b(entityplayer);
							}
						}
					}
				}

				entityplayer.d = entityplayer.locX;
				entityplayer.e = entityplayer.locZ;

				// CraftBukkit start - send nearest chunks first
				if (i1 > 1 || i1 < -1 || j1 > 1 || j1 < -1) {
					final int x = i;
					final int z = j;
					List<ChunkCoordIntPair> chunksToSend = entityplayer.chunkCoordIntPairQueue;

					java.util.Collections.sort(chunksToSend, new java.util.Comparator<ChunkCoordIntPair>() {
						public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
							return Math.max(Math.abs(a.x - x), Math.abs(a.z - z)) - Math.max(Math.abs(b.x - x), Math.abs(b.z - z));
						}
					});
				}
				// CraftBukkit end
			}
		}
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
