package net.glowstone.block.data;

public abstract class BlockData {

    /**
     * The block ID this BlockData is applicable for.
     */
    private final int id;

    private final PlaceRequirement requirement;

    public BlockData(int id, PlaceRequirement requirement) {
        this.id = id;
        this.requirement = requirement;
    }

    
    public int getId() {
        return id;
    }

    public PlaceRequirement getPlaceRequirement() {
        return requirement;
    }
}
