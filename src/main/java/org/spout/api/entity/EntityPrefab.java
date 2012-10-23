package org.spout.api.entity;

import java.util.List;
import java.util.Map;

import org.spout.api.component.components.EntityComponent;

public interface EntityPrefab {
	public String getName();
	
	public List<Class<? extends EntityComponent>> getComponents();
	
	public Map<String, Object> getDatas();
}
