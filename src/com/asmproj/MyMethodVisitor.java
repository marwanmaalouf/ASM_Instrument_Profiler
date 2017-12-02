package com.asmproj;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


// TODO: we need to find a solution for mSignature. we need to map m_strMethodSignature to desc

public class MyMethodVisitor extends MethodVisitor {

	protected final String mMethodName;
	protected final String mMethodSignature;
	protected final MethodNode mMethodNode;
	protected final String mClassName;
	protected final String mMethodIdentifier;
	
	protected final MyLocalVariableSorter mLocalVariablesSorter;

	protected int mCounter = 0;// need to increment it at the end of each visit

	protected int m_localLocationCount;
	protected LocalInfo[] m_localLocation;
	protected int m_nLocals;

	protected String m_strParamWrapper;
	protected static Hashtable<String, Boolean> _instrument = new Hashtable();

	protected String newLocalVariableName = null;
	
	
	protected final List<Integer> leaders;
	protected boolean foundInstruction;
	protected int line = -1;
	protected int count = 0;
	protected static int methodCallInstruction = -1;
	
	public MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String className, String[] exceptions) {
		super(api, mv);
		mMethodNode = new MethodNode(access, name, desc, signature, exceptions);
		mLocalVariablesSorter = new MyLocalVariableSorter(api, access, desc, mv);
		mMethodName = name;
		mMethodSignature = desc;
		mClassName = className;
		mMethodIdentifier = mClassName +"." + mMethodName + mMethodSignature;
		
		foundInstruction = false;
		count = 0;
		
		leaders = (List<Integer>)BasicBlockGenerator._leadersPerMethod.get(mMethodIdentifier); 
		System.out.print(mClassName + "/" + mMethodName + mMethodSignature + "{ ");
		for(int i = 0; i < leaders.size(); i++){
			System.out.print(leaders.get(i) + " ");
		}
		System.out.println("}");
		
		_instrument.put("Field", Boolean.TRUE);
		_instrument.put("DefUse", Boolean.TRUE);
		_instrument.put("MethodCall", Boolean.FALSE);
		_instrument.put("MethodCoverage", Boolean.TRUE);
	}

	private List<String> methodParamsName = null;
	
	@Override
	public void visitCode() {
		super.visitCode();
//		super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//		super.visitLdcInsn("method: " + mMethodIdentifier);
//		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//		

		
		// Handle method Entry
		super.visitLdcInsn("TBD");
		super.visitLdcInsn(-1);
		super.visitLdcInsn(-1);
		super.visitLdcInsn(mClassName);
		super.visitLdcInsn(mMethodName);
		super.visitLdcInsn(mMethodSignature);
		if(mMethodName.equals("main")){
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleMainMethodEntry", 
					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE, Type.INT_TYPE, Type.getType(String.class),
							Type.getType(String.class), Type.getType(String.class)), false);
		}else{
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleMethodEntry", 
					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE, Type.INT_TYPE, Type.getType(String.class),
							Type.getType(String.class), Type.getType(String.class)), false);
		}		
		
		
		if(methodParamsName != null){
			for(int i = 0; i < methodParamsName.size(); i++){
				System.out.println("Adding parameter " + methodParamsName.get(i) +" with index " + i + " and instruction " + methodCallInstruction);
		  	  	super.visitLdcInsn(methodParamsName.get(i));
		  	  	super.visitLdcInsn(methodCallInstruction);
		  	  	super.visitLdcInsn(mMethodIdentifier);
		  	  	super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleParameterDef", 
							Type.getMethodDescriptor(
									Type.VOID_TYPE, 
									Type.getType(String.class), 
									Type.INT_TYPE, 
									Type.getType(String.class)), false);
			}

			System.out.println("Clearing parameters");
			methodParamsName = null;
					
		}
		
		
		
		m_localLocationCount = 0;
		m_localLocation = new LocalInfo[100000];
	}

	@Override
	public void visitEnd() {

		super.visitEnd();
	}
	
	@Override
	public void visitParameter(String name, int access) {
		System.out.println("parameter: " + name);
		if(methodParamsName == null){
			methodParamsName = new ArrayList<String>();
		}
		methodParamsName.add(name);
		super.visitParameter(name, access);
		
	}
	
	@Override
	public void visitVarInsn(int opcode, int var) {
		checkForBasicBlock();
		mLocalVariablesSorter.incrementLocalCounter(var);

		if(_instrument.get("DefUse").booleanValue()){
			// This should go in each store instance, but it will work if kept here for testing purposes

			if (opcode == Opcodes.ISTORE) {
				mCounter += 6;
				super.visitInsn(Opcodes.DUP);
				LoadOrStore(var, "handleLocalVariableDefINT", "(IILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.FSTORE) {
				mCounter += 6;
				super.visitInsn(Opcodes.DUP);
				LoadOrStore(var, "handleLocalVariableDefFLOAT", "(FILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.DSTORE) {
				mCounter += 6;
				super.visitInsn(Opcodes.DUP2);
				LoadOrStore(var, "handleLocalVariableDefDOUBLE", "(DILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.LSTORE) {
				mCounter += 6;
				super.visitInsn(Opcodes.DUP2);
				LoadOrStore(var, "handleLocalVariableDefLONG", "(JILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.ASTORE) {
				mCounter += 6;
				super.visitInsn(Opcodes.DUP);
				LoadOrStore(var, "handleLocalVariableObjectDef", "(Ljava/lang/Object;ILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.ILOAD) {
				mCounter += 6;
				super.visitVarInsn(Opcodes.ILOAD, var);
				LoadOrStore(var, "handleLocalVariableUseI", "(IILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.FLOAD) {
				mCounter += 6;
				super.visitVarInsn(Opcodes.FLOAD, var);
				LoadOrStore(var, "handleLocalVariableUseD", "(FILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.DLOAD) {
				mCounter += 6;
				super.visitVarInsn(Opcodes.DLOAD, var);
				LoadOrStore(var, "handleLocalVariableUseD", "(DILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.LLOAD) {
				mCounter += 6;
				super.visitVarInsn(Opcodes.LLOAD, var);
				LoadOrStore(var, "handleLocalVariableUseL", "(JILjava/lang/String;ILjava/lang/String;)V");

			} else if (opcode == Opcodes.ALOAD) {
				mCounter += 6;

				// TODO: take care of case UNINITIALIZED_THIS
				if (!(var == 0 && mMethodName.contains("<init>"))) {
					super.visitVarInsn(Opcodes.ALOAD, var);
					LoadOrStore(var, "handleLocalVariableObjectUse", "(Ljava/lang/Object;ILjava/lang/String;ILjava/lang/String;)V");
				}

			}
		}
		super.visitVarInsn(opcode, var);
		mCounter++;
		count++;
	}

	@Override
	public void visitLabel(Label label) {
		if(leaders.contains(count)){
			foundInstruction= true;
		}
		checkForBasicBlock();
		super.visitLabel(label);
		count++;
	}

	
	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    	
		checkForBasicBlock();
		methodCallInstruction = count + 1;
		if(opcode == Opcodes.INVOKESTATIC){
	  	  	super.visitLdcInsn(true);
	  	  	super.visitLdcInsn(methodCallInstruction);
	  	  	super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "toggleStatic", 
						Type.getMethodDescriptor(
								Type.VOID_TYPE, 
								Type.BOOLEAN_TYPE, 
								Type.INT_TYPE), false);
		}else{
			super.visitLdcInsn(false);
	  	  	super.visitLdcInsn(methodCallInstruction);
	  	  	super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "toggleStatic", 
						Type.getMethodDescriptor(
								Type.VOID_TYPE, 
								Type.BOOLEAN_TYPE, 
								Type.INT_TYPE), false);
		}
		
		
		if(_instrument.get("MethodCall").booleanValue()){

			if(opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE){

				// Store passed arguments
				String calledClassName = owner;
				String calledMethodName = name;
				String calledMethodSignature = desc;

				// Retrieve types of arguments and if there is a return value
				Type [] argumentTypes = Type.getArgumentTypes(desc); // get type of arguments
				Type returnType = Type.getReturnType(desc); // get return type
				boolean bReturnsValue = !(returnType.equals(Type.VOID_TYPE));

				// Create an array to log the index of the variables we will create to store the arguments
				int [] newVariableIndexes = new int[argumentTypes.length];
				for(int i = 0; i < argumentTypes.length; i++){
					newVariableIndexes[i] = mLocalVariablesSorter.createLocalVariable(argumentTypes[i]);
				}

				// Create a local variable to save THIS
				int instanceVariableIndex =
						mLocalVariablesSorter.createLocalVariable(Type.getType(Object.class));

				// Save the arguments if any
				if(argumentTypes.length > 0){
					mCounter += argumentTypes.length;
					for (int i = argumentTypes.length - 1; i >= 0; i--){
						super.visitVarInsn(createStore(argumentTypes[i]), newVariableIndexes[i]);
					}
					super.visitInsn(Opcodes.DUP);
					mCounter += 1;
				}else{
					super.visitInsn(Opcodes.DUP);
					mCounter += 1;
				}

				// Save the instance
				mCounter += 1;
				super.visitVarInsn(Opcodes.ASTORE, instanceVariableIndex);

				mCounter += 8;
				int callInstruction = mCounter + argumentTypes.length; // include the loads
				super.visitVarInsn(Opcodes.ALOAD, instanceVariableIndex);
				super.visitLdcInsn(calledMethodName);
				super.visitLdcInsn(calledMethodSignature);
				super.visitLdcInsn(argumentTypes.length);
				super.visitLdcInsn(callInstruction);
				super.visitLdcInsn(bReturnsValue);
				super.visitLdcInsn(mMethodIdentifier);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceMethodCall", 
						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;IIZLjava/lang/String;)V", false);


				// Restore the method arguments
				for (int j = 0; j < argumentTypes.length; j++)
				{
					super.visitVarInsn(createLoad(argumentTypes[j]), newVariableIndexes[j]);
				}

				super.visitMethodInsn(opcode, owner, name, desc, itf);
				mCounter++;
				
				mCounter = callInstruction + 8;
				
				super.visitVarInsn(Opcodes.ALOAD, instanceVariableIndex);
				super.visitLdcInsn(calledClassName);
				super.visitLdcInsn(calledMethodName);
				super.visitLdcInsn(calledMethodSignature);
				super.visitLdcInsn(callInstruction);
				super.visitLdcInsn(bReturnsValue);
				super.visitLdcInsn(mMethodIdentifier);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceMethodReturn", 
						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V", false);
			
			}
			else{
				super.visitMethodInsn(opcode, owner, name, desc, itf);
				mCounter++;
			}
		}else{
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			mCounter++;
		}
		count++;
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		checkForBasicBlock();
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		mCounter++;
		count++;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		checkForBasicBlock();
		super.visitJumpInsn(opcode, label);
		mCounter++;
		count++;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
		checkForBasicBlock();
		System.out.println("Local variable: " + name + " with index " + index + " " + desc);
		super.visitLocalVariable(name, desc, signature, start, end, index);
		mCounter++;
		//count++;
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc){        
		checkForBasicBlock();
		if(_instrument.get("Field").booleanValue()){
			
			
			if(opcode == Opcodes.GETFIELD){
				Type fieldType = Type.getType(desc);

				int tempindex = mLocalVariablesSorter.createLocalVariable(Type.getType( (new Object()).getClass() ));

				mCounter += 10;

				super.visitVarInsn(Opcodes.ASTORE, tempindex);
				super.visitVarInsn(Opcodes.ALOAD, tempindex);
				super.visitInsn(Opcodes.DUP);
				super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
				super.visitLdcInsn(owner);
				super.visitLdcInsn(name);
				super.visitLdcInsn(count);
				super.visitLdcInsn(mMethodIdentifier);

				if(fieldType.equals(Type.INT_TYPE) || fieldType.equals(Type.CHAR_TYPE) || fieldType.equals(Type.BOOLEAN_TYPE)
						|| fieldType.equals(Type.SHORT_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldUseINT", "(Ljava/lang/Object;ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldUseDOUBLE", "(Ljava/lang/Object;DLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldUseFLOAT", "(Ljava/lang/Object;FLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else if(fieldType.equals(Type.LONG_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldUseLONG", "(Ljava/lang/Object;JLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else{// reference type
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldObjectUse", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);
				}

				super.visitVarInsn(Opcodes.ALOAD, tempindex);
			} else if(opcode == Opcodes.PUTFIELD){
				Type fieldType = Type.getType(desc);
				
				String strParamWrapperField = fieldType.getClassName() + "|" + name;
	   
				if(fieldType.equals(Type.INT_TYPE) || fieldType.equals(Type.CHAR_TYPE) || fieldType.equals(Type.BOOLEAN_TYPE)
						|| fieldType.equals(Type.SHORT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefINT", "(Ljava/lang/Object;ILjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					mCounter += 10;
					
					int valueTempIndex = mLocalVariablesSorter.createLocalVariable(Type.DOUBLE_TYPE);
					int objectTempIndex = mLocalVariablesSorter.createLocalVariable(Type.getType( (new Object()).getClass() ));
				
					super.visitVarInsn(Opcodes.DSTORE, valueTempIndex);
					super.visitVarInsn(Opcodes.ASTORE, objectTempIndex);
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.DLOAD, valueTempIndex);
					super.visitLdcInsn(name);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodIdentifier);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefDOUBLE", "(Ljava/lang/Object;DLjava/lang/String;ILjava/lang/String;)V", false);
				
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.DLOAD, valueTempIndex);
				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefFLOAT", "(Ljava/lang/Object;FLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.LONG_TYPE)){
					mCounter += 10;
					
					int valueTempIndex = mLocalVariablesSorter.createLocalVariable(Type.LONG_TYPE);
					int objectTempIndex = mLocalVariablesSorter.createLocalVariable(Type.getType( Object.class ));
				
					super.visitVarInsn(Opcodes.LSTORE, valueTempIndex);
					super.visitVarInsn(Opcodes.ASTORE, objectTempIndex);
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.LLOAD, valueTempIndex);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefLONG", "(Ljava/lang/Object;JLjava/lang/String;ILjava/lang/String;)V", false);
				
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.LLOAD, valueTempIndex);
				}else{// reference type
					mCounter += 5;
					// TODO:Bug when we have an uninitialized this

					boolean uninithializedThis =mMethodName.contains("<init>") && name.equals("this$0"); 
					if(!uninithializedThis){
				
					
						super.visitInsn(Opcodes.DUP2);
					
						super.visitLdcInsn(name);
						super.visitLdcInsn(count);
						super.visitLdcInsn(mClassName);
					
						super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldObjectDef", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;ILjava/lang/String;)V", false);
				
					}
				}
			} else if(opcode == Opcodes.GETSTATIC){ //GETSTATIC
				Type fieldType = Type.getType(desc);

				mCounter += 6;
				super.visitFieldInsn(opcode, owner, name, desc);
				super.visitLdcInsn(owner);
				super.visitLdcInsn(name);
				super.visitLdcInsn(count);
				super.visitLdcInsn(mMethodIdentifier);

				if(fieldType.equals(Type.INT_TYPE) || fieldType.equals(Type.CHAR_TYPE) || fieldType.equals(Type.BOOLEAN_TYPE)
						|| fieldType.equals(Type.SHORT_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldUseINT", "(ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldUseDOUBLE", "(DLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldUseFLOAT", "(FLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else if(fieldType.equals(Type.LONG_TYPE)){
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldUseLONG", "(JLjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);

				}else{// reference type
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldObjectUse", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V", false);
				}
			} else if(opcode == Opcodes.PUTSTATIC){
				Type fieldType = Type.getType(desc);
				
				String strParamWrapperField = fieldType.getClassName() + "|" + name;
	   
				if(fieldType.equals(Type.INT_TYPE) || fieldType.equals(Type.CHAR_TYPE) || fieldType.equals(Type.BOOLEAN_TYPE)
						|| fieldType.equals(Type.SHORT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefINT", "(ILjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(name);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefDOUBLE", "(DLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefFLOAT", "(FLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.LONG_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefLONG", "(JLjava/lang/String;ILjava/lang/String;)V", false);
				}else{// reference type
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP);
					super.visitLdcInsn(name);
					super.visitLdcInsn(count);
					super.visitLdcInsn(mClassName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldObjectDef", "(Ljava/lang/Object;Ljava/lang/String;ILjava/lang/String;)V", false);
				}
			}
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		mCounter++;
		count++;
	}

	@Override
	public void visitInsn(int opcode) {
		checkForBasicBlock();
		if(_instrument.get("MethodCoverage")){
	       switch(opcode) {
	          case Opcodes.IRETURN:
	          case Opcodes.FRETURN:
	          case Opcodes.ARETURN:
	          case Opcodes.LRETURN:
	          case Opcodes.DRETURN:
	        	  super.visitLdcInsn(true);
	        	  super.visitLdcInsn(mCounter);
	        	  super.visitLdcInsn(mClassName);
	        	  super.visitLdcInsn(mMethodName);
	        	  super.visitLdcInsn(mMethodSignature);
	        	  super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleMethodExit", 
		  					Type.getMethodDescriptor(Type.VOID_TYPE, Type.BOOLEAN_TYPE, Type.INT_TYPE, Type.getType(String.class),
		  							Type.getType(String.class), Type.getType(String.class)), false);
	        	  break;
	          case Opcodes.RETURN:
	        	  super.visitLdcInsn(false);
	        	  super.visitLdcInsn(mCounter);
	        	  super.visitLdcInsn(mClassName);
	        	  super.visitLdcInsn(mMethodName);
	        	  super.visitLdcInsn(mMethodSignature);
	        	  super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleMethodExit", 
		  					Type.getMethodDescriptor(Type.VOID_TYPE, Type.BOOLEAN_TYPE, Type.INT_TYPE, Type.getType(String.class),
		  							Type.getType(String.class), Type.getType(String.class)), false);
	              break;
	          default: // do nothing
	        }
		}
		
		
		
		
		if(_instrument.get("DefUse").booleanValue()){
			
			if (opcode == Opcodes.AASTORE) {
                arrayStore(Type.getType(Object.class),Opcodes.ASTORE,Opcodes.ALOAD,"handleArrayElementObjectDef");
            } else if (opcode == Opcodes.DASTORE) {
                arrayStore(Type.DOUBLE_TYPE,Opcodes.DSTORE,Opcodes.DLOAD,"handleArrayElementDefDOUBLE");
            } else if (opcode == Opcodes.FASTORE) {
                arrayStore(Type.FLOAT_TYPE,Opcodes.FSTORE,Opcodes.FLOAD,"handleArrayElementDefFLOAT");
            } else if (opcode == Opcodes.LASTORE) {
                arrayStore(Type.LONG_TYPE, Opcodes.LSTORE, Opcodes.LLOAD, "handleArrayElementDefLONG");
            } else if (opcode == Opcodes.BASTORE ||
                    opcode == Opcodes.CASTORE ||
                    opcode == Opcodes.IASTORE ||
                    opcode == Opcodes.SASTORE) {
                arrayStore(Type.INT_TYPE, Opcodes.ISTORE, Opcodes.ILOAD, "handleArrayElementDefINT");
            } else if(opcode == Opcodes.CALOAD ||
                    opcode == Opcodes.IALOAD ||
                    opcode == Opcodes.BALOAD ||
                    opcode == Opcodes.SALOAD){
                arrayLoad(Type.INT_TYPE,opcode,"handleArrayElementUseINT");
            }else if(opcode == Opcodes.LALOAD){
                arrayLoad(Type.LONG_TYPE,opcode,"handleArrayElementUseLONG");
            }else if(opcode == Opcodes.FALOAD){
                arrayLoad(Type.FLOAT_TYPE,opcode,"handleArrayElementUseFLOAT");
            }else if(opcode == Opcodes.DALOAD){
                arrayLoad(Type.DOUBLE_TYPE,opcode,"handleArrayElementUseDOUBLE");
            }else if(opcode == Opcodes.AALOAD){
                arrayLoad(Type.getType(Object.class),opcode,"handleArrayElementObjectUse");
            }
		}
			
		super.visitInsn(opcode);
		mCounter++;
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
		super.visitLineNumber(line, start);
		if(foundInstruction){
			foundInstruction = false;
			this.line = line;
			System.out.println("Basic block @line: " + line);
		}
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
	/**
	 * Helper function, returns the XSTORE Opcode corresponding to the given type
	 * @param t: Type of the object to be stored
	 * @return
	 */
	protected int createStore(Type t){
		int opcode = -1;
		if(t.equals(Type.BYTE_TYPE) || t.equals(Type.BOOLEAN_TYPE) || t.equals(Type.CHAR_TYPE) 
				|| t.equals(Type.SHORT_TYPE) || t.equals(Type.INT_TYPE)){
			opcode = Opcodes.ISTORE;
		}else if(t.equals(Type.DOUBLE_TYPE)){
			opcode = Opcodes.DSTORE;
		}else if(t.equals(Type.FLOAT_TYPE)){
			opcode = Opcodes.FSTORE;
		}else if(t.equals(Type.LONG_TYPE)){
			opcode = Opcodes.LSTORE;
		}else{
			opcode = Opcodes.ASTORE; 
		}
		return opcode;
	}

	/**
	 * Helper function, returns the XLOAD Opcode corresponding to the given type
	 * @param t: Type of the object to be stored
	 * @return
	 */
	protected int createLoad(Type t){
		int opcode = -1;
		if(t.equals(Type.BYTE_TYPE) || t.equals(Type.BOOLEAN_TYPE) || t.equals(Type.CHAR_TYPE) 
				|| t.equals(Type.SHORT_TYPE) || t.equals(Type.INT_TYPE)){
			opcode = Opcodes.ILOAD;
		}else if(t.equals(Type.DOUBLE_TYPE)){
			opcode = Opcodes.DLOAD;
		}else if(t.equals(Type.FLOAT_TYPE)){
			opcode = Opcodes.FLOAD;
		}else if(t.equals(Type.LONG_TYPE)){
			opcode = Opcodes.LLOAD;
		}else{
			opcode = Opcodes.ALOAD; 
		}
		return opcode;
	}

	protected void LoadOrStore(int index, String methodHandler, String methodSignature){
		super.visitLdcInsn(index);
		super.visitLdcInsn("var_" + index);
		super.visitLdcInsn(count);
		super.visitLdcInsn(mMethodIdentifier);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", methodHandler, methodSignature, false);

		saveLocalLocation(mCounter, mCounter - 5, "var" + index);

	}

	void saveLocalLocation(int loadOrStore, int push, String oldName)
	{
		m_localLocation[m_localLocationCount] = new LocalInfo();
		m_localLocation[m_localLocationCount].pushLocation = push; // instruction number
		m_localLocation[m_localLocationCount].loadOrStoreLocation = loadOrStore; // instruction number
		m_localLocation[m_localLocationCount].oldName = oldName;

		m_localLocationCount++;
	}

    private void arrayStore(Type type, int storeOpCode, int loadOpCode, String methodName) {
        int index = mLocalVariablesSorter.createLocalVariable(type);

        mCounter += 7;
        super.visitVarInsn(storeOpCode, index);
        super.visitInsn(Opcodes.DUP2);
        super.visitVarInsn(loadOpCode, index);
        super.visitLdcInsn(count);
        super.visitLdcInsn(mMethodIdentifier);
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                "com/asmproj/IFProfiler",
                methodName,
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, type, Type.INT_TYPE, Type.getType(String.class)),
                false);
        super.visitVarInsn(loadOpCode, index);
    }
    
    private void checkForBasicBlock(){
		if(line != -1){
			super.visitLdcInsn(mClassName);
			super.visitLdcInsn(mMethodName);
			super.visitLdcInsn(mMethodSignature);
			super.visitLdcInsn(line);
			super.visitLdcInsn(mCounter);
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleBasicBlockEntry", 
					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class),
							Type.getType(String.class), Type.getType(String.class), Type.INT_TYPE, Type.INT_TYPE), false);
//			System.out.println("found basic block at line " + line);
			line = -1;
		}
    }
    
    
    private void arrayLoad(Type type, int loadOpcode, String methodName) {
        mCounter += 6;
        super.visitInsn(Opcodes.DUP2);
        super.visitInsn(Opcodes.DUP2);
        super.visitInsn(loadOpcode);
        super.visitLdcInsn(count);
        super.visitLdcInsn(mMethodIdentifier);
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                "com/asmproj/IFProfiler",
                methodName,
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, type, Type.INT_TYPE, Type.getType(String.class)),
                false);
    }
}
