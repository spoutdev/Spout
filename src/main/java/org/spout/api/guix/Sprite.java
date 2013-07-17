package org.spout.api.guix;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.RenderMaterial;

public class Sprite implements Comparable<Sprite> {
	private RenderMaterial material;
	private Rectangle source = Rectangle.ZERO;
	private Rectangle sprite = Rectangle.ZERO;
	private int zIndex = 0;
	private Color color = Color.WHITE;

	public RenderMaterial getMaterial() {
		return material;
	}

	public void setMaterial(RenderMaterial material) {
		this.material = material;
	}

	/**
	 * Sets the bounds of the source of the render part. This is commonly used
	 * for sprite sheets and should be left at zero for simple colored
	 * rectangles.
	 *
	 * @param source of part
	 */
	public void setSource(Rectangle source) {
		this.source = source;
	}

	/**
	 * Returns the bounds of the source of the render part. This is commonly
	 * used for sprite sheets and should be left at zero for simple colored
	 * rectangles.
	 *
	 * @return source of part
	 */
	public Rectangle getSource() {
		return source;
	}

	/**
	 * Sets the bounds of the actual sprite of the render material. This is
	 * used for specifying the actual visible size of the render part.
	 *
	 * @param sprite of render part
	 */
	public void setSprite(Rectangle sprite) {
		this.sprite = sprite;
	}

	/**
	 * Returns the bounds of the actual sprite of the render material. This is
	 * used for specifying the actual visible size of the render part.
	 *
	 * @return sprite of render part
	 */
	public Rectangle getSprite() {
		return sprite;
	}

	/**
	 * Returns the layer that this should be rendered on. Something with a
	 * higher z-index will be rendered on top of something with a lower z-index
	 * and something with a lower z-index will be rendered under the higher
	 * z-index part.
	 *
	 * @param zIndex of part
	 */
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	/**
	 * Returns the layer that this should be rendered on. Something with a
	 * higher z-index will be rendered on top of something with a lower z-index
	 * and something with a lower z-index will be rendered under the higher
	 * z-index part.
	 *
	 * @return z-index of part
	 */
	public int getZIndex() {
		return zIndex;
	}

	/**
	 * Returns the color of this part.
	 *
	 * @return color of part
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color of this part.
	 *
	 * @param color of part
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the vertices used for rendering the part.
	 *
	 * @return vertices of part
	 */
	public List<Vertex> getVertices() {
		List<Vertex> face = new ArrayList<Vertex>();
		Vector3 p1 = new Vector3(sprite.getX(), sprite.getY(), 0);
		Vector3 p2 = new Vector3(sprite.getX() + sprite.getWidth(), sprite.getY(), 0);
		Vector3 p3 = new Vector3(sprite.getX() + sprite.getWidth(), sprite.getY() - sprite.getHeight(), 0);
		Vector3 p4 = new Vector3(sprite.getX(), sprite.getY() - sprite.getHeight(), 0);

		Vector2 t1 = new Vector2(source.getX(), source.getY());
		Vector2 t2 = new Vector2(source.getX() + source.getWidth(), source.getY());
		Vector2 t3 = new Vector2(source.getX() + source.getWidth(), source.getY() + source.getHeight());
		Vector2 t4 = new Vector2(source.getX(), source.getY() + source.getHeight());

		face.add(Vertex.createVertexPositionTexture0(p1, t1));
		face.add(Vertex.createVertexPositionTexture0(p2, t2));
		face.add(Vertex.createVertexPositionTexture0(p3, t3));
		face.add(Vertex.createVertexPositionTexture0(p4, t4));

		for (Vertex v : face) {
			v.color = color;
		}

		return face;
	}

	@Override
	public int compareTo(Sprite arg0) {
		return arg0.getZIndex() - getZIndex();
	}
}
