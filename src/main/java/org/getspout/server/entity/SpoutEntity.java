package org.getspout.server.entity;

import java.util.List;

import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.EntityMetadataStore;
import org.getspout.api.metadata.MetadataStringValue;
import org.getspout.api.metadata.MetadataValue;
import org.getspout.api.plugin.Plugin;

public class SpoutEntity extends EntityMetadataStore implements Entity  {
	Transform transform = new Transform();
	Transform previousLocation;
	Controller controller;
	public int id;
	
	public SpoutEntity(){ 
	
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id; 
	}
	
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		controller.attachToEntity(this);
		this.controller = controller;
	}
	public Transform getTransform() {
		return transform;
	}
	
	public void onTick(float dt){
		if(controller != null) controller.onTick(dt);
	}
	
	public void reset(){
		previousLocation = transform;
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

	@Override
	public boolean is(Class<?> clazz) {		
		return clazz.isAssignableFrom(this.getController().getClass());
	}

	@Override
	public void setMetadata(String key, int value) {
		setMetadata(key, new MetadataStringValue(value));		
	}

	@Override
	public void setMetadata(String key, float value) {
		setMetadata(key, new MetadataStringValue(value));	
		
	}

	@Override
	public void setMetadata(String key, String value) {
		setMetadata(key, new MetadataStringValue(value));	
		
	}


}
