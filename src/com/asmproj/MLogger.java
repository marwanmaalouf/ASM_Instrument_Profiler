package com.asmproj;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

abstract class MLogger{
	protected HashMap map;
	public String _FILENAME;
	public void printToFile(){
		PrintWriter writer;
		try {
			writer = new PrintWriter(_FILENAME, "UTF-8");
			for (Object key: map.keySet()) {
				Object token = map.get(key);
				writer.printf("%-100s%-30s", key.toString(), token.toString());
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}