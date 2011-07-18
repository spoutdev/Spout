package com.pclewis.mcpatcher.mod;

import com.pclewis.mcpatcher.MCPatcherUtils;
import com.pclewis.mcpatcher.mod.CustomAnimation;
import com.pclewis.mcpatcher.mod.TileSize;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import javax.imageio.stream.FileImageInputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ColorizerFoliage;
import net.minecraft.src.ColorizerGrass;
import net.minecraft.src.ColorizerWater;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.TextureCompassFX;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TextureFlamesFX;
import net.minecraft.src.TextureLavaFX;
import net.minecraft.src.TextureLavaFlowFX;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.TexturePackDefault;
import net.minecraft.src.TexturePortalFX;
import net.minecraft.src.TextureWatchFX;
import net.minecraft.src.TextureWaterFX;
import net.minecraft.src.TextureWaterFlowFX;

public class TextureUtils {

	public static Minecraft minecraft;
	private static boolean animatedFire = true;
	private static boolean animatedLava = true;
	private static boolean animatedWater = true;
	private static boolean animatedPortal = true;
	private static boolean customFire = true;
	private static boolean customLava = true;
	private static boolean customWater = true;
	private static boolean customPortal = true;
	public static final int LAVA_STILL_TEXTURE_INDEX = 237;
	public static final int LAVA_FLOWING_TEXTURE_INDEX = 238;
	public static final int WATER_STILL_TEXTURE_INDEX = 205;
	public static final int WATER_FLOWING_TEXTURE_INDEX = 206;
	public static final int FIRE_E_W_TEXTURE_INDEX = 31;
	public static final int FIRE_N_S_TEXTURE_INDEX = 47;
	public static final int PORTAL_TEXTURE_INDEX = 14;
	private static HashMap expectedColumns = new HashMap();
	private static boolean useTextureCache = false;
	private static TexturePackBase lastTexturePack = null;
	private static HashMap cache = new HashMap();


	public static boolean setTileSize() {
		//MCPatcherUtils.log("\nchanging skin to %s", new Object[]{getTexturePackName(getSelectedTexturePack())});
		int var0 = getTileSize();
		if(var0 == TileSize.int_size) {
			//MCPatcherUtils.log("tile size %d unchanged", new Object[]{Integer.valueOf(var0)});
			return false;
		} else {
			//MCPatcherUtils.log("setting tile size to %d (was %d)", new Object[]{Integer.valueOf(var0), Integer.valueOf(TileSize.int_size)});
			TileSize.setTileSize(var0);
			return true;
		}
	}

	public static void setFontRenderer() {
		//MCPatcherUtils.log("setFontRenderer()", new Object[0]);
		minecraft.fontRenderer.initialize(minecraft.gameSettings, "/font/default.png", minecraft.renderEngine);
	}

	public static void registerTextureFX(List var0, TextureFX var1) {
		TextureFX var2 = refreshTextureFX(var1);
		if(var2 != null) {
			//MCPatcherUtils.log("registering new TextureFX class %s", new Object[]{var1.getClass().getName()});
			var0.add(var2);
			var2.onTick();
		}

	}

	private static TextureFX refreshTextureFX(TextureFX var0) {
		if(!(var0 instanceof TextureCompassFX) && !(var0 instanceof TextureWatchFX) && !(var0 instanceof TextureLavaFX) && !(var0 instanceof TextureLavaFlowFX) && !(var0 instanceof TextureWaterFX) && !(var0 instanceof TextureWaterFlowFX) && !(var0 instanceof TextureFlamesFX) && !(var0 instanceof TexturePortalFX) && !(var0 instanceof CustomAnimation)) {
			Class var1 = var0.getClass();

			for(int var2 = 0; var2 < 3; ++var2) {
				try {
					Constructor var3;
					switch(var2) {
					case 0:
						var3 = var1.getConstructor(new Class[]{Minecraft.class, Integer.TYPE});
						return (TextureFX)var3.newInstance(new Object[]{minecraft, Integer.valueOf(TileSize.int_size)});
					case 1:
						var3 = var1.getConstructor(new Class[]{Minecraft.class});
						return (TextureFX)var3.newInstance(new Object[]{minecraft});
					case 2:
						var3 = var1.getConstructor(new Class[0]);
						return (TextureFX)var3.newInstance(new Object[0]);
					}
				} catch (NoSuchMethodException var5) {
					;
				} catch (IllegalAccessException var6) {
					;
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}

			if(var0.imageData.length != TileSize.int_numBytes) {
				//MCPatcherUtils.log("resizing %s buffer from %d to %d bytes", new Object[]{var1.getName(), Integer.valueOf(var0.imageData.length), Integer.valueOf(TileSize.int_numBytes)});
				var0.imageData = new byte[TileSize.int_numBytes];
			}

			return var0;
		} else {
			return null;
		}
	}

	public static void refreshTextureFX(List var0) {
		//MCPatcherUtils.log("refreshTextureFX()", new Object[0]);
		ArrayList var1 = new ArrayList();
		Iterator var2 = var0.iterator();

		while(var2.hasNext()) {
			TextureFX var3 = (TextureFX)var2.next();
			TextureFX var4 = refreshTextureFX(var3);
			if(var4 != null) {
				var1.add(var4);
			}
		}

		var0.clear();
		var0.add(new TextureCompassFX(minecraft));
		var0.add(new TextureWatchFX(minecraft));
		TexturePackBase var6 = getSelectedTexturePack();
		boolean var7 = var6 == null || var6 instanceof TexturePackDefault;
		if(!var7 && customLava) {
			var0.add(new CustomAnimation(237, 0, 1, "lava_still", -1, -1));
			var0.add(new CustomAnimation(238, 0, 2, "lava_flowing", 3, 6));
		} else if(animatedLava) {
			var0.add(new TextureLavaFX());
			var0.add(new TextureLavaFlowFX());
		}

		if(!var7 && customWater) {
			var0.add(new CustomAnimation(205, 0, 1, "water_still", -1, -1));
			var0.add(new CustomAnimation(206, 0, 2, "water_flowing", 0, 0));
		} else if(animatedWater) {
			var0.add(new TextureWaterFX());
			var0.add(new TextureWaterFlowFX());
		}

		if(!var7 && customFire && hasResource("/custom_fire_e_w.png") && hasResource("/custom_fire_n_s.png")) {
			var0.add(new CustomAnimation(47, 0, 1, "fire_n_s", 2, 4));
			var0.add(new CustomAnimation(31, 0, 1, "fire_e_w", 2, 4));
		} else if(animatedFire) {
			var0.add(new TextureFlamesFX(0));
			var0.add(new TextureFlamesFX(1));
		}

		if(!var7 && customPortal && hasResource("/custom_portal.png")) {
			var0.add(new CustomAnimation(14, 0, 1, "portal", -1, -1));
		} else if(animatedPortal) {
			var0.add(new TexturePortalFX());
		}

		Iterator var8 = var1.iterator();

		TextureFX var5;
		while(var8.hasNext()) {
			var5 = (TextureFX)var8.next();
			var0.add(var5);
		}

		var8 = var0.iterator();

		while(var8.hasNext()) {
			var5 = (TextureFX)var8.next();
			var5.onTick();
		}

		if(ColorizerWater.waterBuffer != ColorizerFoliage.foliageBuffer) {
			refreshColorizer(ColorizerWater.waterBuffer, "/misc/watercolor.png");
		}

		refreshColorizer(ColorizerGrass.grassBuffer, "/misc/grasscolor.png");
		refreshColorizer(ColorizerFoliage.foliageBuffer, "/misc/foliagecolor.png");
		System.gc();
	}

	public static TexturePackBase getSelectedTexturePack() {
		return minecraft == null?null:(minecraft.texturePackList == null?null:minecraft.texturePackList.selectedTexturePack);
	}

	public static String getTexturePackName(TexturePackBase var0) {
		return var0 == null?"Default":var0.texturePackFileName;
	}

	public static ByteBuffer getByteBuffer(ByteBuffer var0, byte[] var1) {
		var0.clear();
		int var2 = var0.capacity();
		int var3 = var1.length;
		if(var3 > var2 || var2 >= 4 * var3) {
			//MCPatcherUtils.log("resizing gl buffer from 0x%x to 0x%x", new Object[]{Integer.valueOf(var2), Integer.valueOf(var3)});
			var0 = GLAllocation.createDirectByteBuffer(var3);
		}

		var0.put(var1);
		var0.position(0).limit(var3);
		TileSize.int_glBufferSize = var3;
		return var0;
	}

	public static InputStream getResourceAsStream(TexturePackBase var0, String var1) {
		InputStream var2 = null;
		if(var0 != null) {
			try {
				var2 = var0.getResourceAsStream(var1);
			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}
		/*System.out.println("Texture Not in Texture Pack: " + var1);
		try {
			File test = new File(var1);
			System.out.println("Texture Not in Texture Pack: " + var1 + " File Exists: " + test.exists());
			if (test.exists()) {
				var2 = new FileImageInputStream(test);
				System.out.println("Found Texture In File: " + var1);
			}
		}
		catch (Exception e) {
			
		}*/
		
		if(var2 == null) {
			var2 = TextureUtils.class.getResourceAsStream(var1);
		}

		if(var2 == null && !var1.startsWith("/custom_")) {
			var2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(var1);
			//MCPatcherUtils.warn("falling back on thread class loader for %s: %s", new Object[]{var1, var2 == null?"failed":"success"});
		}

		return var2;
	}

	public static InputStream getResourceAsStream(String var0) {
		return getResourceAsStream(getSelectedTexturePack(), var0);
	}

	public static BufferedImage getResourceAsBufferedImage(TexturePackBase var0, String var1) throws IOException {
		BufferedImage var2 = null;
		boolean var3 = false;
		if(useTextureCache && var0 == lastTexturePack) {
			var2 = (BufferedImage)cache.get(var1);
			if(var2 != null) {
				var3 = true;
			}
		}

		if(var2 == null) {
			InputStream var4 = getResourceAsStream(var0, var1);
			if(var4 != null) {
				try {
					var2 = ImageIO.read(var4);
				} finally {
					MCPatcherUtils.close((Closeable)var4);
				}
			}
		}
		
		if(var2 == null) {
			System.out.println("Texture Not in Texture Pack: " + var1);
			FileImageInputStream imageStream = null;
			try {
				File test = new File(var1);
				System.out.println("Texture Not in Texture Pack: " + var1 + " File Exists: " + test.exists());
				if (test.exists()) {
					imageStream = new FileImageInputStream(test);
					var2 = ImageIO.read(imageStream);
					System.out.println("Found Texture In File: " + var1);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (imageStream != null){
					//imageStream.close();
				}
			}
		}

		if(var2 == null) {
			throw new IOException(var1 + " image is null");
		} else {
			if(useTextureCache && !var3 && var0 != lastTexturePack) {
				//MCPatcherUtils.log("clearing texture cache (%d items)", new Object[]{Integer.valueOf(cache.size())});
				cache.clear();
			}

			//MCPatcherUtils.log("opened %s %dx%d from %s", new Object[]{var1, Integer.valueOf(var2.getWidth()), Integer.valueOf(var2.getHeight()), var3?"cache":getTexturePackName(var0)});
			if(!var3) {
				Integer var8 = (Integer)expectedColumns.get(var1);
				if(var8 != null && var2.getWidth() != var8.intValue() * TileSize.int_size) {
					var2 = resizeImage(var2, var8.intValue() * TileSize.int_size);
				}

				if(useTextureCache) {
					lastTexturePack = var0;
					cache.put(var1, var2);
				}
			}

			return var2;
		}
	}

	public static BufferedImage getResourceAsBufferedImage(String var0) throws IOException {
		return getResourceAsBufferedImage(getSelectedTexturePack(), var0);
	}

	public static int getTileSize(TexturePackBase var0) {
		int var1 = 0;
		Iterator var2 = expectedColumns.entrySet().iterator();

		while(var2.hasNext()) {
			Entry var3 = (Entry)var2.next();
			InputStream var4 = null;

			try {
				var4 = getResourceAsStream(var0, (String)var3.getKey());
				if(var4 != null) {
					BufferedImage var5 = ImageIO.read(var4);
					int var6 = var5.getWidth() / ((Integer)var3.getValue()).intValue();
					//MCPatcherUtils.log("  %s tile size is %d", new Object[]{var3.getKey(), Integer.valueOf(var6)});
					var1 = Math.max(var1, var6);
				}
			} catch (Exception var10) {
				var10.printStackTrace();
			} finally {
				MCPatcherUtils.close((Closeable)var4);
			}
		}

		return var1 > 0?var1:16;
	}

	public static int getTileSize() {
		return getTileSize(getSelectedTexturePack());
	}

	public static boolean hasResource(TexturePackBase var0, String var1) {
		InputStream var2 = getResourceAsStream(var0, var1);
		boolean var3 = var2 != null;
		MCPatcherUtils.close((Closeable)var2);
		return var3;
	}

	public static boolean hasResource(String var0) {
		return hasResource(getSelectedTexturePack(), var0);
	}

	private static BufferedImage resizeImage(BufferedImage var0, int var1) {
		int var2 = var0.getHeight() * var1 / var0.getWidth();
		//MCPatcherUtils.log("  resizing to %dx%d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2)});
		BufferedImage var3 = new BufferedImage(var1, var2, 2);
		Graphics2D var4 = var3.createGraphics();
		var4.drawImage(var0, 0, 0, var1, var2, (ImageObserver)null);
		return var3;
	}

	private static void refreshColorizer(int[] var0, String var1) {
		try {
			BufferedImage var2 = getResourceAsBufferedImage(var1);
			if(var2 != null) {
				var2.getRGB(0, 0, 256, 256, var0, 0, 256);
			}
		} catch (IOException var3) {
			var3.printStackTrace();
		}

	}

	public static void setMinecraft(Minecraft var0) {
		minecraft = var0;
	}

	public static Minecraft getMinecraft() {
		return minecraft;
	}

	static {
		expectedColumns.put("/terrain.png", Integer.valueOf(16));
		expectedColumns.put("/gui/items.png", Integer.valueOf(16));
		expectedColumns.put("/misc/dial.png", Integer.valueOf(1));
		expectedColumns.put("/custom_lava_still.png", Integer.valueOf(1));
		expectedColumns.put("/custom_lava_flowing.png", Integer.valueOf(1));
		expectedColumns.put("/custom_water_still.png", Integer.valueOf(1));
		expectedColumns.put("/custom_water_flowing.png", Integer.valueOf(1));
		expectedColumns.put("/custom_fire_n_s.png", Integer.valueOf(1));
		expectedColumns.put("/custom_fire_e_w.png", Integer.valueOf(1));
		expectedColumns.put("/custom_portal.png", Integer.valueOf(1));
	}
}
