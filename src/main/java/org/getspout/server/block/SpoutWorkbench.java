package org.getspout.server.block;


import org.bukkit.block.BlockFace;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.window.WindowID;

public class SpoutWorkbench  {
    public static boolean interacted(SpoutPlayer player, boolean rightClick, BlockFace against)
    {
        if(rightClick)
        {
            player.openWindow(WindowID.WORKBENCH);
        }

        return true;
    }
}
