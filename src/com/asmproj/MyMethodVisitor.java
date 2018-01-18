package com.asmproj;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// TODO:
// Fix the output.csv -> replace 1 and 0 by actual string
// Try to add java line

public class MyMethodVisitor extends MethodVisitor {

	private static final Type STRING_TYPE = Type.getType(String.class);
	private static final Type OBJECT_TYPE = Type.getType(Object.class);
	private static final String PROFILER = "javaprofiler/dataflow/dupair/Profiler";

	private final String mMethodName;
	private final String mMethodSignature;
	private final String mClassName;
	private final String mMethodIdentifier;
	private final String mJarFile;
	
	
	MyLocalVariableSorter mLocalVariablesSorter;
	private List<Runnable> delayedInstruction = new ArrayList<>();
	private final List<Integer> leaders;
	private final HashMap<Integer, String> localVariablesMap;
	private int mLine;
	private int nLocals;
	private int nParams;
	private int count;
	private final boolean isConstructor;
	private boolean seenSuperConstructor;	
	private final boolean isImplemented;
	
	 private final Label start;
	 private final Label end;
     private final Label handler;
     private int maxStack;
     private int maxLocals;

	public MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String className, String[] exceptions,
			String jarFile) {
		super(api, mv);

		mMethodName = name;
		mMethodSignature = desc;
		mClassName = className;
		mMethodIdentifier = mClassName +"." + mMethodName + mMethodSignature;
		mJarFile = jarFile;

		System.out.println("Augmenting: "+ mMethodIdentifier);
		isConstructor = mMethodName.equals("<init>");
		seenSuperConstructor=false;
		count = 0;
		mLine = 0;

		leaders = (List<Integer>)BasicBlockGenerator._leadersPerMethod.get(mMethodIdentifier);
		isImplemented =leaders.size() > 0;
		
		System.out.print(mClassName + "/" + mMethodName + mMethodSignature + "{ ");
		for(int i = 0; i < leaders.size(); i++){
			System.out.print(leaders.get(i) + " ");
		}
		System.out.println("}");
		


		localVariablesMap = (HashMap<Integer, String>) BasicBlockGenerator._localsPerMethod.get(mMethodIdentifier);
		System.out.print("........................" + mClassName + "/" + mMethodName + mMethodSignature + "{ ");
		for(int key : localVariablesMap.keySet()){
			System.out.print(key + ":" + localVariablesMap.get(key) + " ");
		}
		System.out.println("}");

		// STATIC VS NON STATIC
		boolean isStatic = (access & ACC_STATIC) != 0;
		if(isStatic){
			System.out.println("Method is Static");
		}else{
			System.out.println("Method is NOT Static");
		}

		nLocals = 0;
		for(final int key : localVariablesMap.keySet()){
			nLocals = (key > nLocals)?key:nLocals;
		}		

		nParams = Type.getArgumentTypes(desc).length + (isStatic ? 0 : 1);

		if(nParams > nLocals && isConstructor){
			nLocals = nParams; 	
			// When in a constructor of an inner class, ALOAD 0 and ALOAD 1 are called
			// but only "this" is captured as a local variable
		}
		
		System.out.println("nLocals: " + nLocals);
		System.out.println("nParams: " + nParams);
		
		// Wrap the entire method in a try catch block
		start = new Label();
		end = new Label();
		handler = new Label();
		maxLocals = 0;
		maxStack= 0;	
	}

	@Override
	public void visitCode() {
		super.visitCode();	
		push(nLocals);
		push(nParams);
		addMethodInfo();
		super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleMethodEntry", 
				Type.getMethodDescriptor(VOID_TYPE, INT_TYPE, INT_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE), false);
	}

	@Override
	public void visitEnd() {

		// -------------------------------------------------------------------
		// wrap method in a try catch block
		if(isImplemented){
			visitLabel(handler);
			int index = mLocalVariablesSorter.createLocalVariable(OBJECT_TYPE);
			super.visitVarInsn(ASTORE, index);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleMethodExitException", Type.getMethodDescriptor(VOID_TYPE), false);
			super.visitVarInsn(ALOAD, index);
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false);
			super.visitLabel(end);
			
			super.visitTypeInsn(NEW, "java/lang/Throwable");
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", Type.getMethodDescriptor(VOID_TYPE), false);
			super.visitInsn(ATHROW);
			super.visitInsn(RETURN);
			super.visitMaxs(maxStack, maxLocals);
		}
		// -------------------------------------------------------------------

	    
		super.visitEnd();
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		// -------------------------------------------------------------------
		// Wrap method in a try catch block
		if(isImplemented){
			/*
			 * visitMaxs should be called directly before ending the method, so call is moved from here to visitEnd()
			 */
			this.maxStack = maxStack;
			this.maxLocals = maxLocals;
		}
		// -------------------------------------------------------------------
		else{
			super.visitMaxs(maxStack, maxLocals);
		}
	}
	
	@Override
	public void visitParameter(String name, int access) {
		super.visitParameter(name, access);	
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
//			System.out.println(OPCODE_NAMES[opcode] + " " + var);
		
		
		checkForBasicBlock();

		if(opcode == ILOAD
				||   opcode == LLOAD
				||	 opcode == FLOAD
				||	 opcode == DLOAD
				||	 opcode == ALOAD){
			localVariableUse(var);
		}
		else if(opcode == ISTORE
				||    opcode == LSTORE
				||	  opcode == FSTORE
				||	  opcode == DSTORE
				||	  opcode == ASTORE){
			localVariableDef(var);
		}

		super.visitVarInsn(opcode, var);
		count++;
	}

	@Override
	public void visitLabel(Label label) {
		
		// -------------------------------------------------------------------
		// Wrap method in try catch block		
		if((count == 0) && !isConstructor && isImplemented){
			super.visitTryCatchBlock(start, end, handler, "java/lang/Throwable");
			super.visitLabel(start);
		}
		// -------------------------------------------------------------------

		super.visitLabel(label);
		
		
		count++;
//		System.out.println("label offset: " + label.getOffset());
	}


	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    	
		checkForBasicBlock();
		
		//System.out.println(OPCODE_NAMES[opcode] + " " + owner + "." + name + " " + desc);

		if(opcode == INVOKESPECIAL && isConstructor && !seenSuperConstructor){
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			count++;
			System.out.println("  First invokeSpecial goes to: "+ owner + " / " + name);
			seenSuperConstructor=true;
			
			// -------------------------------------------------------------------
			// Wrap method in try catch block
			if(isConstructor && isImplemented){
				super.visitTryCatchBlock(start, end, handler, "java/lang/Exception");
				super.visitLabel(start);
			}
			// -------------------------------------------------------------------

			
			while(!delayedInstruction.isEmpty()){
				Runnable r = delayedInstruction.remove(0);
				r.run();
			}
			System.out.println("  Successfully added delayed code");
		}else{

			if(opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE){
				addMethodInfo();
				push(count);
				push(mLine);
				super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleAllMethodCall", 
						Type.getMethodDescriptor(VOID_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);

			}

			else if(opcode == INVOKESPECIAL || opcode == INVOKESTATIC){
				addMethodInfo();
				push(count);
				push(mLine);
				super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleAllMethodCall", 
						Type.getMethodDescriptor(VOID_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			}

			super.visitMethodInsn(opcode, owner, name, desc, itf);
			count++;
		}
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		checkForBasicBlock();
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		count++;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		checkForBasicBlock();

		super.visitJumpInsn(opcode, label);
		count++;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
		checkForBasicBlock();
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc){        
		checkForBasicBlock();

		if(opcode == GETFIELD){
			super.visitInsn(DUP);
			super.visitLdcInsn(name);
			super.visitLdcInsn(owner);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, "javaprofiler/dataflow/dupair/Profiler", "handleInstanceFieldUse",
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE,STRING_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
		}

		else if(opcode == GETSTATIC){
			super.visitLdcInsn(name);
			super.visitLdcInsn(owner);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, "javaprofiler/dataflow/dupair/Profiler", "handleStaticFieldUse", 
					Type.getMethodDescriptor(VOID_TYPE,  STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE,
							INT_TYPE, INT_TYPE), false);
		}

		else if(opcode == PUTFIELD){
			if(isConstructor && !seenSuperConstructor){
				System.out.println("  Setting: "+ name +" before invokeSpecial");
				int tempCount = count;
				int line = mLine;
//				delayedInstruction.add(() -> super.visitVarInsn(ALOAD, 0));
//				delayedInstruction.add(() -> super.visitLdcInsn(name));
//				delayedInstruction.add(() -> super.visitLdcInsn(owner));
//				delayedInstruction.add(() -> addMethodInfo());
//				delayedInstruction.add(() -> push(tempCount));
//				delayedInstruction.add(() -> push(line));
//				delayedInstruction.add(() -> super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleInstanceFieldDef", 
//						Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE,
//								INT_TYPE, INT_TYPE), false));
				

				// JDK7 compatible
				delayedInstruction.add(new ALOAD_0_Instruction(this));
				delayedInstruction.add(new LDC_Instruction(this, name));
				delayedInstruction.add(new LDC_Instruction(this, owner));
				delayedInstruction.add(new LDC_Instruction(this, mClassName));
				delayedInstruction.add(new LDC_Instruction(this, mMethodName));
				delayedInstruction.add(new LDC_Instruction(this, mMethodSignature));
				delayedInstruction.add(new Push_Instruction(this, tempCount));
				delayedInstruction.add(new Push_Instruction(this, line));
				delayedInstruction.add(new Method_Instruction(this, "handleInstanceFieldDef", 
						Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE,
								INT_TYPE, INT_TYPE)));
			}else{
				Type type = Type.getType(desc);
				if(type == LONG_TYPE || type == DOUBLE_TYPE){
					super.visitInsn(DUP2_X1);
					super.visitInsn(POP2);
					super.visitInsn(DUP2_X2);
				}else{
					super.visitInsn(DUP2);
					super.visitInsn(POP);
				}
				super.visitLdcInsn(name);
				super.visitLdcInsn(owner);
				addMethodInfo();
				push(count);
				push(mLine);
				super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleInstanceFieldDef", 
						Type.getMethodDescriptor(VOID_TYPE,  
								OBJECT_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE,
								INT_TYPE, INT_TYPE), false);
			}
		}


		else if (opcode == PUTSTATIC) {
			super.visitLdcInsn(owner);
			super.visitLdcInsn(name);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleStaticFieldUse", 
					Type.getMethodDescriptor(VOID_TYPE,  STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE,
							INT_TYPE, INT_TYPE), false);
		}

		super.visitFieldInsn(opcode, owner, name, desc);

		count++;
	}

	
	@Override
	public void visitInsn(int opcode) {		
		checkForBasicBlock();
		if(opcode == AALOAD
				||  opcode == BALOAD
				||  opcode == CALOAD
				||  opcode == DALOAD
				||  opcode == FALOAD
				||  opcode == IALOAD
				||  opcode == LALOAD
				||  opcode == SALOAD){

			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementUse",
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, 
							STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
		}

		else if(opcode == AASTORE){
			int index = mLocalVariablesSorter.createLocalVariable(OBJECT_TYPE);
			super.visitVarInsn(ASTORE, index);
			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementDef", 
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			super.visitVarInsn(ALOAD, index);
		}

		else if(opcode == BASTORE
				|| opcode == CASTORE
				|| opcode == IASTORE
				|| opcode == SASTORE){
			int index = mLocalVariablesSorter.createLocalVariable(INT_TYPE);
			super.visitVarInsn(ISTORE, index);
			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementDef", 
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			super.visitVarInsn(ILOAD, index);
		}

		else if (opcode == DASTORE) {
			int index = mLocalVariablesSorter.createLocalVariable(DOUBLE_TYPE);
			super.visitVarInsn(DSTORE, index);
			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementDef", 
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			super.visitVarInsn(DLOAD, index);
		}

		else if(opcode == FASTORE){
			int index = mLocalVariablesSorter.createLocalVariable(FLOAT_TYPE);
			super.visitVarInsn(FSTORE, index);
			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementDef", 
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			super.visitVarInsn(FLOAD, index);
		}

		else if(opcode == LASTORE){
			int index = mLocalVariablesSorter.createLocalVariable(LONG_TYPE);
			super.visitVarInsn(LSTORE, index);
			super.visitInsn(DUP2);
			addMethodInfo();
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleArrayElementDef", 
					Type.getMethodDescriptor(VOID_TYPE, OBJECT_TYPE, INT_TYPE, STRING_TYPE,
							STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), false);
			super.visitVarInsn(LLOAD, index);
		}

		else if(
				opcode == ARETURN
				|| opcode == DRETURN
				|| opcode == FRETURN
				|| opcode == IRETURN
				|| opcode == LRETURN
				|| opcode == RETURN
				){
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleMethodExit", 
					Type.getMethodDescriptor(VOID_TYPE), false);
		}

		//System.out.println(OPCODE_NAMES[opcode]);
		super.visitInsn(opcode);

		count++;
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		super.visitFrame(type, nLocal, local, nStack, stack);
		checkForBasicBlock();
		count++;
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		checkForBasicBlock();

		localVariableUse(var);
		localVariableDef(var);

		super.visitIincInsn(var, increment);

		count++;
	}



	@Override
	public void visitLdcInsn(Object cst) {
		checkForBasicBlock();

		super.visitLdcInsn(cst);

		count++;
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		checkForBasicBlock();
		super.visitIntInsn(opcode, operand);
		count++;
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		mLine = line;
		super.visitLineNumber(line, start);
		count++;		
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		checkForBasicBlock();
		super.visitLookupSwitchInsn(dflt, keys, labels);
		count++;
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		checkForBasicBlock();
		super.visitMultiANewArrayInsn(desc, dims);
		count++;
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		checkForBasicBlock();	
		super.visitTableSwitchInsn(min, max, dflt, labels);
		count++;
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		checkForBasicBlock();
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		super.visitTypeInsn(opcode, type);
		count++;
	}


	// ############################SUPPORT FUNCTIONS##############################//
	private void checkForBasicBlock(){
		if(leaders.contains(count)){
			push(count);
			push(mLine);
			super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleBBEntry",
					Type.getMethodDescriptor(VOID_TYPE, INT_TYPE, INT_TYPE), false);
		}
	}

	/**
	 * returns {@link #CONSUME_STACK}[opcode] as per BCEL @since 6.0
	 * @param opcode
	 * @return Number of words consumed on operand stack
	 */
	private static int getConsumeStack(final int opcode) {
		return CONSUME_STACK[opcode];
	}



	/**
	 * returns {@link #PRODUCE_STACK}[opcode] as per BCEL @since 6.0
	 * @param opcode
	 * @return Number of words produced onto operand stack
	 */
	public static int getProduceStack(final int opcode) {
		return PRODUCE_STACK[opcode];
	}

	public final void push(final int value) {
		//System.out.println(value);
		if(value >= -1 && value <= 5) {
			super.visitInsn(ICONST_0 + value);
		} else if(value == (byte)value) {
			super.visitIntInsn(BIPUSH, value);
		} else if(value == (short)value) {
			super.visitIntInsn(SIPUSH, value);
		} else {
			super.visitLdcInsn(value);
		}
	}

	private String getLocalVariableName(final int index){
		if(localVariablesMap == null){
			return "var_"+index;
		}
		if(localVariablesMap.containsKey(index)){
			return localVariablesMap.get(index);
		}
		return "var_"+index;
	}

	private void localVariableUse(final int index){
		addMethodInfo();
		push(index);
		super.visitLdcInsn(getLocalVariableName(index));
		push(count);
		push(mLine);
		super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleLocalVariableUse", 
				Type.getMethodDescriptor(VOID_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE), 
				false);
	}

	private void localVariableDef(final int index){
		addMethodInfo();
		push(index);
		push(count);
		push(mLine);
		super.visitMethodInsn(INVOKESTATIC, PROFILER, "handleLocalVariableDef", 
				Type.getMethodDescriptor(VOID_TYPE, STRING_TYPE, STRING_TYPE, STRING_TYPE, INT_TYPE, INT_TYPE, INT_TYPE), 
				false);
	}

	public final void addMethodInfo(){
		super.visitLdcInsn(mClassName);
		super.visitLdcInsn(mMethodName);
		super.visitLdcInsn(mMethodSignature);
	}

	public final void ALOAD_func(final int index){super.visitVarInsn(ALOAD, index);}
	public final void Ldc_func(final Object cst){super.visitLdcInsn(cst);}
	public final void Method_func(final String name, final String desc){
		super.visitMethodInsn(INVOKESTATIC, PROFILER, name, desc, false);
	}




	/** Mnemonic for an illegal opcode. */
	private static final String ILLEGAL_OPCODE = "<illegal opcode>";

	/** cannot be computed statically */
	private static final int UNPREDICTABLE = -1;

	/** Illegal opcode. */
	private static final int UNDEFINED = -2;

	/**
	 * Number of words consumed on operand stack by instructions.
	 * Indexed by opcode.  CONSUME_STACK[FALOAD] = number of words
	 * consumed from the stack by a faload instruction.
	 */
	private static final int[] CONSUME_STACK = {
			0/*nop*/, 				0/*aconst_null*/,	0/*iconst_m1*/, 	0/*iconst_0*/, 		0/*iconst_1*/,
			0/*iconst_2*/, 			0/*iconst_3*/, 		0/*iconst_4*/, 		0/*iconst_5*/, 		0/*lconst_0*/,
			0/*lconst_1*/, 			0/*fconst_0*/, 		0/*fconst_1*/, 		0/*fconst_2*/,		0/*dconst_0*/,
			0/*dconst_1*/, 			0/*bipush*/, 		0/*sipush*/, 		0/*ldc*/, 			0/*ldc_w*/, 
			0/*ldc2_w*/, 			0/*iload*/,			0/*lload*/, 		0/*fload*/, 		0/*dload*/, 		
			0/*aload*/, 			0/*iload_0*/, 		0/*iload_1*/, 		0/*iload_2*/,		0/*iload_3*/,
			0/*lload_0*/,	 		0/*lload_1*/, 		0/*lload_2*/, 		0/*lload_3*/, 		0/*fload_0*/,
			0/*fload_1*/,			0/*fload_2*/, 		0/*fload_3*/, 		0/*dload_0*/, 		0/*dload_1*/, 	
			0/*dload_2*/,			0/*dload_3*/,		0/*aload_0*/, 		0/*aload_1*/, 		0/*aload_2*/, 	
			0/*aload_3*/, 			2/*iaload*/,		2/*laload*/, 		2/*faload*/, 		2/*daload*/,
			2/*aaload*/, 			2/*baload*/, 		2/*caload*/, 		2/*saload*/,		1/*istore*/,
			2/*lstore*/, 			1/*fstore*/, 		2/*dstore*/, 		1/*astore*/, 		1/*istore_0*/,
			1/*istore_1*/,			1/*istore_2*/, 		1/*istore_3*/, 		2/*lstore_0*/, 		2/*lstore_1*/,
			2/*lstore_2*/, 			2/*lstore_3*/, 		1/*fstore_0*/, 		1/*fstore_1*/, 		1/*fstore_2*/,
			1/*fstore_3*/, 			2/*dstore_0*/, 		2/*dstore_1*/, 		2/*dstore_2*/, 		2/*dstore_3*/,
			1/*astore_0*/, 			1/*astore_1*/, 		1/*astore_2*/, 		1/*astore_3*/, 		3/*iastore*/, 
			4/*lastore*/,			3/*fastore*/, 		4/*dastore*/, 		3/*aastore*/, 		3/*bastore*/, 		
			3/*castore*/, 			3/*sastore*/,		1/*pop*/, 			2/*pop2*/, 			1/*dup*/, 
			2/*dup_x1*/, 			3/*dup_x2*/, 		2/*dup2*/, 			3/*dup2_x1*/,		4/*dup2_x2*/,
			2/*swap*/, 				2/*iadd*/, 			4/*ladd*/, 			2/*fadd*/, 			4/*dadd*/, 
			2/*isub*/, 				4/*lsub*/,			2/*fsub*/,			4/*dsub*/, 			2/*imul*/, 
			4/*lmul*/, 				2/*fmul*/, 			4/*dmul*/, 			2/*idiv*/, 			4/*ldiv*/,
			2/*fdiv*/, 				4/*ddiv*/, 			2/*irem*/, 			4/*lrem*/, 			2/*frem*/, 
			4/*drem*/, 				1/*ineg*/, 			2/*lneg*/,			1/*fneg*/, 			2/*dneg*/,
			2/*ishl*/, 				3/*lshl*/, 			2/*ishr*/, 			3/*lshr*/, 			2/*iushr*/, 
			3/*lushr*/,				2/*iand*/,			4/*land*/,	 		2/*ior*/, 			4/*lor*/, 
			2/*ixor*/, 				4/*lxor*/, 			0/*iinc*/,			1/*i2l*/, 			1/*i2f*/,
			1/*i2d*/, 				2/*l2i*/, 			2/*l2f*/, 			2/*l2d*/, 			1/*f2i*/, 
			1/*f2l*/,				1/*f2d*/, 			2/*d2i*/, 			2/*d2l*/, 			2/*d2f*/, 
			1/*i2b*/, 				1/*i2c*/, 			1/*i2s*/,			4/*lcmp*/, 			2/*fcmpl*/,
			2/*fcmpg*/, 			4/*dcmpl*/, 		4/*dcmpg*/, 		1/*ifeq*/, 			1/*ifne*/,
			1/*iflt*/, 				1/*ifge*/, 			1/*ifgt*/, 			1/*ifle*/, 			2/*if_icmpeq*/,
			2/*if_icmpne*/, 		2/*if_icmplt*/,		2 /*if_icmpge*/,	2/*if_icmpgt*/, 	2/*if_icmple*/, 
			2/*if_acmpeq*/, 		2/*if_acmpne*/,		0/*goto*/,			0/*jsr*/, 			0/*ret*/, 
			1/*tableswitch*/, 		1/*lookupswitch*/, 	1/*ireturn*/,		2/*lreturn*/, 		1/*freturn*/,
			2/*dreturn*/, 			1/*areturn*/, 		0/*return*/, 		0/*getstatic*/,		UNPREDICTABLE/*putstatic*/, 		
			1/*getfield*/, 		
			UNPREDICTABLE/*putfield*/,	
			UNPREDICTABLE/*invokevirtual*/, 	
			UNPREDICTABLE/*invokespecial*/,
			UNPREDICTABLE/*invokestatic*/,
			UNPREDICTABLE/*invokeinterface*/, 	
			UNPREDICTABLE/*invokedynamic*/, 
			0/*new*/, 			1/*newarray*/, 		1/*anewarray*/,		
			1/*arraylength*/,		1/*athrow*/,		1/*checkcast*/, 	1/*instanceof*/, 	1/*monitorenter*/,	
			1/*monitorexit*/,		UNDEFINED,			
			UNPREDICTABLE/*multianewarray*/, 
			1/*ifnull*/, 		1/*ifnonnull*/,
	};

	/**
	 * Number of words produced onto operand stack by instructions.
	 * Indexed by opcode.  CONSUME_STACK[DALOAD] = number of words
	 * consumed from the stack by a daload instruction.
	 */
	private static final int[] PRODUCE_STACK = {
			0/*nop*/, 1/*aconst_null*/, 1/*iconst_m1*/, 1/*iconst_0*/, 1/*iconst_1*/,
			1/*iconst_2*/, 1/*iconst_3*/, 1/*iconst_4*/, 1/*iconst_5*/, 2/*lconst_0*/,
			2/*lconst_1*/, 1/*fconst_0*/, 1/*fconst_1*/, 1/*fconst_2*/, 2/*dconst_0*/,
			2/*dconst_1*/, 1/*bipush*/, 1/*sipush*/, 1/*ldc*/, 1/*ldc_w*/, 2/*ldc2_w*/, 1/*iload*/,
			2/*lload*/, 1/*fload*/, 2/*dload*/, 1/*aload*/, 1/*iload_0*/, 1/*iload_1*/, 1/*iload_2*/,
			1/*iload_3*/, 2/*lload_0*/, 2/*lload_1*/, 2/*lload_2*/, 2/*lload_3*/, 1/*fload_0*/,
			1/*fload_1*/, 1/*fload_2*/, 1/*fload_3*/, 2/*dload_0*/, 2/*dload_1*/, 2/*dload_2*/,
			2/*dload_3*/, 1/*aload_0*/, 1/*aload_1*/, 1/*aload_2*/, 1/*aload_3*/, 1/*iaload*/,
			2/*laload*/, 1/*faload*/, 2/*daload*/, 1/*aaload*/, 1/*baload*/, 1/*caload*/, 1/*saload*/,
			0/*istore*/, 0/*lstore*/, 0/*fstore*/, 0/*dstore*/, 0/*astore*/, 0/*istore_0*/,
			0/*istore_1*/, 0/*istore_2*/, 0/*istore_3*/, 0/*lstore_0*/, 0/*lstore_1*/,
			0/*lstore_2*/, 0/*lstore_3*/, 0/*fstore_0*/, 0/*fstore_1*/, 0/*fstore_2*/,
			0/*fstore_3*/, 0/*dstore_0*/, 0/*dstore_1*/, 0/*dstore_2*/, 0/*dstore_3*/,
			0/*astore_0*/, 0/*astore_1*/, 0/*astore_2*/, 0/*astore_3*/, 0/*iastore*/, 0/*lastore*/,
			0/*fastore*/, 0/*dastore*/, 0/*aastore*/, 0/*bastore*/, 0/*castore*/, 0/*sastore*/,
			0/*pop*/, 0/*pop2*/, 2/*dup*/, 3/*dup_x1*/, 4/*dup_x2*/, 4/*dup2*/, 5/*dup2_x1*/,
			6/*dup2_x2*/, 2/*swap*/, 1/*iadd*/, 2/*ladd*/, 1/*fadd*/, 2/*dadd*/, 1/*isub*/, 2/*lsub*/,
			1/*fsub*/, 2/*dsub*/, 1/*imul*/, 2/*lmul*/, 1/*fmul*/, 2/*dmul*/, 1/*idiv*/, 2/*ldiv*/,
			1/*fdiv*/, 2/*ddiv*/, 1/*irem*/, 2/*lrem*/, 1/*frem*/, 2/*drem*/, 1/*ineg*/, 2/*lneg*/,
			1/*fneg*/, 2/*dneg*/, 1/*ishl*/, 2/*lshl*/, 1/*ishr*/, 2/*lshr*/, 1/*iushr*/, 2/*lushr*/,
			1/*iand*/, 2/*land*/, 1/*ior*/, 2/*lor*/, 1/*ixor*/, 2/*lxor*/,
			0/*iinc*/, 2/*i2l*/, 1/*i2f*/, 2/*i2d*/, 1/*l2i*/, 1/*l2f*/, 2/*l2d*/, 1/*f2i*/,
			2/*f2l*/, 2/*f2d*/, 1/*d2i*/, 2/*d2l*/, 1/*d2f*/,
			1/*i2b*/, 1/*i2c*/, 1/*i2s*/, 1/*lcmp*/, 1/*fcmpl*/, 1/*fcmpg*/,
			1/*dcmpl*/, 1/*dcmpg*/, 0/*ifeq*/, 0/*ifne*/, 0/*iflt*/, 0/*ifge*/, 0/*ifgt*/, 0/*ifle*/,
			0/*if_icmpeq*/, 0/*if_icmpne*/, 0/*if_icmplt*/, 0/*if_icmpge*/, 0/*if_icmpgt*/,
			0/*if_icmple*/, 0/*if_acmpeq*/, 0/*if_acmpne*/, 0/*goto*/, 1/*jsr*/, 0/*ret*/,
			0/*tableswitch*/, 0/*lookupswitch*/, 0/*ireturn*/, 0/*lreturn*/, 0/*freturn*/,
			0/*dreturn*/, 0/*areturn*/, 0/*return*/, UNPREDICTABLE/*getstatic*/, 0/*putstatic*/,
			UNPREDICTABLE/*getfield*/, 0/*putfield*/, UNPREDICTABLE/*invokevirtual*/,
			UNPREDICTABLE/*invokespecial*/, UNPREDICTABLE/*invokestatic*/,
			UNPREDICTABLE/*invokeinterface*/, UNPREDICTABLE/*invokedynamic*/, 1/*new*/, 1/*newarray*/, 1/*anewarray*/,
			1/*arraylength*/, 1/*athrow*/, 1/*checkcast*/, 1/*instanceof*/, 0/*monitorenter*/,
			0/*monitorexit*/, UNDEFINED, 1/*multianewarray*/, 0/*ifnull*/, 0/*ifnonnull*/,
	};	

	/**
	 * Names of opcodes.  Indexed by opcode.  OPCODE_NAMES[ALOAD] = "aload".
	 */
	private static final String[] OPCODE_NAMES = {
			"nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1",
			"iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0",
			"lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0",
			"dconst_1", "bipush", "sipush", "ldc", "ldc_w", "ldc2_w", "iload",
			"lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2",
			"iload_3", "lload_0", "lload_1", "lload_2", "lload_3", "fload_0",
			"fload_1", "fload_2", "fload_3", "dload_0", "dload_1", "dload_2",
			"dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload",
			"laload", "faload", "daload", "aaload", "baload", "caload", "saload",
			"istore", "lstore", "fstore", "dstore", "astore", "istore_0",
			"istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1",
			"lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2",
			"fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3",
			"astore_0", "astore_1", "astore_2", "astore_3", "iastore", "lastore",
			"fastore", "dastore", "aastore", "bastore", "castore", "sastore",
			"pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1",
			"dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub",
			"fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv",
			"fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg",
			"fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr",
			"iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f",
			"i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f",
			"i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg",
			"dcmpl", "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle",
			"if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt",
			"if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret",
			"tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn",
			"dreturn", "areturn", "return", "getstatic", "putstatic", "getfield",
			"putfield", "invokevirtual", "invokespecial", "invokestatic",
			"invokeinterface", "invokedynamic", "new", "newarray", "anewarray",
			"arraylength", "athrow", "checkcast", "instanceof", "monitorenter",
			"monitorexit", ILLEGAL_OPCODE, "multianewarray", "ifnull", "ifnonnull"
	};	
	
	
	
	class ALOAD_0_Instruction implements Runnable{
		MyMethodVisitor mv;
		ALOAD_0_Instruction(MyMethodVisitor mv){
			this.mv = mv;
		}
		@Override
		public void run() {
			mv.ALOAD_func(0);
		}
	}
	class LDC_Instruction implements Runnable {
		MyMethodVisitor mv;
		final Object obj;
		public LDC_Instruction(MyMethodVisitor mv, final Object obj){
			this.mv = mv;
			this.obj = obj;
		}
		@Override
		public void run() {
			mv.Ldc_func(obj);
		}
	}
	class Push_Instruction implements Runnable{
		MyMethodVisitor mv;
		final int cst; 
		public Push_Instruction(MyMethodVisitor mv, final int cst) {
			this.mv = mv;
			this.cst = cst;
		}
		@Override
		public void run() {
			mv.push(cst);
		}
	}
	class Method_Instruction implements Runnable{
		MyMethodVisitor mv;
		final String methodName;
		final String methodDesc;
		public Method_Instruction(MyMethodVisitor mv, final String name, final String desc) {
			this.mv = mv;
			this.methodName = name;
			this.methodDesc = desc;
			
		}
		@Override
		public void run() {
			mv.Method_func(methodName, methodDesc);
		}
	}
}
