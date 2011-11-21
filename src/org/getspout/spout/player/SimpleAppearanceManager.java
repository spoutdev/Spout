/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.EntitySkinType;
import org.getspout.spoutapi.player.SpoutPlayer;

@SuppressWarnings("deprecation")
public class SimpleAppearanceManager implements AppearanceManager{
	public SimpleAppearanceManager() {
		
	}

	@Override
	public void setGlobalSkin(HumanEntity target, String Url) {
		((SpoutPlayer)target).setSkin(Url);
	}

	@Override
	public void setPlayerSkin(SpoutPlayer viewingPlayer, HumanEntity target, String Url) {
		((SpoutPlayer)target).setSkinFor(viewingPlayer, Url);
	}

	@Override
	public void setGlobalCloak(HumanEntity target, String Url) {
		((SpoutPlayer)target).setCape(Url);
	}

	@Override
	public void setPlayerCloak(SpoutPlayer viewingPlayer, HumanEntity target, String Url) {
		((SpoutPlayer)target).setCapeFor(viewingPlayer, Url);
	}

	@Override
	public void setPlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target, String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGlobalTitle(LivingEntity target, String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hidePlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideGlobalTitle(LivingEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSkinUrl(SpoutPlayer viewingPlayer, HumanEntity target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetGlobalSkin(HumanEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerSkin(SpoutPlayer viewingPlayer, HumanEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetGlobalCloak(HumanEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerCloak(SpoutPlayer viewingPlayer, HumanEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetGlobalTitle(LivingEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCloakUrl(SpoutPlayer viewingPlayer, HumanEntity target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle(SpoutPlayer viewingPlayer, LivingEntity target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetAllSkins() {
		
	}

	@Override
	public void resetAllCloaks() {
		
	}

	@Override
	public void resetAllTitles() {
		
	}

	@Override
	public void resetAll() {
		
	}

	@Override
	public void setEntitySkin(SpoutPlayer viewingPlayer, LivingEntity target, String url, EntitySkinType type) {
		
	}

	@Override
	public void setGlobalEntitySkin(LivingEntity entity, String url, EntitySkinType type) {
		
	}

	@Override
	public void resetEntitySkin(LivingEntity entity) {
		
	}
	

}
