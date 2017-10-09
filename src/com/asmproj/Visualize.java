package com.asmproj;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Visualize {
		
		static List<InsnList> l = new ArrayList<InsnList>();
		static InsnList il = new InsnList();
		
	 @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		 
		String filePath = "C:\\Users\\User\\Desktop\\ASM bytecode project\\out\\com\\asmproj\\Testing_2.class";
		System.out.println("Loading: " + filePath);
        InputStream in = new FileInputStream(filePath);
        
	     ClassReader reader = new ClassReader(in);
	     ClassNode classNode = new ClassNode();
	     reader.accept(classNode,0);
	     
	     @SuppressWarnings("unchecked")
	     final List<MethodNode> methods = classNode.methods;
	     int count = 0;
	     for(MethodNode m: methods){
	    	 System.out.println(m.name);
	    	 for(AbstractInsnNode ain: m.instructions.toArray()){
	    		 if(ain.getType() == AbstractInsnNode.FRAME){
	    			 l.add(il);
	    			 il = new InsnList();
	    	    	 System.out.println("new block");

	    		 }else{
	    			 il.add(ain);
	    			 System.out.print(insnToString(ain));
	    		 }
	    	 }

	     }
	     

//	     System.out.println(l);
//	     for(InsnList list: l){
//	    	 System.out.println("new block");
//	    	 AbstractInsnNode [] listofnode = list.toArray();
//	    	 for(int i = 0; i < listofnode.length; i++){
//	    		 System.out.println(insnToString(listofnode[i]));
//	    	 }
//	     }
	     
	     	//write classNode
			ClassWriter out=new ClassWriter(0);
			classNode.accept(out);
			output("C:\\Users\\User\\workspace\\Eclipse Test\\src\\Main1.class",  out.toByteArray());
	    }

	    public static String insnToString(AbstractInsnNode insn){
	        insn.accept(mp);
	        StringWriter sw = new StringWriter();
	        printer.print(new PrintWriter(sw));
	        printer.getText().clear();
	        return sw.toString();
	    }

		public static void output(String filename, byte[] data) throws IOException {
			FileOutputStream out=new FileOutputStream(filename);
			out.write(data);
			out.close();
		}
		
	    private static Printer printer = new Textifier();
	    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer); 

	

}
