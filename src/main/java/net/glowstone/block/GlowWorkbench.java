package net.glowstone.block;

import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;

public class GlowWorkbench  {
    public static boolean interacted(GlowPlayer player, boolean rightClick, BlockFace against)
    {
        player.playNote(player.getLocation(), Instrument.PIANO, new Note((byte) 1, Note.Tone.C, false));
        return true;
    }
}
