package org.spout.api.gui.attribute;

import java.util.HashMap;

public class AttributeUnit {

	public static AttributeUnit PIXELS = new AttributeUnit(0, "px", "pxs", "pixels", "pixel");
	public static AttributeUnit CENTIMETERS = new AttributeUnit(1, "cm", "centimeters");
	public static AttributeUnit PERCENT = new AttributeUnit(2, "%", "percent");
	public static AttributeUnit NONE = new AttributeUnit(3, "", "none"); //can be used for complex attributes like colors

	private int id;
	private String[] text;
	
	private static HashMap<Integer, AttributeUnit> idStore = new HashMap<Integer, AttributeUnit>();
	private static HashMap<String, AttributeUnit> textStore = new HashMap<String, AttributeUnit>();
	
	public static AttributeUnit getByText(String text) {
		return textStore.get(text);
	}
	
	public static AttributeUnit getById(int id) {
		return idStore.get(id);
	}
	
	public AttributeUnit(int id, String ...text) {
		this.id = id;
		this.text = text;
		idStore.put(id, this);
		for(String txt:text) {
			textStore.put(txt, this);
		}
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String[] getText() {
		return text;
	}
	
	public void setText(String[] text) {
		this.text = text;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof AttributeUnit) {
			return id == ((AttributeUnit) other).id;
		}
		return false;
	}
}
