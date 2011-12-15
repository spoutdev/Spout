package org.getspout.server.block.physics;

import org.getspout.server.block.SpoutBlock;

public class FluidPhysics extends DefaultBlockPhysics {
    private final int fluidId, stationaryFluidId, flowDistance;

    public FluidPhysics(int fluidId, int stationaryFluidId, int flowDistance) {
        this.fluidId = fluidId;
        this.stationaryFluidId = stationaryFluidId;
        this.flowDistance = flowDistance;
    }

    @Override
    public boolean doPhysics(SpoutBlock block) {
        if (block.getTypeId() == fluidId) {
            block.setTypeId(stationaryFluidId);
            return true;
        }
        return false;
    }
}
