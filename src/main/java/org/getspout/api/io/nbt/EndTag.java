package org.getspout.api.io.nbt;

/**
 * The {@code TAG_End} tag.
 * @author Graham Edgecombe
 */
public final class EndTag extends Tag {
	/**
	 * Creates the tag.
	 */
	public EndTag() {
		super();
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String toString() {
		return "TAG_End";
	}
	
	public EndTag clone() {
		return new EndTag();
	}
}
