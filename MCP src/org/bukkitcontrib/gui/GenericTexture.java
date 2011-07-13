package org.bukkitcontrib.gui;
//BukkitContrib
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;
import org.bukkitcontrib.packet.PacketUtil;

public class GenericTexture extends GenericWidget implements Texture {
	protected String Url = null;
	public GenericTexture() {
		
	}
	
	public GenericTexture(String Url) {
		this.Url = Url;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.Texture;
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + getUrl().length();
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setUrl(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getUrl());
	}
	
	public int getRotation() {
		return 360;
	}

	@Override
	public void render() {
		String path = CustomTextureManager.getTextureFromUrl(getUrl());
		if (path == null) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 0);
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, BukkitContrib.getGameInstance().renderEngine.getTexture(path));
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
		int i = 150;//getUpperRightX();
		int j = 150;//getUpperRightY();
		//double rotation = -1 * 
        tessellator.addVertexWithUV(0.0D, j, 0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(i, j, 0D, -0.5D, 0.0D);
        tessellator.addVertexWithUV(i, 0.0D, 0D, -0.5D, -0.5D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0D, 0.0D, -0.5D);
        tessellator.draw();
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		GL11.glPopMatrix();
	}
	
	private String getFileName() {
		int slashIndex = getUrl().lastIndexOf('/');
		int dotIndex = getUrl().lastIndexOf('.', slashIndex);
		if (dotIndex == -1 || dotIndex < slashIndex) {
				return getUrl().substring(slashIndex + 1).replaceAll("%20", " ");
		}
		return getUrl().substring(slashIndex + 1, dotIndex).replaceAll("%20", " ");
	 }
	
	public void drawTexturedModalRect(int i, int j, int k, int l, int i1, int j1)
	 {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		int zLevel = -90;
		tessellator.addVertexWithUV(i + 0, j + j1, zLevel, (float)(k + 0) * f, (float)(l + j1) * f1);
		tessellator.addVertexWithUV(i + i1, j + j1, zLevel, (float)(k + i1) * f, (float)(l + j1) * f1);
		tessellator.addVertexWithUV(i + i1, j + 0, zLevel, (float)(k + i1) * f, (float)(l + 0) * f1);
		tessellator.addVertexWithUV(i + 0, j + 0, zLevel, (float)(k + 0) * f, (float)(l + 0) * f1);
		tessellator.draw();
	 }

	@Override
	public String getUrl() {
		return Url;
	}

	@Override
	public Texture setUrl(String Url) {
		if (getUrl() != null) {
			//BukkitContrib.getGameInstance().renderEngine.releaseImageData(getUrl());
		}
		this.Url = Url;
		if (getUrl() != null) {
			CustomTextureManager.downloadTexture(Url);
			//BukkitContrib.getGameInstance().renderEngine.obtainImageData(getUrl(), new ImageBufferDownload());
		}
		return this;
	}

}
