package org.getspout.server.entity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

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

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutChicken;
import org.getspout.server.entity.animals.SpoutCow;
import org.getspout.server.entity.animals.SpoutPig;
import org.getspout.server.entity.animals.SpoutSheep;
import org.getspout.server.entity.monsters.SpoutCaveSpider;
import org.getspout.server.entity.monsters.SpoutCreeper;
import org.getspout.server.entity.monsters.SpoutGhast;
import org.getspout.server.entity.monsters.SpoutGiant;
import org.getspout.server.entity.monsters.SpoutSilverfish;
import org.getspout.server.entity.monsters.SpoutSkeleton;
import org.getspout.server.entity.monsters.SpoutSlime;
import org.getspout.server.entity.monsters.SpoutSpider;
import org.getspout.server.entity.monsters.SpoutZombie;
import org.getspout.server.entity.neutrals.SpoutEnderman;
import org.getspout.server.entity.neutrals.SpoutPigZombie;
import org.getspout.server.entity.neutrals.SpoutWolf;
import org.getspout.server.entity.objects.SpoutArrow;
import org.getspout.server.entity.objects.SpoutEgg;
import org.getspout.server.entity.objects.SpoutFallingBlock;
import org.getspout.server.entity.objects.SpoutFireball;
import org.getspout.server.entity.objects.SpoutItem;
import org.getspout.server.entity.objects.SpoutPrimedTNT;
import org.getspout.server.entity.objects.SpoutSnowball;
import org.getspout.server.entity.vehicles.SpoutBoat;
import org.getspout.server.entity.vehicles.SpoutMinecart;
import org.getspout.server.entity.vehicles.SpoutPoweredMinecart;
import org.getspout.server.entity.vehicles.SpoutStorageMinecart;
import org.getspout.server.entity.water.SpoutSquid;

public enum EntityProperties {
	// -- Animals
	CHICKEN(SpoutChicken.class, new EntityFactory<SpoutChicken>() {
		public SpoutChicken createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutChicken(server, world);
		}
	}, bukkit(CreatureType.CHICKEN, Chicken.class)),
	COW(SpoutCow.class, new EntityFactory<SpoutCow>() {
		public SpoutCow createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutCow(server, world);
		}
	}, bukkit(CreatureType.COW, Cow.class)),
	PIG(SpoutPig.class, new EntityFactory<SpoutPig>() {
		public SpoutPig createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutPig(server, world);
		}
	}, bukkit(CreatureType.PIG, Pig.class)),
	SHEEP(SpoutSheep.class, new EntityFactory<SpoutSheep>() {
		public SpoutSheep createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSheep(server, world);
		}
	}, bukkit(CreatureType.SHEEP, Sheep.class)),

	// -- Monsters

	CAVE_SPIDER(SpoutCaveSpider.class, new EntityFactory<SpoutCaveSpider>() {
		public SpoutCaveSpider createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutCaveSpider(server, world);
		}
	}, bukkit(CreatureType.CAVE_SPIDER, CaveSpider.class)),
	CREEPER(SpoutCreeper.class, new EntityFactory<SpoutCreeper>() {
		public SpoutCreeper createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutCreeper(server, world);
		}
	}, bukkit(CreatureType.CREEPER, Creeper.class)),
	GHAST(SpoutGhast.class, new EntityFactory<SpoutGhast>() {
		public SpoutGhast createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutGhast(server, world);
		}
	}, bukkit(CreatureType.GHAST, Ghast.class)),
	GIANT(SpoutGiant.class, new EntityFactory<SpoutGiant>() {
		public SpoutGiant createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutGiant(server, world);
		}
	}, bukkit(CreatureType.GIANT, Giant.class)),
	SILVERFISH(SpoutSilverfish.class, new EntityFactory<SpoutSilverfish>() {
		public SpoutSilverfish createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSilverfish(server, world);
		}
	}, bukkit(CreatureType.SILVERFISH, Silverfish.class)),
	SKELETON(SpoutSkeleton.class, new EntityFactory<SpoutSkeleton>() {
		public SpoutSkeleton createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSkeleton(server, world);
		}
	}, bukkit(CreatureType.SKELETON, Skeleton.class)),
	SLIME(SpoutSlime.class, new EntityFactory<SpoutSlime>() {
		public SpoutSlime createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSlime(server, world);
		}
	}, bukkit(CreatureType.SLIME, Slime.class)),
	SPIDER(SpoutSpider.class, new EntityFactory<SpoutSpider>() {
		public SpoutSpider createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSpider(server, world);
		}
	}, bukkit(CreatureType.SPIDER, Spider.class)),
	ZOMBIE(SpoutZombie.class, new EntityFactory<SpoutZombie>() {
		public SpoutZombie createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutZombie(server, world);
		}
	}, bukkit(CreatureType.ZOMBIE, Zombie.class)),

	// -- Neutrals
	ENDERMAN(SpoutEnderman.class, new EntityFactory<SpoutEnderman>() {
		public SpoutEnderman createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutEnderman(server, world);
		}
	}, bukkit(CreatureType.ENDERMAN, Enderman.class)),

	PIG_ZOMBIE(SpoutPigZombie.class, new EntityFactory<SpoutPigZombie>() {
		public SpoutPigZombie createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutPigZombie(server, world);
		}
	}, bukkit(CreatureType.PIG_ZOMBIE, PigZombie.class)),
	WOLF(SpoutWolf.class, new EntityFactory<SpoutWolf>() {
		public SpoutWolf createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutWolf(server, world);
		}
	}, bukkit(CreatureType.WOLF, Wolf.class)),

	// -- Water mobs

	SQUID(SpoutSquid.class, new EntityFactory<SpoutSquid>() {
		public SpoutSquid createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSquid(server, world);
		}
	}, bukkit(CreatureType.SQUID, Squid.class)),

	// -- Projectiles

	ARROW(SpoutArrow.class, new EntityFactory<SpoutArrow>() {
		public SpoutArrow createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutArrow(server, world);
		}
	}, bukkit(null, Arrow.class)),
	EGG(SpoutEgg.class, new EntityFactory<SpoutEgg>() {
		public SpoutEgg createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutEgg(server, world);
		}
	}, bukkit(null, Egg.class)),
	FIREBALL(SpoutFireball.class, new EntityFactory<SpoutFireball>() {
		public SpoutFireball createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutFireball(server, world, new Vector(0, 0, 0));
		}
	}, bukkit(null, Fireball.class)),
	SNOWBALL(SpoutSnowball.class, new EntityFactory<SpoutSnowball>() {
		public SpoutSnowball createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutSnowball(server, world);
		}
	}, bukkit(null, Snowball.class)),

	// -- Vehicles

	BOAT(SpoutBoat.class, new EntityFactory<SpoutBoat>() {
		public SpoutBoat createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutBoat(server, world);
		}
	}, bukkit(null, Boat.class)),
	MINECART(SpoutMinecart.class, new EntityFactory<SpoutMinecart>() {
		public SpoutMinecart createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutMinecart(server, world);
		}
	}, bukkit(null, Minecart.class)),
	POWERED_MINECART(SpoutPoweredMinecart.class, new EntityFactory<SpoutPoweredMinecart>() {
		public SpoutPoweredMinecart createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutPoweredMinecart(server, world);
		}
	}, bukkit(null, PoweredMinecart.class)),
	STORAGE_MINECART(SpoutStorageMinecart.class, new EntityFactory<SpoutStorageMinecart>() {
		public SpoutStorageMinecart createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutStorageMinecart(server, world);
		}
	}, bukkit(null, StorageMinecart.class)),

	// -- Objects

	FALLING_BLOCK(SpoutFallingBlock.class, new EntityFactory<SpoutFallingBlock>() {
		public SpoutFallingBlock createEntity(SpoutServer server, SpoutWorld world) {
			return null;
		}
	}, bukkit(null, FallingSand.class)),
	ITEM(SpoutItem.class, new EntityFactory<SpoutItem>() {
		public SpoutItem createEntity(SpoutServer server, SpoutWorld world) {
			return null;
		}
	}, bukkit(null, Item.class)),
	PRIMED_TNT(SpoutPrimedTNT.class, new EntityFactory<SpoutPrimedTNT>() {
		public SpoutPrimedTNT createEntity(SpoutServer server, SpoutWorld world) {
			return new SpoutPrimedTNT(server, world);
		}
	});


	private static final Map<CreatureType, EntityProperties> creatureTypeLookup = new EnumMap<CreatureType, EntityProperties>(CreatureType.class);
	private static final Map<Class<? extends Entity>, EntityProperties> bukkitClassLookup = new HashMap<Class<? extends Entity>, EntityProperties>();
	private static final Map<Class <? extends SpoutEntity>, EntityProperties> spoutClassLookup = new HashMap<Class<? extends SpoutEntity>, EntityProperties>();

	static {
		for (EntityProperties prop : EntityProperties.values()) {
			if (prop.creatureType != null && !creatureTypeLookup.containsKey(prop.creatureType)) {
					creatureTypeLookup.put(prop.creatureType, prop);
				}
				if (prop.bukkitClass != null && !bukkitClassLookup.containsKey(prop.bukkitClass)) {
					bukkitClassLookup.put(prop.bukkitClass, prop);
				}
			spoutClassLookup.put(prop.entityClass, prop);
		}
	}

	public static EntityProperties getByCreatureType(CreatureType type) {
		return creatureTypeLookup.get(type);
	}

	public static EntityProperties getByBukkitClass(Class<? extends Entity> clazz) {
		return bukkitClassLookup.get(clazz);
	}

	public static EntityProperties getBySpoutClass(Class<? extends SpoutEntity> clazz) {
		return spoutClassLookup.get(clazz);
	}

	private int maxHealth;
	private Class<? extends SpoutEntity> entityClass;
	private CreatureType creatureType;
	private Class<? extends Entity> bukkitClass;
	private EntityFactory factory;


	private <T extends SpoutEntity> EntityProperties(Class<T> entity, EntityFactory<T> factory, Property... props) {
		entityClass = entity;
		this.factory = factory;
		for (Property p : props)
			p.apply(this);
	}

	public CreatureType getCreatureType() {
		return creatureType;
	}

	public Class<? extends SpoutEntity> getEntityClass() {
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
