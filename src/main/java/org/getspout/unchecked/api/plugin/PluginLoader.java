/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.unchecked.api.plugin;

import java.io.File;
import java.util.regex.Pattern;

public abstract interface PluginLoader {

	public abstract Plugin loadAddon(File paramFile) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionException;

	public abstract Plugin loadAddon(File paramFile, boolean paramBoolean) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionException;

	public abstract Pattern[] getAddonFileFilters();

	public abstract void enableAddon(Plugin paramAddon);

	public abstract void disableAddon(Plugin paramAddon);

}
