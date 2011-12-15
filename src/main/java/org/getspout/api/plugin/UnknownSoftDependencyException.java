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
package org.getspout.api.plugin;

import org.getspout.api.plugin.UnknownDependencyException;

public class UnknownSoftDependencyException extends UnknownDependencyException {

	private static final long serialVersionUID = 5721389371901775899L;

	public UnknownSoftDependencyException(Throwable throwable) {
		this(throwable, "Unknown soft dependency");
	}

	public UnknownSoftDependencyException(final String message) {
		this(null, message);
	}

	public UnknownSoftDependencyException(final Throwable throwable, final String message) {
		super(throwable, message);
	}

	public UnknownSoftDependencyException() {
		this(null, "Unknown dependency");
	}
}
