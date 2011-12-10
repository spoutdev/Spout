package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.BlockID;
import net.glowstone.entity.Damager;
import net.glowstone.entity.GlowEntity;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnVehicleMessage;
import net.glowstone.util.Position;
import org.bukkit.entity.FallingSand;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlowFallingBlock extends GlowEntity implements FallingSand {
    private final int type;

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     * @param blockId The id of the block used for this entity. Can be sand or gravel.
     */
    public GlowFallingBlock(GlowServer server, GlowWorld world, int blockId) {
        super(server, world);
        if (blockId == BlockID.SAND) {
            type = 70;
        } else if (blockId == BlockID.GRAVEL) {
            type = 71;
        } else {
            throw new IllegalArgumentException("Unknown falling block type!");
        }
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnVehicleMessage(id, type, x, y, z);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }
}
