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

public abstract class AbstractControl extends AbstractInline implements Control {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 3L;
	private boolean focus = false;
	private boolean enabled = true;
	private Color color = new Color(0.878F, 0.878F, 0.878F);
	private Color disabledColor = new Color(0.625F, 0.625F, 0.625F);

	public AbstractControl() {
	}

	public AbstractControl(int width, int height) {
		super(width, height);
	}

	public AbstractControl(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 12;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setEnabled(input.readBoolean());
		setColor(PacketUtil.readColor(input));
		setDisabledColor(PacketUtil.readColor(input));
		setFocus(input.readBoolean());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isEnabled());
		PacketUtil.writeColor(output, getColor());
		PacketUtil.writeColor(output, getDisabledColor());
		output.writeBoolean(isFocus());
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Control setEnabled(boolean enable) {
		if (isEnabled() != enable) {
			enabled = enable;
			autoDirty();
		}
		return this;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Control setColor(Color color) {
		if (color != null && !getColor().equals(color)) {
			this.color = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public Color getDisabledColor() {
		return disabledColor;
	}

	@Override
	public Control setDisabledColor(Color color) {
		if (color != null && !getDisabledColor().equals(color)) {
			this.disabledColor = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public boolean isFocus() {
		return focus;
	}

	@Override
	public Control setFocus(boolean focus) {
		if (isFocus() != focus) {
			this.focus = focus;
			autoDirty();
		}
		return this;
	}

	@Override
	public Control copy() {
		return ((Control) super.copy()).setEnabled(isEnabled()).setColor(getColor()).setDisabledColor(getDisabledColor());
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
