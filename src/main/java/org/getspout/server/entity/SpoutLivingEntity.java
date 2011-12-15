package org.getspout.server.entity;

import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.msg.EntityMetadataMessage;
import org.getspout.server.util.Parameter;
import org.getspout.server.util.TargetBlock;

/**
 * A GlowLivingEntity is a {@link org.bukkit.entity.Player} or {@link org.bukkit.entity.Monster}.
 * @author Graham Edgecombe.
 */
public abstract class SpoutLivingEntity extends SpoutEntity implements LivingEntity {
    
    /**
     * The entity's health.
     */
    protected int health = 0;

    /**
     * The monster's metadata.
     */
    protected final List<Parameter<?>> metadata = new ArrayList<Parameter<?>>();

    /**
     * Creates a mob within the specified world.
     * @param world The world.
     */
    public SpoutLivingEntity(SpoutServer server, SpoutWorld world) {
        super(server, world);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) health = 0;
        if (health > 200) health = 200;
        this.health = health;
    }

    public double getEyeHeight() {
       return getEyeHeight(false);
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        if (false /* TODO: sneaking */ || !ignoreSneaking) {
            return 1.6;
        } else {
            return 1.4;
        }
    }

    public Location getEyeLocation() {
        Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks.add(0);
        }
        List<Block> ret = new ArrayList<Block>();
        TargetBlock target = new TargetBlock(this, maxDistance, 0.2, transparentBlocks);
        while (target.getNextBlock()) {
            Block block = target.getCurrentBlock().getBlock();
            if (!transparentBlocks.contains(block.getTypeId())) {
                ret.add(block);
            }
        }
        return ret;
    }

    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks = null;
        }
        Location loc = new TargetBlock(this, maxDistance, 0.2, transparentBlocks).getSolidTargetBlock();
        return loc == null ? null : loc.getBlock();
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks = null;
        }
        TargetBlock target = new TargetBlock(this, maxDistance, 0.2, transparentBlocks);
        Location last = target.getSolidTargetBlock();
        if (last == null) {
            return new ArrayList<Block>(Arrays.asList(target.getPreviousBlock().getBlock()));
        }
        return new ArrayList<Block>(Arrays.asList(target.getPreviousBlock().getBlock(), last.getBlock()));
    }

    public Egg throwEgg() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Snowball throwSnowball() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Arrow shootArrow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInsideVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean leaveVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Vehicle getVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void damage(int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void damage(int amount, Entity source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaximumNoDamageTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMaximumNoDamageTicks(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLastDamage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLastDamage(int damage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNoDamageTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNoDamageTicks(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Player getKiller() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected Parameter<?> getMetadata(int index) {
        if (metadata.size() <= index) return null;
        return metadata.get(index);
    }

    protected void setMetadata(Parameter<?> data) {
        if(data.getIndex() < metadata.size()) {
            metadata.set(data.getIndex(), data);
        } else {
            metadata.add(data);
        }
        EntityMetadataMessage msg = new EntityMetadataMessage(id, metadata);
        for (SpoutPlayer player : world.getRawPlayers()) {
            if (player != this && player.canSee(this)) player.getSession().send(msg);
        }
    }

    protected void setMetadataFlag(int index, int flag, boolean value) {
        byte existingMeta = getMetadata(index) == null ? 0 : (Byte)getMetadata(index).getValue();
        if (value) {
            setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, index, (byte)(existingMeta | flag)));
        } else {
            setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, index, (byte)(existingMeta & -(flag + 1))));
        }
    }

}
