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
package org.getspout.api.plugin.security;

public class CommonSecurityManager extends SecurityManager implements Secure {

	private final double key;
	private boolean locked = false;
	
	public CommonSecurityManager(final double key) {
		this.key = key;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public boolean lock(double key) {
		boolean old = locked;
		if (key == this.key)
			locked = true;
		return old;
	}

	public void unlock(double key) {
		if (key == this.key)
			locked = false;
	}

}
