package org.getspout.server.block.data;

public abstract class ToggleableAttachable extends Attachable {

    public ToggleableAttachable(int id, PlaceRequirement requirement, AttachmentType type) {
        super(id, requirement, type);
    }

    public int toggleOpen(int existing) {
        return setOpen(existing, !isOpen(existing));
    }

    public abstract boolean isOpen(int existing);

    public abstract int setOpen(int existing, boolean open);
    
}
