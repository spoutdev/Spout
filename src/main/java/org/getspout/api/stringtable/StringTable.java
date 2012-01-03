package org.getspout.api.stringtable;


import gnu.trove.map.hash.TObjectIntHashMap;

public class StringTable {
	
	TObjectIntHashMap<String> table = new TObjectIntHashMap<String>();
	
	public void registerString(String s){
		if(table.contains(s)) return;
		table.put(s, table.size());
	}
	
	public int getStringID(String s){
		if(!table.contains(s)) registerString(s);
		return table.get(s);
	}


}
