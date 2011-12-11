package net.glowstone.io.entity;

import java.util.HashMap;
import java.util.Map;

import net.glowstone.entity.GlowEntity;
import net.glowstone.io.entity.animals.ChickenStore;
import net.glowstone.io.entity.animals.CowStore;
import net.glowstone.io.entity.animals.PigStore;
import net.glowstone.io.entity.animals.SheepStore;
import net.glowstone.io.entity.monsters.CaveSpiderStore;
import net.glowstone.io.entity.monsters.CreeperStore;
import net.glowstone.io.entity.monsters.GhastStore;
import net.glowstone.io.entity.monsters.SilverfishStore;
import net.glowstone.io.entity.monsters.SkeletonStore;
import net.glowstone.io.entity.monsters.SlimeStore;
import net.glowstone.io.entity.monsters.SpiderStore;
import net.glowstone.io.entity.monsters.ZombieStore;

/**
 * A class used to lookup message codecs.
 * @author Graham Edgecombe
 */
public final class EntityStoreLookupService {

    /**
     * A table which maps entity ids to compound readers. This is generally used to map
     * stored entities to actual entities.
     */
    private static final Map<String, EntityStore<?>> idTable = new HashMap<String, EntityStore<?>>();

    /**
     * A table which maps entities to stores. This is generally used to map
     * entities being stored.
     */
    private static final Map<Class<? extends GlowEntity>, EntityStore<?>> classTable = new HashMap<Class<? extends GlowEntity>, EntityStore<?>>();

    /**
     * Populates the opcode and class tables with codecs.
     */
    static {
        try {
            bind(PlayerStore.class);
            bind(ChickenStore.class);
            bind(CowStore.class);
            bind(PigStore.class);
            bind(SheepStore.class);
            bind(CaveSpiderStore.class);
            bind(CreeperStore.class);
            bind(GhastStore.class);
            bind(SilverfishStore.class);
            bind(SkeletonStore.class);
            bind(SlimeStore.class);
            bind(SpiderStore.class);
            bind(ZombieStore.class);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Binds a store by adding entries for it to the tables.
     * @param clazz The store's class.
     * @param <T> The type of store.
     * @param <C> The type of entity.
     * @throws InstantiationException if the store could not be instantiated.
     * @throws IllegalAccessException if the store could not be instantiated due
     * to an access violation.
     */
    private static <T extends GlowEntity, C extends EntityStore<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException {
        EntityStore<T> store = clazz.newInstance();

        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Finds an entity store by entity id.
     * @param id The entity id.
     * @return The codec, or {@code null} if it could not be found.
     */
    public static EntityStore<?> find(String id) {
        return idTable.get(id);
    }

    /**
     * Finds a store by entity class.
     * @param clazz The entity class.
     * @param <T> The type of entity.
     * @return The codec, or {@code null} if it could not be found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends GlowEntity> EntityStore<T> find(Class<T> clazz) {
        return (EntityStore<T>) classTable.get(clazz);
    }

    /**
     * Default private constructor to prevent instantiation.
     */
    private EntityStoreLookupService() {

    }

}
