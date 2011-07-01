package org.bukkitcontrib.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkitcontrib.ClientOnly;
import org.bukkitcontrib.event.input.RenderDistance;
import org.bukkitcontrib.gui.InGameScreen;
import org.bukkitcontrib.keyboard.Keyboard;

public interface ContribPlayer extends org.bukkit.entity.Player{
    
    /**
     * Closes any dialog windows the client may have open at the time
     * @return true if a window was closed
     */
    public boolean closeActiveWindow();
    
    /**
     * Opens an inventory dialog to the player, with the given inventory displayed in the upper pane, and the player's inventory in the lower pane
     * @param inventory to use in the dialog GUI
     * @return true if an inventory window was opened
     */
    public boolean openInventoryWindow(Inventory inventory);
    
    /**
     * Opens an inventory dialog to the player, with the given inventory displayed in the upper pane, and the player's inventory in the lower pane.
     * The location is not used, but is passed to other plugins when notifying them of the open window
     * @param inventory to use in the dialog GUI
     * @param location that represents this inventory in the world (e.g Chest, Furnace). Use null if there is no physical location.
     * @return true if an inventory window was opened
     */
    public boolean openInventoryWindow(Inventory inventory, Location location);
    
    /**
     * Opens an inventory dialog to the player, with the given inventory displayed in the upper pane, and the player's inventory in the lower pane.
     * The location is not used, but is passed to other plugins when notifying them of the open window
     * @param inventory to use in the dialog GUI
     * @param location that represents this inventory in the world (e.g Chest, Furnace). Use null if there is no physical location.
     * @param ignoreDistance whether the distance from the inventory should be considered (opening an inventory will fail if it's too far away, without ignoring distance)
     * @return true if an inventory window was opened
     */
    public boolean openInventoryWindow(Inventory inventory, Location location, boolean ignoreDistance);
    
    /**
     * Opens an workbench dialog to the player, using the workbench at the given location
     * @param location of the workbench to use. Must be a valid workbench.
     * @return true if a workbench window was opened
     */
    public boolean openWorkbenchWindow(Location location);
    
    public InGameScreen getMainScreen();

    /**
     * Return's true if the player is using the bukkit contrib single player mod
     * @return bukkit contrib single player mod
     */
    public boolean isEnabledBukkitContribSinglePlayerMod();
    
    /**
     * Gets the version of the bukkitcontrib SP client mod in use, or -1 if none.
     * @return version
     */
    public int getVersion();
    
    /**
     * Return's the key bound to forward movement for this player, or unknown if not known.
     * @return forward key
     */
    @ClientOnly
    public Keyboard getForwardKey();
    
    /**
     * Return's the key bound to backward movement for this player, or unknown if not known.
     * @return backward key
     */
    @ClientOnly
    public Keyboard getBackwardKey();
    
    /**
     * Return's the key bound to left movement for this player, or unknown if not known.
     * @return left key
     */
    @ClientOnly
    public Keyboard getLeftKey();
    
    /**
     * Return's the key bound to right movement for this player, or unknown if not known.
     * @return right key
     */
    public Keyboard getRightKey();
    
    /**
     * Return's the key bound to jumping for this player, or unknown if not known.
     * @return jump key
     */
    @ClientOnly
    public Keyboard getJumpKey();
    
    /**
     * Return's the key bound to opening and closing inventories for this player, or unknown if not known.
     * @return inventory key
     */
    @ClientOnly
    public Keyboard getInventoryKey();
    
    /**
     * Return's the key bound to forward movement for this player, or unknown if not known.
     * @return forward key
     */
    @ClientOnly
    public Keyboard getDropItemKey();
    
    /**
     * Return's the key bound to opening the chat bar for this player, or unknown if not known.
     * @return chat key
     */
    @ClientOnly
    public Keyboard getChatKey();
    
    /**
     * Return's the key bound to toggle view fog for this player, or unknown if not known.
     * @return toggle fog key
     */
    @ClientOnly
    public Keyboard getToggleFogKey();
    
    /**
     * Return's the key bound to sneaking for this player, or unknown if not known.
     * @return sneak key
     */
    @ClientOnly
    public Keyboard getSneakKey();
    
    /**
     * Gets the render distance that the player views, or null if unknown
     * @return render distance
     */
    @ClientOnly
    public RenderDistance getRenderDistance();
    
    /**
     * Sets the render distance that the player views
     * @param distance to set
     */
    @ClientOnly
    public void setRenderDistance(RenderDistance distance);
    
    /**
     * Gets the maximum render distance that the player can view, or null if unknown
     * @return maximum distance
     */
    @ClientOnly
    public RenderDistance getMaximumRenderDistance();
    
    /**
     * Sets the maximum render distance that the player can view
     * @param maximum distance
     */
    @ClientOnly
    public void setMaximumRenderDistance(RenderDistance maximum);
    
    /**
     * Releases the maximum render distance, and allows the player to change the distance with no maximum restriction
     */
    @ClientOnly
    public void resetMaximumRenderDistance();
    
    /**
     * Gets the minimum render distance that the player can view, or null if unknown
     * @return minimum distance
     */
    @ClientOnly
    public RenderDistance getMinimumRenderDistance();
    
    /**
     * Releases the minimum render distance, and allows the player to change the distance with no minimum restriction
     */
    @ClientOnly
    public void resetMinimumRenderDistance();
    
    /**
     * Sets the minimum render distance that the player can view
     * @param minimum distance
     */
    @ClientOnly
    public void setMinimumRenderDistance(RenderDistance minimum);
    
    /**
     * Send's the player a notification (using the existing Achievement Get window), with the given title, message, and item to render as a graphic
     * The title and message may not exceed 26 characters in length
     * The item to render may not be null
     * @param title to send
     * @param message to send
     * @param toRender to render
     */
    @ClientOnly
    public void sendNotification(String title, String message, Material toRender);
    
    /**
     * Get's the clipboard text from the player, or null if unknown
     * @return clipboard text
     */
    @ClientOnly
    public String getClipboardText();
    
    /**
     * Sets the clipboard text for the player
     * @param text to set
     */
    @ClientOnly
    public void setClipboardText(String text);

}
