package org.spout.api.gui.attribute;

public class AttributeValue {
	private Object value;
	
	public AttributeValue(Object value2) {
		set(value2);
	}

	public void set(Object val) {
		value = val;
	}
	
	public Object getValue() {
		return value;
	}
	
	public int getIntValue() {
		if(isInt()) {
			return (Integer) getValue();
		}
		if(isLong()) {
			return (int)(long)(Long) getValue();
		}
		return 0;
	}
	
	public boolean isInt() {
		return value instanceof Integer;
	}
	
	public long getLongValue() {
		if(isInt()) {
			return (long)(int)(Integer) getValue();
		}
		if(isLong()) {
			return (Long) getValue();
		}
		return (long) 0;
	}
	
	public boolean isLong() {
		return value instanceof Long;
	}
	
	public double getDoubleValue() {
		if(isDouble()) {
			return (Double) getValue();
		}
		if(isFloat()) {
			return (double)(float)(Float) getValue();
		}
		return 0d;
	}

	public boolean isDouble() {
		return value instanceof Double;
	}
	
	public float getFloatValue() {
		if(isFloat()) {
			return (Float) getValue();
		}
		if(isDouble()) {
			return (float)(double)(Double) getValue();
		}
		return 0f;
	}
	
	public boolean isFloat() {
		return value instanceof Float;
	}
}
