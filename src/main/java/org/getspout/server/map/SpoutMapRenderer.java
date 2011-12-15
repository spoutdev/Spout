package org.getspout.server.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Glowstone's built-in map renderer.
 */
public final class SpoutMapRenderer extends MapRenderer {
    
    private final SpoutMapView map;

    public SpoutMapRenderer(SpoutMapView map) {
        super(false);
        this.map = map;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        // TODO
    }
    
}
