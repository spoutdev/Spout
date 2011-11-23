package net.glowstone.entity.neutrals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Angerable;
import net.glowstone.entity.Damager;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.animals.GlowAminals;
import net.glowstone.msg.EntityMetadataMessage;
import net.glowstone.util.Parameter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowWolf extends GlowAminals implements Wolf, Angerable {

    private boolean angry, sitting, tamed;
    private String owner;

    public GlowWolf(GlowServer server, GlowWorld world) {
        super(server, world, 95);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }

    public boolean isAngry() {
        return angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
        setMetadataFlag(16, 0x02, angry);
    }

    public boolean isSitting() {
        return sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
        setMetadataFlag(16, 0x01, sitting);
    }

    public boolean isTamed() {
        return tamed;
    }

    public void setTamed(boolean tame) {
        this.tamed = tame;
        setMetadataFlag(16, 0x04, tame);
    }

    public AnimalTamer getOwner() {
        return server.getOfflinePlayer(owner);
    }

    public void setOwner(AnimalTamer tamer) {
        if (tamer == null) {
            this.owner = "";
        } else if (tamer instanceof OfflinePlayer) {
            this.owner = ((OfflinePlayer)tamer).getName();
        } else {
            throw new IllegalArgumentException("Unknown AnimalTamer type!");
        }
        if (owner != null) {
            setMetadata(new Parameter<String>(Parameter.TYPE_STRING, 17, owner));
        }
    }

    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        setMetadata(new Parameter<Integer>(Parameter.TYPE_INT, 18, getHealth()));
    }
}
