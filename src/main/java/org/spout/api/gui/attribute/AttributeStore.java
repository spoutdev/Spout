package org.spout.api.gui.attribute;

public interface AttributeStore {

	public boolean hasAttribute(String name);

	public Attribute getAttribute(String name);

	public void setAttribute(Attribute value);

}