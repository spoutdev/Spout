package org.getspout.server.io.blockstate;

import java.util.Map;

import org.getspout.server.block.SpoutNoteBlock;
import org.getspout.server.util.nbt.ByteTag;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.Tag;

public class NoteBlockStore extends BlockStateStore<SpoutNoteBlock> {
	public NoteBlockStore() {
		super(SpoutNoteBlock.class, "Music");
	}

	@Override
	public void load(SpoutNoteBlock state, CompoundTag compound) {
		state.setRawNote(((ByteTag) compound.getValue().get("note")).getValue());
	}

	@Override
	public Map<String, Tag> save(SpoutNoteBlock entity) {
		Map<String, Tag> map = super.save(entity);
		map.put("note", new ByteTag("note", entity.getRawNote()));
		return map;
	}
}
