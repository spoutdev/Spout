package org.spout.api.gui.attribute;

import java.util.HashMap;

public class SimpleAttributeStore implements AttributeStore {
	private HashMap<String, Attribute> attachedAttributes = new HashMap<String, Attribute>();
	
	@Override
	public boolean hasAttribute(String name) {
		return attachedAttributes.containsKey(name);
	}
	
	@Override
	public Attribute getAttribute(String name) {
		return attachedAttributes.get(name);
	}
	
	@Override
	public void setAttribute(Attribute value) {
		attachedAttributes.put(value.getName(), value);
	}
}
