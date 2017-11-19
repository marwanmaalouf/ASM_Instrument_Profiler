package com.asmproj;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

abstract class MLogger<K, V>{
	protected HashMap<K, V> map;
	protected String _FILENAME;
	protected String _COLUMN_1;
	protected String _COLUMN_2;
	
	public void printToFile(){
		PrintWriter writer;
		try {
			writer = new PrintWriter(_FILENAME, "UTF-8");
			writer.printf("%-130s%-30s", _COLUMN_1, _COLUMN_2);
			writer.println();

			for (K key: map.keySet()) {
				V token = map.get(key);
				writer.printf("%-130s%-30s", key.toString(), token.toString());
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}