/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.render.Texture;
import org.spout.engine.resources.ClientTexture;

/**
 * A bin packing algorithm useful for merging multiple textures into one.
 * 
 * <p>
 * This algorithm is based on a Javascript algorithm found <a
 * href="http://codeincomplete.com/posts/2011/5/7/bin_packing/">on this
 * page</a>.
 * </p>
 */
public class RectangularPacker {
	private final Texture[] textures;
	private Node root;
	private Map<Texture, Node> fits;
	private Texture texture;

	private RectangularPacker(List<Texture> textures) {
		Collections.sort(textures, new Comparator<Texture>() {

			@Override
			public int compare(Texture a, Texture b) {
				int heightDiff = b.getHeight() - a.getHeight();
				if (heightDiff != 0) {
					return heightDiff;
				}

				return b.getWidth() - a.getWidth();
			}
		});
		this.textures = textures.toArray(new Texture[textures.size()]);
		
		fit();
		BufferedImage image = generateImage(true);
		this.texture = new ClientTexture(image);
	}

	private final void fit() {
		int len = textures.length;

		// Get initial size
		int w = len > 0 ? textures[0].getWidth() : 0;
		int h = len > 0 ? textures[0].getHeight() : 0;

		root = new Node(0, 0, w, h);

		fits = new HashMap<Texture, Node>();

		for (int n = 0; n < len; n++) {
			Texture texture = textures[n];
			Node fit;

			// Find where we can place the guy
			Node available = root.find(texture.getWidth(), texture.getHeight());
			if (available != null) {
				fit = available.split(texture.getWidth(), texture.getHeight());
			} else {
				// No room, we have to grow the root node
				fit = grow(texture.getWidth(), texture.getHeight());
			}
			fits.put(texture, fit);
		}
	}

	public Map<Texture, Node> getFits() {
		return fits; // Don't bother cloning the hashmap; this is a single-use
						// class since we can't do multiple returns
	}

	/**
	 * Generates the packed image.
	 * 
	 * @return The packed image.
	 */
	private BufferedImage generateImage(boolean powerOfTwo) {
		BufferedImage image;
		if (powerOfTwo) {
			int width = 1 << (32 - Integer.numberOfLeadingZeros(root.getWidth() - 1));
			int height = 1 << (32 - Integer.numberOfLeadingZeros(root.getHeight() - 1));
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		} else {
			image = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}

		Graphics2D canvas = image.createGraphics();

		for (Entry<Texture, Node> entry : fits.entrySet()) {
			canvas.drawImage(entry.getKey().getImage(), entry.getValue().getX(), entry.getValue().getY(), null);
		}

		return image;
	}
	
	/**
	 * Gets the resulting texture from the packer.
	 * 
	 * @return
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * Grows the root node to allow space for another image.
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	private Node grow(int w, int h) {
		// Ensure the image can actually fit
		boolean canGrowRight = h <= root.h;
		boolean canGrowDown = w <= root.w;

		// Keep it square
		boolean shouldGrowRight = canGrowRight && (root.w >= (root.h + h));
		boolean shouldGrowDown = canGrowDown && (root.h >= (root.w + w));

		if (shouldGrowRight) {
			return growRight(w, h);
		}

		if (shouldGrowDown) {
			return growDown(w, h);
		}

		if (canGrowRight) {
			return growRight(w, h);
		}

		if (canGrowDown) {
			return growDown(w, h);
		}

		return null;
	}

	private Node growRight(int w, int h) {
		Node oldRoot = root;
		root = new Node(0, 0, oldRoot.w + w, oldRoot.h);
		root.used = true;
		root.down = oldRoot;
		root.right = new Node(oldRoot.w, 0, w, oldRoot.h);

		Node available = root.find(w, h);
		if (available != null) {
			return available.split(w, h);
		}

		return null; // this happens when we don't sort.
	}

	private Node growDown(int w, int h) {
		Node oldRoot = root;
		root = new Node(0, 0, w, oldRoot.h);
		root.used = true;
		root.down = oldRoot;
		root.right = new Node(oldRoot.h, w, oldRoot.w, h);

		Node available = root.find(w, h);
		if (available != null) {
			return available.split(w, h);
		}

		return null; // this happens when we don't sort.
	}
	
	/**
	 * Packs the given textures into one Texture.
	 * 
	 * @param textures
	 * @return
	 */
	public static RectangularPacker packTextures(List<Texture> textures) {
		return new RectangularPacker(textures);
	}

	/**
	 * Helper object representing a node within the tree of packed rectangles.
	 */
	public static class Node {
		private boolean used = false;
		private int x;
		private int y;
		private int w;
		private int h;
		private Node down = null;
		private Node right = null;

		public Node(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}

		/**
		 * Finds either this node or a child node that can fit an image with the
		 * given width and height.
		 * 
		 * @param w
		 * @param h
		 * @return The next available node within this node, or null if a node
		 *         could not be found.
		 */
		public Node find(int w, int h) {
			if (used) {
				Node found = right.find(w, h);
				if (found == null) {
					return down.find(w, h);
				} else {
					return found;
				}
			}

			if ((w <= this.w) && (h <= this.h)) {
				return this;
			}

			return null;
		}

		/**
		 * Adds two children to the node.
		 * 
		 * @param w
		 * @param h
		 * @return
		 */
		public Node split(int w, int h) {
			used = true;
			down = new Node(x, y + h, this.w, this.h - h);
			right = new Node(x + w, y, this.w - w, this.h);
			return this;
		}
	}
}
