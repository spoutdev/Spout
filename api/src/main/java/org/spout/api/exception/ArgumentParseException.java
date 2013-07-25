/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.exception;

/**
 * Thrown when an invalid argument is encountered, either from there not being enough input data or invalid input data
 */
public class ArgumentParseException extends CommandException {
    private final String command;
    private final String invalidArgName;
    private final String reason;
	private final boolean silenceable;
    private final String[] completions;
    public ArgumentParseException(String command, String invalidArgName, String reason, boolean silenceable, String... completions ) {
        super("/" + command + " [" + invalidArgName + "] invalid: " + reason); // /command [invalidArg] invalid: reason
        this.command = command;
        this.invalidArgName = invalidArgName;
        this.reason = reason;
		this.silenceable = silenceable;
        this.completions = completions;
    }

    public String getCommand() {
        return command;
    }

    public String getInvalidArgName() {
        return invalidArgName;
    }

    public String getReason() {
        return reason;
    }

	/**
	 * Return whether this error is ever appropriate to silence.
	 * Reasons for choosing either value are provided in the return value section.
	 *
	 * @return {@code true}: User has provided invalid syntax for argument (permanent failure)<br>
	 * 		   {@code false}: User has not provided enough arguments or other error (eg: tried to refer to offline player or unloaded world)
	 */
	public boolean isSilenceable() {
		return silenceable;
	}

    public String[] getCompletions() {
        return completions;
    }
}
