package net.minecraft.src;
//BukkitContrib HD Start
import com.pclewis.mcpatcher.mod.TileSize;
//BukkitContrib HD End
import net.minecraft.src.RenderEngine;
import org.lwjgl.opengl.GL11;

public class TextureFX {
//BukkitContrib HD Start
	public byte[] imageData = new byte[TileSize.int_numBytes];
//BukkitContrib HD End
	public int iconIndex;
	public boolean anaglyphEnabled = false;
	public int textureId = 0;
	public int tileSize = 1;
	public int tileImage = 0;


	public TextureFX(int var1) {
		this.iconIndex = var1;
	}

	public void onTick() {}

	public void bindImage(RenderEngine var1) {
		if(this.tileImage == 0) {
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1.getTexture("/terrain.png"));
		} else if(this.tileImage == 1) {
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1.getTexture("/gui/items.png"));
		}

	}
}
