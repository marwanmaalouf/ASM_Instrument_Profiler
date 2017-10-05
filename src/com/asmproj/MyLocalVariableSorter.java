package com.asmproj;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodNode;




public class MyLocalVariableSorter extends LocalVariablesSorter {

	MethodVisitor myMethodVisitor; 
	
	public MyLocalVariableSorter(int api, int access, String desc, MethodVisitor mv){
//		super(api, access | Opcodes.ACC_STATIC, "()V", mv);
		super(api, access, desc, mv);
		myMethodVisitor = mv;
	}

	public int createLocalVariable(Type t){
		int index = super.nextLocal;
		super.newLocal(t);
		return index;	
	}
	
	public void incrementLocalCounter(int index){
		super.nextLocal = (index >= super.nextLocal)? index + 1 : super.nextLocal;
	}
	
}