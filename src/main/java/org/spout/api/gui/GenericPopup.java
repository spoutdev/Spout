/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.ClientOnly;
import org.spout.api.plugin.Plugin;

public class GenericPopup extends GenericScreen implements PopupScreen {

	protected boolean transparent = false;

	public GenericPopup() {
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 0;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 1;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTransparent(input.readBoolean());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isTransparent());
	}

	@Override
	public boolean isTransparent() {
		return transparent;
	}

	@Override
	public PopupScreen setTransparent(boolean value) {
		this.transparent = value;
		return this;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.PopupScreen;
	}

	@Override
	public boolean close() {
		if (getScreen() instanceof InGameScreen) {
			return ((InGameScreen) getScreen()).closePopup();
		}
		return false;
	}

	@Override
	public ScreenType getScreenType() {
		return ScreenType.CUSTOM_SCREEN;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
