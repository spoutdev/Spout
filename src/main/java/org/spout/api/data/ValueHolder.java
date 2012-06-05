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
package org.spout.api.data;

import java.util.List;

public interface ValueHolder {
	/**
	 * Return this node's value as a boolean
	 *
	 * @return the boolean value
	 * @see #getBoolean(boolean)
	 * @see #getValue()
	 */
	public boolean getBoolean();

	/**
	 * Return this node's value as a boolean
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a boolean
	 * @return the boolean value
	 * @see #getValue(Object)
	 */
	public boolean getBoolean(boolean def);

	/**
	 * Return this node's value as a byte
	 *
	 * @return the byte value
	 * @see #getByte(byte)
	 * @see #getValue()
	 */
	public byte getByte();

	/**
	 * Return this value as a byte
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a byte
	 * @return the byte value
	 * @see #getValue(Object)
	 */
	public byte getByte(byte def);

	/**
	 * Return this node's value as a float
	 *
	 * @return the float value
	 * @see #getFloat(float)
	 * @see #getValue()
	 */
	public float getFloat();

	/**
	 * Return this value as a float
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a float
	 * @return the float value
	 * @see #getValue(Object)
	 */
	public float getFloat(float def);

	/**
	 * Return this node's value as a short
	 *
	 * @return the short value
	 * @see #getShort(short)
	 * @see #getValue()
	 */
	public short getShort();

	/**
	 * Return this value as a short
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a short
	 * @return the short value
	 * @see #getValue(Object)
	 */
	public short getShort(short def);

	/**
	 * Return this node's value as an integer
	 *
	 * @return the integer value
	 * @see #getInt(int)
	 * @see #getValue()
	 */
	public int getInt();

	/**
	 * Return this value as an integer
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't an integer
	 * @return the integer value
	 * @see #getValue(Object)
	 */
	public int getInt(int def);

	/**
	 * Return this node's value as a long
	 *
	 * @return the long value
	 * @see #getLong(long)
	 * @see #getValue()
	 */
	public long getLong();

	/**
	 * Return this node's value as a long
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a long
	 * @return the long value
	 * @see #getValue(Object)
	 */
	public long getLong(long def);

	/**
	 * Return this node's value as a double
	 *
	 * @return the double value
	 * @see #getDouble(double)
	 * @see #getValue()
	 */
	public double getDouble();

	/**
	 * Return this node's value as a double
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a double
	 * @return the double value
	 * @see #getValue(Object)
	 */
	public double getDouble(double def);

	/**
	 * Return this node's value as a String
	 *
	 * @return the String value
	 * @see #getString(String)
	 * @see #getValue()
	 */
	public String getString();

	/**
	 * Return this node's value as a String
	 *
	 * @param def The default value, returned if this node doesn't have a set value
	 * @return the String value
	 * @see #getValue(Object)
	 */
	public String getString(String def);

	/**
	 * Return this node's value
	 *
	 * @return the value
	 * @see #getValue(Object)
	 */
	public Object getValue();

	/**
	 * Return this node's value
	 *
	 * @param def The default value, returned if this node doesn't have a set value
	 * @return the value
	 */
	public abstract Object getValue(Object def);

	/**
	 * Return this node's value as the given type
	 *
	 * @param <T> The type to get as
	 * @param type The type to get as and check for
	 * @return the value as the give type, or null if the value is not present or not of the given type
	 * @see #getTypedValue(Class, Object)
	 * @see #getValue()
	 */
	public <T> T getTypedValue(Class<T> type);

	/**
	 * Return this node's value as the given type
	 *
	 * @param <T> The type to get as
	 * @param type The type to get as and check for
	 * @param def The value to use as default
	 * @return the value as the give type, or {@code def} if the value is not present or not of the given type
	 * @see #getValue(Object)
	 */
	public <T> T getTypedValue(Class<T> type, T def);

	/**
	 * Return this node's value as a list
	 *
	 * @return the list value
	 * @see #getList(java.util.List)
	 * @see #getValue()
	 */
	public List<?> getList();

	/**
	 * Return this node's value as a list
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a boolean. If this is null it will act as an empty list.
	 * @return the List value
	 * @see #getValue(Object)
	 */
	public abstract List<?> getList(List<?> def);

	/**
	 * Return this node's value as a string list.
	 * Note that this will not necessarily return the same collection that is in this configuration's value.
	 * This means that changes to the return value of this method might not affect the
	 * configuration, so after changes the value of this node should be set to this list.
	 *
	 * @return the string list value
	 * @see #getStringList(java.util.List)
	 * @see #getValue()
	 */
	public List<String> getStringList();

	/**
	 * Return this node's value as a string list.
	 * Note that this will not necessarily return the same collection that is in this configuration's value.
	 * This means that changes to the return value of this method might not affect the
	 * configuration, so after changes the value of this node should be set to this list.
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a boolean. If this is null it will act as an empty list.
	 * @return the string list value
	 * @see #getValue(Object)
	 */
	public abstract List<String> getStringList(List<String> def);

	/**
	 * Return this node's value as an integer list.
	 * Note that this will not necessarily return the same collection that is in this configuration's value.
	 * This means that changes to the return value of this method might not affect the
	 * configuration, so after changes the value of this node should be set to this list.
	 *
	 * @return the integer list value
	 * @see #getStringList(java.util.List)
	 * @see #getValue()
	 */
	public List<Integer> getIntegerList();

	/**
	 * Return this node's value as a string list.
	 * Note that this will not necessarily return the same collection that is in this value.
	 * This means that changes to the return value of this method might not affect the
	 * value, so after changes the value of this node should be set to this list.
	 *
	 * @param def The default value, returned if this node doesn't have a set value or the value isn't a boolean. If this is null it will act as an empty list.
	 * @return the string list value
	 * @see #getValue(Object)
	 */
	public abstract List<Integer> getIntegerList(List<Integer> def);

	public List<Double> getDoubleList();

	public abstract List<Double> getDoubleList(List<Double> def);

	public List<Boolean> getBooleanList();

	public abstract List<Boolean> getBooleanList(List<Boolean> def);
}
