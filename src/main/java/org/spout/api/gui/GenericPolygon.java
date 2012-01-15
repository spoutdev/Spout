/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.spout.api.util.Color;

public class GenericPolygon extends AbstractInline implements Polygon {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 0L;
	private List<Pair<Point, Color>> points = new LinkedList<Pair<Point, Color>>();
	private Color lastColor = null;

	public GenericPolygon() {
	}

	public GenericPolygon(int width, int height) {
		super(width, height);
	}

	public GenericPolygon(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	public WidgetType getType() {
		return WidgetType.POLYGON;
	}

	public void render() {
		/*
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(770, 771);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		MinecraftTessellator t = Spoutcraft.getTessellator();
		GL11.glTranslated(getActualX(), getActualY(), 0);
		t.startDrawingQuads();
		for(Pair<Point, Color> point:points) {
		Point p = point.getLeft();
		Color c = point.getRight();
		t.setColorRGBAFloat(c.getRedF(), c.getGreenF(), c.getBlueF(), c.getAlphaF());
		t.addVertex(p.getX(), p.getY(), 0);

		}
		t.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		 */
	}

	public Polygon addPoint(int x, int y) throws IllegalStateException {
		return addPoint(new Point(x, y));
	}

	public Polygon addPoint(Point p) throws IllegalStateException {
		if (lastColor == null) {
			throw new IllegalStateException("No color set.");
		}
		return addPoint(p, lastColor);
	}

	public Polygon addPoint(Point p, Color c) {
		if (lastColor == null || !c.equals(lastColor)) {
			lastColor = c.clone();
		}
		Pair<Point, Color> toAdd = Pair.of(p, lastColor);
		points.add(toAdd);
		return this;
	}

	public Polygon addPoint(int x, int y, Color c) {
		return addPoint(new Point(x, y), c);
	}

	public LinkedList<Point> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedList<Color> getColors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}
}
