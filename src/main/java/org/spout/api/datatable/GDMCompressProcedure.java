/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.datatable;

import gnu.trove.procedure.TIntObjectProcedure;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

import org.spout.api.util.VarInt;

class GDMCompressProcedure implements TIntObjectProcedure<AbstractData> {

	private final HashSet<String> stringKeys = new HashSet<String>();
	private final OutputStream stringOutput;
	private final OutputStream objectOutput;

	public int strings = 0;
	public int objects = 0;

	private final GenericDatatableMap map;

	public GDMCompressProcedure(GenericDatatableMap map, OutputStream stringOutput, OutputStream objectOutput) {
		this.map = map;
		this.stringOutput = stringOutput;
		this.objectOutput = objectOutput;
	}

	@Override
	public boolean execute(int a, AbstractData b) {
		String stringKey = map.getStringKey(a);
		if (stringKey != null) {
			if (stringKeys.add(stringKey)) {
				try {
					VarInt.writeInt(stringOutput, a);
					VarInt.writeString(stringOutput, stringKey);
					strings++;
				} catch (IOException e) {
					return false;
				}
			}
			try {
				b.output(objectOutput);
				objects++;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
};
