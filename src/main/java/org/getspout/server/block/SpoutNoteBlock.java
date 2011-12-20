package org.getspout.server.block;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;

import org.getspout.server.SpoutChunk;
import org.getspout.server.entity.SpoutPlayer;

/**
 * Represents a noteblock in the world.
 */
public class SpoutNoteBlock extends SpoutBlockState implements NoteBlock {
	private NoteWrapper wrapper = new NoteWrapper(new Note((byte) 0));

	public SpoutNoteBlock(SpoutBlock block) {
		super(block);
		if (block.getTypeId() != BlockID.NOTE_BLOCK) {
			throw new IllegalArgumentException("SpoutNoteBlock: expected NOTE_BLOCK, got " + block.getType());
		}
	}

	@Override
	public Note getNote() {
		return wrapper.note;
	}

	@Override
	public byte getRawNote() {
		return wrapper.note.getId();
	}

	@Override
	public void setNote(Note note) {
		wrapper.note = note;
	}

	@Override
	public void setRawNote(byte note) {
		wrapper.note = new Note(note);
	}

	@Override
	public boolean play() {
		return play(instrumentOf(getBlock().getRelative(BlockFace.DOWN).getTypeId()), wrapper.note);
	}

	@Override
	public boolean play(byte instrument, byte note) {
		if (getBlock().getTypeId() != BlockID.NOTE_BLOCK) {
			return false;
		}

		Location location = getBlock().getLocation();

		for (SpoutPlayer player : getWorld().getRawPlayers()) {
			if (player.canSee(new SpoutChunk.Key(getX() >> 4, getZ() >> 4))) {
				player.playNote(location, instrument, note);
			}
		}

		return true;
	}

	@Override
	public boolean play(Instrument instrument, Note note) {
		return play(instrument.getType(), note.getId());
	}

	public static Instrument instrumentOf(int id) {
		// TODO: check more blocks.
		switch (id) {
			case BlockID.WOOD:
			case BlockID.NOTE_BLOCK:
			case BlockID.WORKBENCH:
			case BlockID.LOG:
				return Instrument.BASS_GUITAR;
			case BlockID.SAND:
			case BlockID.GRAVEL:
			case BlockID.SOUL_SAND:
				return Instrument.SNARE_DRUM;
			case BlockID.GLASS:
				return Instrument.STICKS;
			case BlockID.STONE:
			case BlockID.OBSIDIAN:
			case BlockID.NETHERRACK:
			case BlockID.BRICK:
				return Instrument.BASS_DRUM;
			case BlockID.DIRT:
			case BlockID.AIR:
			default:
				return Instrument.PIANO;
		}
	}

	// Internal mechanisms

	private class NoteWrapper {
		public Note note;

		public NoteWrapper(Note note) {
			this.note = note;
		}
	}

	@Override
	public SpoutNoteBlock shallowClone() {
		SpoutNoteBlock result = new SpoutNoteBlock(getBlock());
		result.wrapper = wrapper;
		return result;
	}

	@Override
	public void destroy() {
		setRawNote((byte) 0);
	}
}
