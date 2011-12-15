package net.glowstone.block;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.window.WindowID;

import org.bukkit.block.BlockFace;

public class GlowWorkbench  {
    public static boolean interacted(GlowPlayer player, boolean rightClick, BlockFace against)
    {
        if(rightClick)
        {
            player.openWindow(WindowID.WORKBENCH);
        }

        return true;
    }
}
