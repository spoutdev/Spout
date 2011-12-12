/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
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
package org.getspout.commons.addon;

import java.io.File;
import java.util.regex.Pattern;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.addon.InvalidAddonException;
import org.getspout.commons.addon.InvalidDescriptionException;
import org.getspout.commons.addon.UnknownDependencyException;

public abstract interface AddonLoader {

	public abstract Addon loadAddon(File paramFile) throws InvalidAddonException, InvalidAddonException, UnknownDependencyException, InvalidDescriptionException;

	public abstract Addon loadAddon(File paramFile, boolean paramBoolean) throws InvalidAddonException, InvalidAddonException, UnknownDependencyException, InvalidDescriptionException;

	public abstract Pattern[] getAddonFileFilters();

	public abstract void enableAddon(Addon paramAddon);

	public abstract void disableAddon(Addon paramAddon);

}
