/*
 * This file is part of SpoutAPI (http://getspout.org/).
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
package org.getspout.api.plugin.exceptions;

public class InvalidPluginException extends Exception {

	private static final long serialVersionUID = 5907555277800661037L;
	private final Throwable cause;
	private final String message;
	
	public InvalidPluginException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}

	public InvalidPluginException(Throwable cause) {
		this(null, cause);
	}
	
	public InvalidPluginException(String message) {
		this(message, null);
	}

	public InvalidPluginException() {
		this(null, null);
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return message;
	}

}
