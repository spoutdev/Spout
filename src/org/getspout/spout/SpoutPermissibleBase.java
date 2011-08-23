/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.getspout.spout;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.permission.SpoutPermissible;

public class SpoutPermissibleBase implements SpoutPermissible {
	protected PermissibleBase perm;
	public SpoutPermissibleBase(PermissibleBase perm) {
		this.perm = perm;
	}
	
	public boolean isOp() {
		return perm.isOp();
	}
	
	@Override
	public void setOp(boolean value) {
		perm.setOp(value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perm.addAttachment(plugin, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return perm.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String  name, boolean value, int ticks) {
		return perm.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perm.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String perm) {
		return this.perm.hasPermission(perm);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return this.perm.hasPermission(perm);
	}

	@Override
	public boolean isPermissionSet(String name) {
		return perm.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return this.perm.isPermissionSet(perm);
	}

	@Override
	public void recalculatePermissions() {
		perm.recalculatePermissions();
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasAttachment(PermissionAttachment attachment) {
		try {
			Field attachments = PermissibleBase.class.getDeclaredField("attachments");
			attachments.setAccessible(true);
			List<PermissionAttachment> list = (List<PermissionAttachment>) attachments.get(perm);
			return list.contains(attachment);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		if (attachment == null) {
			return;
		}
		if (!hasAttachment(attachment)) {
			return;
		}
		perm.removeAttachment(attachment);
	}
}
