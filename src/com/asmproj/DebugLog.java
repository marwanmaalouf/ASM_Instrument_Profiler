package com.asmproj;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class DebugLog{
	private static final class Message{
		final String message;
		final int priority;
		Message(String message, int priority){
			this.message = message;
			this.priority = priority;
		}
	}
	private static final String _DEBUGGER = "DebugLog";
	protected static List<Message> messages = new ArrayList<>();
	static void Log(String message, int priority){
		messages.add(new Message(message, priority));
		printLog();
	}
	static void Log(String message){
		messages.add(new Message(message, 0));
		printLog();
	}
	static void printLog(){
		if(!Control._DEBUG){
			return;
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter("out\\" + _DEBUGGER + ".txt", "UTF-8");

			writer.printf("%-10s%-300s\n", "PRIORITY", "MESSAGE");
			for (Message m: messages) {
				writer.printf("%-10d%-300s", m.priority, m.message);
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}