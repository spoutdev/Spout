package org.getspout.server.entity;

import java.util.List;

import org.getspout.api.collision.model.CollisionModel;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.EntityMetadataStore;
import org.getspout.api.metadata.MetadataStringValue;
import org.getspout.api.metadata.MetadataValue;
import org.getspout.api.model.Model;
import org.getspout.api.plugin.Plugin;

public class SpoutEntity extends EntityMetadataStore implements Entity  {
	public final static int NOTSPAWNEDID = -1;
	
	Transform transform = new Transform();
	Transform previousLocation;
	Controller controller;
	
	public int id = NOTSPAWNEDID;
	
	Model model;
	CollisionModel collision;
	
	
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
	
	@Override
	public void setModel(Model model) {
		this.model = model;		
	}

	@Override
	public Model getModel() {		
		return model;
	}

	@Override
	public void setCollision(CollisionModel model) {
		this.collision = model;
		
	}

	@Override
	public CollisionModel getCollision() {
		return collision;
	}

	
	
	/**
	 * 
	 * @param dt milliseonds since the last tick
	 */
	public void onTick(float dt){
		if(controller != null) controller.onTick(dt);
	}
	
	@Override
	public boolean isSpawned(){
		return (id == NOTSPAWNEDID);
	}
	
	/**
	 * Called when the tick is finished and collisions need to be resolved
	 * and move events fired
	 */
	public void resolve(){
		//Resolve Collisions Here
		
		//Check to see if we should fire off a Move event
		previousLocation = transform.copy();
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
	public boolean is(Class<? extends Controller> clazz) {		
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
