package org.getspout.unchecked.server.io.blockstate;

import java.util.Map;

import org.getspout.api.util.nbt.ByteTag;
import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.api.util.nbt.Tag;
import org.getspout.unchecked.server.block.SpoutNoteBlock;

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
