package org.getspout.event.input;

public enum RenderDistance {
	FAR(0),
	NORMAL(1),
	SHORT(2),
	TINY(3);
	
	private final int value;
	RenderDistance(final int i) {
		value = i;
	}
	
	public final int getValue() {
		return value;
	}
	
	public static RenderDistance getRenderDistanceFromValue(int value) {
		for (RenderDistance rd : values()) {
			if (rd.getValue() == value) {
				return rd;
			}
		}
		return null;
	}
}
