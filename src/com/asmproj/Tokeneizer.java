package com.asmproj;

import java.util.HashMap;

final class Tokeneizer extends MLogger{
	private int counter;
	public Tokeneizer(String name) {
		super.map = new HashMap<String, Integer>();
		super._FILENAME = name + ".txt";
		counter = 0;
	}
	
	public Integer tokenize(String key){
		Integer token = new Integer(counter);
		if(!map.containsKey(key)){
			map.put(key, token);
			counter++;
			DebugLog.Log("Tokenizing " + key + " with token " + (counter -1));
		}else{
			token = (Integer) map.get(key);
		}
		return token;
	}
}