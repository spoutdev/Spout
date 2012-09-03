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
package org.spout.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.spout.api.Spout;

public abstract class BasicResourceLoader<E extends Resource> implements ResourceLoader<E> {
	@Override
	public abstract String getFallbackResourceName();
	
	@Override
	public abstract E getResource(InputStream stream);

	@Override
	public E getResource(URI resource) throws ResourceNotFoundException{
		InputStream s = Spout.getFilesystem().getResourceStream(resource);
		E r = getResource(s);
		try {
			s.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return r;
	}
}
