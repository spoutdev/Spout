package org.getspout.server.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Spout's built-in map renderer.
 */
public final class SpoutMapRenderer extends MapRenderer {
	public SpoutMapRenderer(SpoutMapView map) {
		super(false);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		// TODO
	}
}
