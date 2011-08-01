package org.getspout.Spout.player;

import java.util.HashMap;
import org.getspout.Spout.packet.PacketSky;
import net.minecraft.src.BukkitContrib;
import org.getspout.Spout.io.CustomTextureManager;

public class SimpleSkyManager implements SkyManager{
	private int cloudHeight = -999;
	private int starFrequency = 1500;
	private int sunPercent = 100;
	private int moonPercent = 100;
	private String sunUrl = null;
	private String moonUrl = null;
	
	@Override
	public int getCloudHeight() {
		if (cloudHeight == -999) {
			return (int)BukkitContrib.getGameInstance().theWorld.worldProvider.getCloudHeight();
		}
		return cloudHeight;
	}

	@Override
	public void setCloudHeight(int y) {
		this.cloudHeight = y;
	}

	@Override
	public boolean isCloudsVisible() {
		return getCloudHeight() > -1;
	}

	@Override
	public void setCloudsVisible(boolean visible) {
		if (isCloudsVisible() != visible) {
			setCloudHeight(visible ? 108 : -1);
		}
	}

	@Override
	public int getStarFrequency() {
		return starFrequency;
	}

	@Override
	public void setStarFrequency(int frequency) {
		starFrequency = frequency;
	}

	@Override
	public boolean isStarsVisible() {
		return starFrequency > -1;
	}

	@Override
	public void setStarsVisible(boolean visible) {
		if (isStarsVisible() != visible) {
			setStarFrequency(visible ? 1500 : -1);
		}
	}

	@Override
	public int getSunSizePercent() {
		return sunPercent;
	}

	@Override
	public void setSunSizePercent(int percent) {
		sunPercent = percent;
	}

	@Override
	public boolean isSunVisible() {
		return sunPercent > -1;
	}

	@Override
	public void setSunVisible(boolean visible) {
		if (isSunVisible() != visible) {
			setSunSizePercent(visible ? 100 : -1);
		}
	}

	@Override
	public String getSunTextureUrl() {
		return sunUrl;
	}

	@Override
	public void setSunTextureUrl(String Url) {
		if (sunUrl != null) {
			//TODO release image?
		}
		sunUrl = Url;
		if (Url != null) {
			CustomTextureManager.downloadTexture(Url);
		}
	}

	@Override
	public int getMoonSizePercent() {
		return moonPercent;
	}

	@Override
	public void setMoonSizePercent(int percent) {
		moonPercent = percent;
	}

	@Override
	public boolean isMoonVisible() {
		return moonPercent > -1;
	}

	@Override
	public void setMoonVisible(boolean visible) {
		if (isMoonVisible() != visible) {
			setMoonSizePercent(visible ? 100 : -1);
		}
	}

	@Override
	public String getMoonTextureUrl() {
		return moonUrl;
	}

	@Override
	public void setMoonTextureUrl(String Url) {
		if (moonUrl != null) {
			//TODO release image?
		}
		moonUrl = Url;
		if (Url != null) {
			CustomTextureManager.downloadTexture(Url);
		}
	}

}
