package org.bukkitcontrib.player;

public interface SkyManager {
	
	public int getCloudHeight();
	
	public void setCloudHeight(int y);
	
	public boolean isCloudsVisible();
	
	public void setCloudsVisible(boolean visible);
	
	public int getStarFrequency();
	
	public void setStarFrequency(int frequency);
	
	public boolean isStarsVisible();
	
	public void setStarsVisible(boolean visible);
	
	public int getSunSizePercent();
	
	public void setSunSizePercent(int percent);
	
	public boolean isSunVisible();
	
	public void setSunVisible(boolean visible);
	
	public String getSunTextureUrl();
	
	public void setSunTextureUrl(String Url);
	
	public int getMoonSizePercent();
	
	public void setMoonSizePercent( int percent);
	
	public boolean isMoonVisible();
	
	public void setMoonVisible(boolean visible);
	
	public String getMoonTextureUrl();
	
	public void setMoonTextureUrl(String Url);

}
