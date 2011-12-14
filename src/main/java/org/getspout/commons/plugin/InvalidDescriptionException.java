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
package org.getspout.commons.plugin;

public class InvalidDescriptionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5428943040337929212L;
	private final Throwable cause;
	private final String message;

	public InvalidDescriptionException(Throwable throwable) {
		this(throwable, "Invalid addon.yml");
	}

	public InvalidDescriptionException(String message) {
		this(null, message);
	}

	public InvalidDescriptionException(Throwable throwable, String message) {
		this.cause = null;
		this.message = message;
	}

	public InvalidDescriptionException() {
		this(null, "Invalid addon.yml");
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return this.message;
	}

}
