package org.spout.api.gui;

public class BoundsI extends Bounds<Integer> {

	public BoundsI() {
		this(0,0,0,0);
	}

	public BoundsI(Integer left, Integer right, Integer top, Integer bottom) {
		super(left, right, top, bottom);
	}

}
