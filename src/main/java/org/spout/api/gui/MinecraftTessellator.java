/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

public interface MinecraftTessellator {
	
	public int getMCTexture(String texture);

	public void draw();

	public void startDrawingQuads();

	public void startDrawing(int drawMode);
	
	public void setBrightness(int brightness);

	public void setTextureUV(double s, double t);

	public void setColorOpaqueFloat(float red, float green, float blue);

	public void setColorRGBAFloat(float red, float green, float blue, float alpha);

	public void setColorOpaque(int red, int green, int blue);

	public void setColorRGBA(int red, int green, int blue, int alpha);

	public void addVertexWithUV(double x, double y, double z, double s, double t);

	public void addVertex(double x, double y, double z);

	public void setColorOpaqueInt(int color);

	public void setColorRGBAInt(int color, int alpha);

	public void disableColor();

	public void setNormal(float x, float y, float z);

	public void setTranslation(double x, double y, double z);
}
