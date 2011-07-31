package org.getspout.gui;

import net.minecraft.src.*;

public class RenderItemCustom extends RenderItem{
	private double width, height, depth;
	
	public RenderItemCustom() {
		super();
		width = height = depth = 1;
	}
	public void setScale(double width, double height, double depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	@Override
	public void renderTexturedQuad(int var1, int var2, int var3, int var4, int var5, int var6) {
		float var7 = 0.0F;
		float var8 = 0.00390625F;
		float var9 = 0.00390625F;
		Tessellator var10 = Tessellator.instance;
		var10.startDrawingQuads();
		var10.addVertexWithUV(var1, (var2 + var6) * height, var7, (float)(var3 * var8), (float)((var4 + var6) * var9));
		var10.addVertexWithUV((var1 + var5) * height, (var2 + var6) * width, var7, (float)((var3 + var5) * var8), (float)((var4 + var6) * var9));
		var10.addVertexWithUV((var1 + var5) * height, var2, var7, (float)((var3 + var5) * var8), (float)(var4* var9));
		var10.addVertexWithUV(var1, var2, var7, (float)(var3 * var8), (float)(var4 * var9));
		var10.draw();
	}
}
