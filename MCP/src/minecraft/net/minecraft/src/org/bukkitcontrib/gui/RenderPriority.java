package org.getspout.Spout.gui;

public enum RenderPriority {
	/**
	 * Will render above all other textures and widgets
	 */
	Highest(0),
	/**
	 * Will render above most other textures and widgets
	 */
	High(1),
	/**
	 * Will render in line with most other textures and widgets
	 */
	Normal(2),
	/**
	 * Will render below most other textures and widgets
	 */
	Low(3),
	/**
	 * Will render below all other textures and widgets
	 */
	Lowest(4),
	;
	
	private final int id;
	RenderPriority(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public static RenderPriority getRenderPriorityFromId(int id) {
		for (RenderPriority rp : values()) {
			if (rp.getId() == id) {
				return rp;
			}
		}
		return null;
	}
}
