package net.glowstone.item;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.glowstone.block.ItemID;
import net.glowstone.block.ItemProperties;
import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class ItemPropertiesTest {
    private final TIntList itemIdKnown = new TIntArrayList();

    @Before
    public void setUp() throws IllegalAccessException {
        for (Field field : ItemID.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            itemIdKnown.add(field.getInt(null));
        }
    }
    @Test
    public void testIdInclusion() throws IllegalAccessException {
        List<String> nonIncludedFields = new ArrayList<String>();
        for (Field field : ItemID.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            int val = field.getInt(null);
            if (ItemProperties.get(val) == null) {
                nonIncludedFields.add(field.getName() + ":" + val);
            }
        }
        if (nonIncludedFields.size() > 0) {
            StringBuilder sb = new StringBuilder("Non-included values: ");
            for (String string : nonIncludedFields) {
                sb.append(string).append(" ");
            }
            fail(sb.toString());
        }
    }

    @Test
    public void testMaterialInclusion() throws IllegalAccessException {
        List<String> nonIncludedFields = new ArrayList<String>();
        for (Material material : Material.values()) {
            if (material.isBlock()) continue;
            if (ItemProperties.get(material) == null) {
                nonIncludedFields.add(material.name() + ":" + material.getId());
            }
        }
        if (nonIncludedFields.size() > 0) {
            StringBuilder sb = new StringBuilder("Non-included values: ");
            for (String string : nonIncludedFields) {
                sb.append(string).append(" ");
            }
            fail(sb.toString());
        }
    }

    @Test
    public void testBlockIDContainsAllKnownToMaterial() {
        List<String> nonIncludedIds = new ArrayList<String>();
        for (Material mat : Material.values()) {
            if (!mat.isBlock() && !itemIdKnown.contains(mat.getId())) {
                nonIncludedIds.add(mat.name() + ":" + mat.getId());
            }
        }
        if (nonIncludedIds.size() > 0) {
            StringBuilder sb = new StringBuilder("Non-included values: ");
            for (String string : nonIncludedIds) {
                sb.append(string).append(" ");
            }
            fail(sb.toString());
        }
    }
}
