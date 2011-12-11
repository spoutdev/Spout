package net.glowstone.entity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowChicken;
import net.glowstone.entity.animals.GlowCow;
import net.glowstone.entity.animals.GlowPig;
import net.glowstone.entity.animals.GlowSheep;
import net.glowstone.entity.monsters.GlowCaveSpider;
import net.glowstone.entity.monsters.GlowCreeper;
import net.glowstone.entity.monsters.GlowGhast;
import net.glowstone.entity.monsters.GlowGiant;
import net.glowstone.entity.monsters.GlowSilverfish;
import net.glowstone.entity.monsters.GlowSkeleton;
import net.glowstone.entity.monsters.GlowSlime;
import net.glowstone.entity.monsters.GlowSpider;
import net.glowstone.entity.monsters.GlowZombie;
import net.glowstone.entity.neutrals.GlowEnderman;
import net.glowstone.entity.neutrals.GlowPigZombie;
import net.glowstone.entity.neutrals.GlowWolf;
import net.glowstone.entity.objects.GlowArrow;
import net.glowstone.entity.objects.GlowEgg;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.entity.objects.GlowFireball;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.entity.objects.GlowPrimedTNT;
import net.glowstone.entity.objects.GlowSnowball;
import net.glowstone.entity.vehicles.GlowBoat;
import net.glowstone.entity.vehicles.GlowMinecart;
import net.glowstone.entity.vehicles.GlowPoweredMinecart;
import net.glowstone.entity.vehicles.GlowStorageMinecart;
import net.glowstone.entity.water.GlowSquid;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public enum EntityProperties {

    // -- Animals
    CHICKEN(GlowChicken.class, new EntityFactory<GlowChicken>() {
        public GlowChicken createEntity(GlowServer server, GlowWorld world) {
            return new GlowChicken(server, world);
        }
    }, bukkit(CreatureType.CHICKEN, Chicken.class)),
    COW(GlowCow.class, new EntityFactory<GlowCow>() {
        public GlowCow createEntity(GlowServer server, GlowWorld world) {
            return new GlowCow(server, world);
        }
    }, bukkit(CreatureType.COW, Cow.class)),
    PIG(GlowPig.class, new EntityFactory<GlowPig>() {
        public GlowPig createEntity(GlowServer server, GlowWorld world) {
            return new GlowPig(server, world);
        }
    }, bukkit(CreatureType.PIG, Pig.class)),
    SHEEP(GlowSheep.class, new EntityFactory<GlowSheep>() {
        public GlowSheep createEntity(GlowServer server, GlowWorld world) {
            return new GlowSheep(server, world);
        }
    }, bukkit(CreatureType.SHEEP, Sheep.class)),

    // -- Monsters

    CAVE_SPIDER(GlowCaveSpider.class, new EntityFactory<GlowCaveSpider>() {
        public GlowCaveSpider createEntity(GlowServer server, GlowWorld world) {
            return new GlowCaveSpider(server, world);
        }
    }, bukkit(CreatureType.CAVE_SPIDER, CaveSpider.class)),
    CREEPER(GlowCreeper.class, new EntityFactory<GlowCreeper>() {
        public GlowCreeper createEntity(GlowServer server, GlowWorld world) {
            return new GlowCreeper(server, world);
        }
    }, bukkit(CreatureType.CREEPER, Creeper.class)),
    GHAST(GlowGhast.class, new EntityFactory<GlowGhast>() {
        public GlowGhast createEntity(GlowServer server, GlowWorld world) {
            return new GlowGhast(server, world);
        }
    }, bukkit(CreatureType.GHAST, Ghast.class)),
    GIANT(GlowGiant.class, new EntityFactory<GlowGiant>() {
        public GlowGiant createEntity(GlowServer server, GlowWorld world) {
            return new GlowGiant(server, world);
        }
    }, bukkit(CreatureType.GIANT, Giant.class)),
    SILVERFISH(GlowSilverfish.class, new EntityFactory<GlowSilverfish>() {
        public GlowSilverfish createEntity(GlowServer server, GlowWorld world) {
            return new GlowSilverfish(server, world);
        }
    }, bukkit(CreatureType.SILVERFISH, Silverfish.class)),
    SKELETON(GlowSkeleton.class, new EntityFactory<GlowSkeleton>() {
        public GlowSkeleton createEntity(GlowServer server, GlowWorld world) {
            return new GlowSkeleton(server, world);
        }
    }, bukkit(CreatureType.SKELETON, Skeleton.class)),
    SLIME(GlowSlime.class, new EntityFactory<GlowSlime>() {
        public GlowSlime createEntity(GlowServer server, GlowWorld world) {
            return new GlowSlime(server, world);
        }
    }, bukkit(CreatureType.SLIME, Slime.class)),
    SPIDER(GlowSpider.class, new EntityFactory<GlowSpider>() {
        public GlowSpider createEntity(GlowServer server, GlowWorld world) {
            return new GlowSpider(server, world);
        }
    }, bukkit(CreatureType.SPIDER, Spider.class)),
    ZOMBIE(GlowZombie.class, new EntityFactory<GlowZombie>() {
        public GlowZombie createEntity(GlowServer server, GlowWorld world) {
            return new GlowZombie(server, world);
        }
    }, bukkit(CreatureType.ZOMBIE, Zombie.class)),

    // -- Neutrals
    ENDERMAN(GlowEnderman.class, new EntityFactory<GlowEnderman>() {
        public GlowEnderman createEntity(GlowServer server, GlowWorld world) {
            return new GlowEnderman(server, world);
        }
    }, bukkit(CreatureType.ENDERMAN, Enderman.class)),

    PIG_ZOMBIE(GlowPigZombie.class, new EntityFactory<GlowPigZombie>() {
        public GlowPigZombie createEntity(GlowServer server, GlowWorld world) {
            return new GlowPigZombie(server, world);
        }
    }, bukkit(CreatureType.PIG_ZOMBIE, PigZombie.class)),
    WOLF(GlowWolf.class, new EntityFactory<GlowWolf>() {
        public GlowWolf createEntity(GlowServer server, GlowWorld world) {
            return new GlowWolf(server, world);
        }
    }, bukkit(CreatureType.WOLF, Wolf.class)),

    // -- Water mobs

    SQUID(GlowSquid.class, new EntityFactory<GlowSquid>() {
        public GlowSquid createEntity(GlowServer server, GlowWorld world) {
            return new GlowSquid(server, world);
        }
    }, bukkit(CreatureType.SQUID, Squid.class)),

    // -- Projectiles

    ARROW(GlowArrow.class, new EntityFactory<GlowArrow>() {
        public GlowArrow createEntity(GlowServer server, GlowWorld world) {
            return new GlowArrow(server, world);
        }
    }, bukkit(null, Arrow.class)),
    EGG(GlowEgg.class, new EntityFactory<GlowEgg>() {
        public GlowEgg createEntity(GlowServer server, GlowWorld world) {
            return new GlowEgg(server, world);
        }
    }, bukkit(null, Egg.class)),
    FIREBALL(GlowFireball.class, new EntityFactory<GlowFireball>() {
        public GlowFireball createEntity(GlowServer server, GlowWorld world) {
            return new GlowFireball(server, world, new Vector(0, 0, 0));
        }
    }, bukkit(null, Fireball.class)),
    SNOWBALL(GlowSnowball.class, new EntityFactory<GlowSnowball>() {
        public GlowSnowball createEntity(GlowServer server, GlowWorld world) {
            return new GlowSnowball(server, world);
        }
    }, bukkit(null, Snowball.class)),

    // -- Vehicles

    BOAT(GlowBoat.class, new EntityFactory<GlowBoat>() {
        public GlowBoat createEntity(GlowServer server, GlowWorld world) {
            return new GlowBoat(server, world);
        }
    }, bukkit(null, Boat.class)),
    MINECART(GlowMinecart.class, new EntityFactory<GlowMinecart>() {
        public GlowMinecart createEntity(GlowServer server, GlowWorld world) {
            return new GlowMinecart(server, world);
        }
    }, bukkit(null, Minecart.class)),
    POWERED_MINECART(GlowPoweredMinecart.class, new EntityFactory<GlowPoweredMinecart>() {
        public GlowPoweredMinecart createEntity(GlowServer server, GlowWorld world) {
            return new GlowPoweredMinecart(server, world);
        }
    }, bukkit(null, PoweredMinecart.class)),
    STORAGE_MINECART(GlowStorageMinecart.class, new EntityFactory<GlowStorageMinecart>() {
        public GlowStorageMinecart createEntity(GlowServer server, GlowWorld world) {
            return new GlowStorageMinecart(server, world);
        }
    }, bukkit(null, StorageMinecart.class)),

    // -- Objects

    FALLING_BLOCK(GlowFallingBlock.class, new EntityFactory<GlowFallingBlock>() {
        public GlowFallingBlock createEntity(GlowServer server, GlowWorld world) {
            return null;
        }
    }, bukkit(null, FallingSand.class)),
    ITEM(GlowItem.class, new EntityFactory<GlowItem>() {
        public GlowItem createEntity(GlowServer server, GlowWorld world) {
            return null;
        }
    }, bukkit(null, Item.class)),
    PRIMED_TNT(GlowPrimedTNT.class, new EntityFactory<GlowPrimedTNT>() {
        public GlowPrimedTNT createEntity(GlowServer server, GlowWorld world) {
            return new GlowPrimedTNT(server, world);
        }
    });


    private static final Map<CreatureType, EntityProperties> creatureTypeLookup = new EnumMap<CreatureType, EntityProperties>(CreatureType.class);
    private static final Map<Class<? extends Entity>, EntityProperties> bukkitClassLookup = new HashMap<Class<? extends Entity>, EntityProperties>();
    private static final Map<Class <? extends GlowEntity>, EntityProperties> glowClassLookup = new HashMap<Class<? extends GlowEntity>, EntityProperties>();

    static {
        for (EntityProperties prop : EntityProperties.values()) {
            if (prop.creatureType != null && !creatureTypeLookup.containsKey(prop.creatureType)) {
                    creatureTypeLookup.put(prop.creatureType, prop);
                }
                if (prop.bukkitClass != null && !bukkitClassLookup.containsKey(prop.bukkitClass)) {
                    bukkitClassLookup.put(prop.bukkitClass, prop);
                }
            glowClassLookup.put(prop.entityClass, prop);
        }
    }

    public static EntityProperties getByCreatureType(CreatureType type) {
        return creatureTypeLookup.get(type);
    }

    public static EntityProperties getByBukkitClass(Class<? extends Entity> clazz) {
        return bukkitClassLookup.get(clazz);
    }

    public static EntityProperties getByGlowClass(Class<? extends GlowEntity> clazz) {
        return glowClassLookup.get(clazz);
    }
    
    private int maxHealth;
    private Class<? extends GlowEntity> entityClass;
    private CreatureType creatureType;
    private Class<? extends Entity> bukkitClass;
    private EntityFactory factory;


    private <T extends GlowEntity> EntityProperties(Class<T> entity, EntityFactory<T> factory, Property... props) {
        entityClass = entity;
        this.factory = factory;
        for (Property p : props)
            p.apply(this);
    }

    public CreatureType getCreatureType() {
        return creatureType;
    }

    public Class<? extends GlowEntity> getEntityClass() {
        return entityClass;
    }

    public Class<? extends Entity> getBukkitClass() {
        return bukkitClass;
    }

    public EntityFactory getFactory() {
        return factory;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    // -----------------

    private interface Property {
        void apply(EntityProperties set);
    }

    private static Property bukkit(@Nullable final CreatureType creature, final Class<? extends Entity> bukkitEntity) {
        return new Property() {
            public void apply(EntityProperties s) {
                s.creatureType = creature;
                s.bukkitClass = bukkitEntity;
            }
        };
    }

    private static Property maxHealth(final int health) {
        return new Property() {
            public void apply(EntityProperties set) {
                set.maxHealth = health;
            }
        };
    }

}
