package net.glowstone.block;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.OpenWindowMessage;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;

public class GlowWorkbench  {
    private static int ID = 1;
    
    public static boolean interacted(GlowPlayer player, boolean rightClick, BlockFace against)
    {
        player.getSession().send(new OpenWindowMessage(ID, (byte)1, "Crafting",(byte)9));
        ID++;
        //player.playNote(player.getLocation(), Instrument.PIANO, new Note((byte) 1, Note.Tone.C, false));
        return true;
    }
}
