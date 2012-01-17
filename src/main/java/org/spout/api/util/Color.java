/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.util;

public final class Color {
	private byte red;
	private byte green;
	private byte blue;
	private byte alpha = (byte) 255;

	public Color(float r, float g, float b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
	}

	public Color(float r, float g, float b, float a) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(a);
	}

	public Color(byte r, byte g, byte b) {
		red = r;
		green = g;
		blue = b;
	}

	public Color(byte r, byte g, byte b, byte a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}

	public Color(int argb) {
		alpha = (byte) (argb >>> 24);
		red = (byte) ((argb & 0xFF0000) >>> 16);
		green = (byte) ((argb & 0xFF00) >>> 8);
		blue = (byte) (argb & 0xFF);
	}

	public float getRedF() {
		return red / 255F;
	}

	public float getGreenF() {
		return green / 255F;
	}

	public float getBlueF() {
		return blue / 255F;
	}

	public float getAlphaF() {
		return alpha / 255F;
	}

	public short getRedB() {
		return red;
	}

	public short getGreenB() {
		return green;
	}

	public short getBlueB() {
		return blue;
	}

	public short getAlphaB() {
		return alpha;
	}

	public int getRedI() {
		return red;
	}

	public int getGreenI() {
		return green;
	}

	public int getBlueI() {
		return blue;
	}

	public int getAlphaI() {
		return alpha;
	}

	public Color setRed(float r) {
		red = (byte) (r * 255);
		return this;
	}

	public Color setGreen(float g) {
		green = (byte) (g * 255);
		return this;
	}

	public Color setBlue(float b) {
		blue = (byte) (b * 255);
		return this;
	}

	public Color setAlpha(float a) {
		alpha = (byte) (a * 255);
		return this;
	}

	@Override
	public Color clone() {
		return new Color(red, green, blue, alpha);
	}

	@Override
	public String toString() {
		return "r: " + red + " g: " + green + " b: " + blue + " a: " + alpha;
	}
	@Override
	public boolean equals(Object other){
		if(this == other)return true;
		if(! (this instanceof Color)) return false;
		Color o = (Color) other;
		if(this.red == o.red && this.green == o.green && this.blue == o.blue && this.alpha == o.alpha) return true;
		return false;
	}

	public int toInt() {
		return (getAlphaI() & 0xFF) << 24 | (getRedI() & 0xFF) << 16 | (getGreenI() & 0xFF) << 8 | getBlueI() & 0xFF;
	}
	
	
	public static final Color Red = new Color(255, 0, 0);
	public static final Color Green = new Color(0, 255, 0);
	public static final Color Blue = new Color(0, 0, 255);
	public static final Color Black = new Color(0, 0, 0);
	public static final Color White = new Color(255, 255, 255);
	
	
	
	public static final Color invalid = new Color(254, 0, 254, 0);
	public static final Color override = new Color(253, 0, 253, 0);
	
}
