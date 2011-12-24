package org.getspout.api.metadata;

import org.getspout.api.plugin.Plugin;

public class MetadataStringValue implements MetadataValue {
	private String data;
	public MetadataStringValue(String val){
		this.data = val;
	}
	public MetadataStringValue(int val){
		this.data = Integer.toString(val);
	}
	public MetadataStringValue(double val){
		this.data = Double.toString(val);
	}
	public MetadataStringValue(boolean val){
		this.data = Boolean.toString(val);
	}
	public int asInt() throws MetadataConversionException {
		int r  = 0;
		try{
			r = Integer.parseInt(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public double asDouble() throws MetadataConversionException {
		double r  = 0;
		try{
			r = Double.parseDouble(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public boolean asBoolean() throws MetadataConversionException {
		boolean r  = false;
		try{
			r = Boolean.parseBoolean(data);
		}
		catch(Exception e){
			throw new MetadataConversionException("Cannot convert " + data + " to int");
		}
		return r;
	}

	public String asString() {
	
		return data;
	}

	public Plugin getOwningPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

}
