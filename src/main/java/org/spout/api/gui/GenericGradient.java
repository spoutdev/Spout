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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.ClientOnly;
import org.spout.api.packet.PacketUtil;
import org.spout.api.util.Color;

public class GenericGradient extends AbstractInline implements Gradient {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 2L;
	private Color color1 = new Color(0, 0, 0, 0), color2 = new Color(0, 0, 0, 0);
	private Orientation axis = Orientation.VERTICAL;

	public GenericGradient() {
	}

	public GenericGradient(Color both) {
		setColor(both);
	}

	public GenericGradient(Color top, Color bottom) {
		setColor(top, bottom);
	}

	public GenericGradient(int width, int height) {
		super(width, height);
	}

	public GenericGradient(int width, int height, Color both) {
		super(width, height);
		setColor(both);
	}

	public GenericGradient(int width, int height, Color top, Color bottom) {
		super(width, height);
		setColor(top, bottom);
	}

	public GenericGradient(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	public GenericGradient(int X, int Y, int width, int height, Color both) {
		super(X, Y, width, height);
		setColor(both);
	}

	public GenericGradient(int X, int Y, int width, int height, Color top, Color bottom) {
		super(X, Y, width, height);
		setColor(top, bottom);
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public Gradient setTopColor(Color color) {
		if (color != null && !getTopColor().equals(color)) {
			this.color1 = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public Gradient setBottomColor(Color color) {
		if (color != null && !getBottomColor().equals(color)) {
			this.color2 = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public Gradient setColor(Color color) {
		setTopColor(color);
		setBottomColor(color);
		return this;
	}

	@Override
	public Gradient setColor(Color top, Color bottom) {
		setTopColor(top);
		setBottomColor(bottom);
		return this;
	}

	@Override
	public Color getTopColor() {
		return this.color1;
	}

	@Override
	public Color getBottomColor() {
		return this.color2;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.GRADIENT;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 11;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTopColor(PacketUtil.readColor(input));
		this.setBottomColor(PacketUtil.readColor(input));
		this.setOrientation(Orientation.getOrientationFromId(input.readByte()));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeColor(output, getTopColor());
		PacketUtil.writeColor(output, getBottomColor());
		output.writeByte(getOrientation().getId());
	}

	@Override
	public Gradient copy() {
		return ((Gradient) super.copy()).setTopColor(getTopColor()).setBottomColor(getBottomColor());
	}

	@Override
	public Gradient setOrientation(Orientation axis) {
		if (getOrientation() != axis) {
			this.axis = axis;
			autoDirty();
		}
		return this;
	}

	@Override
	public Orientation getOrientation() {
		return axis;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
