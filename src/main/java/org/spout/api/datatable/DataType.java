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
package org.spout.api.datatable;

public abstract class DataType<T extends AbstractData> {
	
	public static final DataType<BooleanData> BOOLEAN = DataRegistry.register(new DataType<BooleanData>(BooleanData.class) { public BooleanData newInstance(int key) { return new BooleanData(key); }});
	
	public static final DataType<ByteData> BYTE = DataRegistry.register(new DataType<ByteData>(ByteData.class) { public ByteData newInstance(int key) { return new ByteData(key); }});
	
	public static final DataType<DoubleData> DOUBLE = DataRegistry.register(new DataType<DoubleData>(DoubleData.class) { public DoubleData newInstance(int key) { return new DoubleData(key); }});
	
	public static final DataType<FloatData> FLOAT = DataRegistry.register(new DataType<FloatData>(FloatData.class) { public FloatData newInstance(int key) { return new FloatData(key); }});
	
	public static final DataType<IntegerData> INTEGER = DataRegistry.register(new DataType<IntegerData>(IntegerData.class) { public IntegerData newInstance(int key) { return new IntegerData(key); }});
	
	public static final DataType<LongData> LONG = DataRegistry.register(new DataType<LongData>(LongData.class) { public LongData newInstance(int key) { return new LongData(key); }});
	
	public static final DataType<NullData> NULL = DataRegistry.register(new DataType<NullData>(NullData.class) { public NullData newInstance(int key) { return new NullData(key); }});
	
	public static final DataType<SerializableData> SERIALIZABLE = DataRegistry.register(new DataType<SerializableData>(SerializableData.class) { public SerializableData newInstance(int key) { return new SerializableData(key); }})	;
	
	public static final DataType<ShortData> SHORT = DataRegistry.register(new DataType<ShortData>(ShortData.class) { public ShortData newInstance(int key) { return new ShortData(key); }});

	public static final DataType<StringData> STRING = DataRegistry.register(new DataType<StringData>(StringData.class) { public StringData newInstance(int key) { return new StringData(key); }});

	public static void init() {}

	private final Class<T> dataType;
	
	public DataType(Class<T> clazz) {
		this.dataType = clazz;
	}
	
	public abstract T newInstance(int key);

	@Override
	public String toString() {
		return "DataType{" + "dataType=" + dataType + '}';
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 19 * hash + (this.dataType != null ? this.dataType.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DataType<T> other = (DataType<T>) obj;
		if (this.dataType != other.dataType && (this.dataType == null || !this.dataType.equals(other.dataType))) {
			return false;
		}
		return true;
	}

	public Class<T> getDataType() {
		return dataType;
	}

}
