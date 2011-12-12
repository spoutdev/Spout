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

public class InvalidAddonException extends Exception {

	private static final long serialVersionUID = 2533779376266992189L;
	private final Throwable cause;

	public InvalidAddonException(Throwable throwable) {
		this.cause = throwable;
	}

	public InvalidAddonException() {
		this.cause = null;
	}

	public Throwable getCause() {
		return this.cause;
	}
}
