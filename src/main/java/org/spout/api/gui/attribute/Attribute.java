package org.spout.api.gui.attribute;

public class Attribute {
	private String name = "";
	private AttributeValue value = null;
	private AttributeUnit unit = null;
	private boolean dirty = true;
	
	public Attribute(String name, Object value, AttributeUnit unit) {
		this(name, new AttributeValue(value), unit);
	}

	public Attribute(String name, AttributeValue value, AttributeUnit unit) {
		super();
		this.name = name;
		this.value = value;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		dirty = true;
	}

	public AttributeValue getValue() {
		return value;
	}

	public void setValue(AttributeValue value) {
		this.value = value;
		dirty = true;
	}

	public AttributeUnit getUnit() {
		return unit;
	}

	public void setUnit(AttributeUnit unit) {
		this.unit = unit;
		dirty = true;
	}
	
	public boolean isDirty() {
		return dirty;
	}
}
