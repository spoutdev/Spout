package org.bukkitcontrib.player;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

public interface AppearanceManager {
    
    /**
     * Sets the skin for the target human that is visible to all players
     * @param target to change the skin for
     * @param Url for the new skin
     */
    public void setGlobalSkin(HumanEntity target, String Url);
    
    /**
     * Sets the skin for the target human that is only visible to the viewingPlayer
     * @param viewingPlayer that will see the new skin
     * @param target to change the skin for
     * @param Url for the new skin
     */
    public void setPlayerSkin(ContribPlayer viewingPlayer, HumanEntity target, String Url);
    
    /**
     * Sets the cloak for the target human that is visible to all players
     * @param target to change the cloak for
     * @param Url for the new cloak
     */
    public void setGlobalCloak(HumanEntity target, String Url);
    
    /**
     * Sets the cloak for the target human that is only visible to the viewingPlayer
     * @param viewingPlayer that will see the new cloak
     * @param target to change the cloak for
     * @param Url for the new cloak
     */
    public void setPlayerCloak(ContribPlayer viewingPlayer, HumanEntity target, String Url);
    
    /**
     * Sets the title for the target living entity that is only visible to the viewingPlayer
     * @param viewingPlayer that will see the new title
     * @param target to change the title for
     * @param title to set to the target
     */
    public void setPlayerTitle(ContribPlayer viewingPlayer, LivingEntity target, String title);
    
    /**
     * Sets the title for the target living entity that is visible to all players
     * @param target to change the title for
     * @param title to set to the target
     */
    public void setGlobalTitle(LivingEntity target, String title);
    
    /**
     * Hides title for the target living entity that is only visible to the viewingPlayer
     * @param viewingPlayer that will not see the title
     * @param target to hide the title for
     */
    public void hidePlayerTitle(ContribPlayer viewingPlayer, LivingEntity target);
    
    /**
     * Hides the title for the target living entity that is visible to all players
     * @param target to hide the title for
     */
    public void hideGlobalTitle(LivingEntity target);
    
    /**
     * Get's the current skin Url for the viewing player of the target
     * @param viewingPlayer viewing the target
     * @param target to get the skin Url for
     * @return skin Url
     */
    public String getSkinUrl(ContribPlayer viewingPlayer, HumanEntity target);
    
    /**
     * Reset's the skin for the target for all players
     * @param target to reset the skin for
     */
    public void resetGlobalSkin(HumanEntity target);
    
    /**
     * Reset's the skin for the target for only the viewingPlayer
     * @param viewingPlayer that will see the reset skin
     * @param target to reset the skin for
     */
    public void resetPlayerSkin(ContribPlayer viewingPlayer, HumanEntity target);
    
    /**
     * Resets the cloak for the target for all players
     * @param target to reset the cloak for
     */
    public void resetGlobalCloak(HumanEntity target);
    
    /**
     * Resets the cloak for the target for only the viewingPlayer
     * @param viewingPlayer that will see the reset cloak
     * @param target to reset the cloak for
     */
    public void resetPlayerCloak(ContribPlayer viewingPlayer, HumanEntity target);
    
    /**
     * Resets the title for the target for only the viewingPlayer
     * @param viewingPlayer that will see the reset title
     * @param target to reset the title for
     */
    public void resetPlayerTitle(ContribPlayer viewingPlayer, LivingEntity target);    
    
    /**
     * Resets the tite for the target for all players
     * @param target to reset the title for
     */
    public void resetGlobalTitle(LivingEntity target);
    
    /**
     * Gets the cloak Url currently set for the viewingPlayer seeing the target
     * @param viewingPlayer that sees the target
     * @param target that has the cloak
     * @return cloak Url
     */
    public String getCloakUrl(ContribPlayer viewingPlayer, HumanEntity target);
    
    /**
     * Gets the title currently set for the viewingPlayer seeing the target
     * @param viewingPlayer that sees the target
     * @param target that has the title
     * @return title
     */
    public String getTitle(ContribPlayer viewingPlayer, LivingEntity target);
    
    /**
     * Resets the skins of all humans and players back to their defaults
     */
    public void resetAllSkins();
    
    /**
     * Resets the cloaks of all humans and players back to their defaults
     */
    public void resetAllCloaks();
    
    /**
     * Resets the titles of all living entities back to their defaults
     */
    public void resetAllTitles();
    
    /**
     * Resets all skins, cloaks, and titles back to their defaults
     */
    public void resetAll();
}
