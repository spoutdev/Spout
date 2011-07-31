package org.getspout.gui;

public interface Texture extends Widget {
	
	/**
	 * Gets the url of this texture to render
	 * @return url
	 */
	public String getUrl();
	
	/**
	 * Sets the url of this texture to render
	 * All textures must be of png type and a size that is a factor of 2 (e.g 64x128). Use the alpha channel for hiding empty space.
	 * @param url to set this texture to
	 * @return texture
	 */
	public Texture setUrl(String url);

}
