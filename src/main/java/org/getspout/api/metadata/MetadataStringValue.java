/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.metadata;

import org.getspout.api.plugin.Plugin;

public class MetadataStringValue implements MetadataValue {
	private String data;
	public MetadataStringValue(String val){
		this.data = val;
	}
	public MetadataStringValue(int val){
		this.data = Integer.toString(val);
	}
	public MetadataStringValue(double val){
		this.data = Double.toString(val);
	}
	public MetadataStringValue(boolean val){
		this.data = Boolean.toString(val);
	}
	public int asInt() throws MetadataConversionException {
		int r  = 0;
		try{
			r = Integer.parseInt(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public double asDouble() throws MetadataConversionException {
		double r  = 0;
		try{
			r = Double.parseDouble(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public boolean asBoolean() throws MetadataConversionException {
		boolean r  = false;
		try{
			r = Boolean.parseBoolean(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public String asString() {

		return data;
	}

	public Plugin getOwningPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

}