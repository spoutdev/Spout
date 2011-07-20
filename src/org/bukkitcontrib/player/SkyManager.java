package org.bukkitcontrib.player;

public interface SkyManager {
	
	/**
	 * Gets the y-axis height that cloud tops are rendered at for the given player
	 * @param player
	 * @return height
	 */
	public int getCloudHeight(ContribPlayer player);
	/**
	 * Sets the y-axis heigh that cloud tops are rendered at for the given player
	 * @param player
	 * @param y axis level to render the cloud top at
	 */
	public void setCloudHeight(ContribPlayer player, int y);
	
	/**
	 * Is true if the clouds are visible for the given player
	 * @param player
	 * @return true if clouds are visible
	 */
	public boolean isCloudsVisible(ContribPlayer player);
	
	/**
	 * Enables or disables the rendering of clouds for the given player
	 * @param player
	 * @param visible
	 */
	public void setCloudsVisible(ContribPlayer player, boolean visible);
	
	/**
	 * Gets the frequency of stars overhead at night. The default frequency is 1500. 
	 * Higher frequencies cause more stars, lower, less
	 * @param player to get the frequency for
	 * @return frequency
	 */
	public int getStarFrequency(ContribPlayer player);
	
	/**
	 * Sets the frequency of stars overhead at night for the given player
	 * @param player to set the frequency for
	 * @param frequency
	 */
	public void setStarFrequency(ContribPlayer player, int frequency);
	
	/**
	 * Is true if the stars are visible for the given player player
	 * @param player
	 * @return if the stars are visible
	 */
	public boolean isStarsVisible(ContribPlayer player);
	
	/**
	 * Enables or disables the rendering of stars for the given player
	 * @param player
	 * @param visible
	 */
	public void setStarsVisible(ContribPlayer player, boolean visible);
	
	/**
	 * Gets the percent size of the sun, relative to the default size.
	 * 100 percent is default size. 200 percent is double isze. 50 percent is half size.
	 * @param player to get the size for
	 * @return percent size of the sun
	 */
	public int getSunSizePercent(ContribPlayer player);
	
	/**
	 * Sets the percent size of the sun, relative to the default size.
	 * 100 percent is the default size. 200 percent is double isze. 50 percent is half size.
	 * @param player to set the size for
	 * @param percent to set
	 */
	public void setSunSizePercent(ContribPlayer player, int percent);
	
	/**
	 * Is true if the sun will ever render
	 * @param player to check
	 * @return true if the sun will ever render
	 */
	public boolean isSunVisible(ContribPlayer player);
	
	/**
	 * Enables or disables rendering of the sun during daytime
	 * @param player
	 * @param visible
	 */
	public void setSunVisible(ContribPlayer player, boolean visible);
	
	/**
	 * Gets the custom url of the custom sun texture (in png, 32x32 image format), or null if no custom texture is set
	 * @param player who has the custom texture
	 * @return url of the custom texture
	 */
	public String getSunTextureUrl(ContribPlayer player);
	
	/**
	 * Sets the texture of the sun to the picture in the given format, or if the url is null, resets the sun to the default texture
	 * The texture must be a square png to render correctly (e.g 32x32, 64x64, etc)
	 * @param player to set the custom texture of
	 * @param Url of the texture
	 */
	public void setSunTextureUrl(ContribPlayer player, String Url);
	
	/**
	 * Gets the size percent of the moon, relative to the default size.
	 * 100 percent is the default size. 200 percent is double size. 50 percent is half size.
	 * @param player to get the size from
	 * @return percent size
	 */
	public int getMoonSizePercent(ContribPlayer player);
	
	/**
	 * Sets the percent size of the moon, relative to the default size.
	 * 100 percent is the default size. 200 percent is double size. 50 percent is half size.
	 * @param player to set the size for
	 * @param percent to set
	 */
	public void setMoonSizePercent(ContribPlayer player, int percent);
	
	/**
	 * Is true if the moon will ever render
	 * @param player to check
	 * @return true if the moon will ever render
	 */
	public boolean isMoonVisible(ContribPlayer player);
	
	/**
	 * Enables or disables rendering of the moon during nighttime
	 * @param player
	 * @param visible
	 */
	public void setMoonVisible(ContribPlayer player, boolean visible);
	
	/**
	 * Gets the custom url of the custom moon texture (in png, 32x32 image format), or null if no custom texture is set
	 * @param player who has the custom texture
	 * @return url of the custom texture
	 */
	public String getMoonTextureUrl(ContribPlayer player);
	
	/**
	 * Sets the texture of the moon to the picture in the given format, or if the url is null, resets the moon to the default texture
	 * The texture must be a square png to render correctly (e.g 32x32, 64x64, etc)
	 * @param player to set the custom texture of
	 * @param Url of the texture
	 */
	public void setMoonTextureUrl(ContribPlayer player, String Url);

}
