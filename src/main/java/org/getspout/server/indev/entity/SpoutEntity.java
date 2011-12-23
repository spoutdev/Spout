package org.getspout.server.indev.entity;

import java.util.List;

import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.indev.entity.Controller;
import org.getspout.api.indev.entity.Entity;
import org.getspout.api.indev.entity.EntityMetadataStore;
import org.getspout.api.metadata.MetadataValue;
import org.getspout.api.plugin.Plugin;

public class SpoutEntity extends EntityMetadataStore implements Entity  {
	Transform transform = new Transform();
	Controller controller;
	int id;
	
	public SpoutEntity(int id){ 
		this.id = id; 
	}
	
	public int getId(){
		return id;
	}
	
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	public Transform getTransform() {
		return transform;
	}
	
	public void onTick(float dt){
		if(controller != null) controller.onTick(dt);
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		super.setMetadata(this, metadataKey, newMetadataValue);		
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return super.getMetadata(this, metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return super.hasMetadata(this, metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		super.removeMetadata(this, metadataKey, owningPlugin);		
	}


}
