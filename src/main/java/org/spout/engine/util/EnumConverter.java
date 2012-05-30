package org.spout.engine.util;

import com.beust.jcommander.IStringConverter;

/**
 * @author zml2008
 */
public class EnumConverter<T extends Enum<T>> implements IStringConverter<T> {
	private final Class<T> enumClass;

	public EnumConverter(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public T convert(String s) {
		return Enum.valueOf(enumClass, s.toUpperCase());
	}
}
