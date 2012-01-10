package org.spout.api.gui;

public interface BasicLabel extends Label {
	/**
	 * Recalculates the word wrapping result.
	 */
	public void recalculateLines();
	
	/**
	 * @return the lines of the text. If wordwrapping is enabled, this will already be wrapped to width.
	 */
	public String [] getLines();
	
	/**
	 * Enables word wrapping. Right now, this will wrap for spaces when a line is too big to be displayed in the width.
	 * If a single word is longer than the width, it will be cut off by char.
	 * @param wrapLines wether to enable the feature.
	 * @return instance of the label
	 */
	public Label setWrapLines(boolean wrapLines);
	
	/**
	 * @return if this label has word wrapping enabled.
	 */
	public boolean isWrapLines();
}
