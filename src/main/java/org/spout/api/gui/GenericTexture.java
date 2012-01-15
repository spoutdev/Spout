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

public class GenericTexture extends AbstractInline implements Texture {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 3L;
	private String url = null;
	private boolean drawAlpha = false;
	private int top = -1;
	private int left = -1;

	public GenericTexture() {
	}

	public GenericTexture(String url) {
		this.url = url;
	}

	public GenericTexture(int width, int height) {
		super(width, height);
	}

	public GenericTexture(int width, int height, String url) {
		super(width, height);
		this.url = url;
	}

	public GenericTexture(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	public GenericTexture(int X, int Y, int width, int height, String url) {
		super(X, Y, width, height);
		this.url = url;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.TEXTURE;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + PacketUtil.getNumBytes(getUrl()) + 5;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setUrl(PacketUtil.readString(input)); // String
		this.setDrawAlphaChannel(input.readBoolean()); // 0 + 1 = 1
		setTop(input.readShort()); // 1 + 2 = 3
		setLeft(input.readShort()); // 3 + 2 = 5
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getUrl()); // String
		output.writeBoolean(isDrawingAlphaChannel()); // 0 + 1 = 1
		output.writeShort(top); // 1 + 2 = 3
		output.writeShort(left); // 3 + 2 = 5
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public Texture setUrl(String Url) {
		if ((getUrl() != null && !getUrl().equals(Url)) || (getUrl() == null && Url != null)) {
			this.url = Url;
			autoDirty();
		}
		return this;
	}

	@Override
	public Texture copy() {
		return ((Texture) super.copy()).setUrl(getUrl()).setDrawAlphaChannel(isDrawingAlphaChannel());
	}

	@Override
	public boolean isDrawingAlphaChannel() {
		return drawAlpha;
	}

	@Override
	public Texture setDrawAlphaChannel(boolean draw) {
		if (isDrawingAlphaChannel() != draw) {
			drawAlpha = draw;
			autoDirty();
		}
		return this;
	}

	@Override
	public Texture setTop(int top) {
		if (getTop() != top) {
			this.top = top;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public Texture setLeft(int left) {
		if (getLeft() != left) {
			this.left = left;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
