package com.asmproj;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodNode;

import java.util.Hashtable;


// TODO: we need to find a solution for mSignature. we need to map m_strMethodSignature to desc

public class MyMethodVisitor extends MethodVisitor {

	protected final String mMethodName;
	protected final String mMethodSignature;
	protected final MethodNode mMethodNode;
	//    protected final LocalVariablesSorter mLocalVariablesSorter;
	protected final MyLocalVariableSorter mLocalVariablesSorter;

	protected int mCounter = 0;// need to increment it at the end of each visit

	//	protected AnalyzerAdapter aa;


	protected int m_localLocationCount;
	protected LocalInfo[] m_localLocation;
	protected int m_nLocals;

	protected String m_strParamWrapper;
	protected static Hashtable _anyHash1 = new Hashtable();
	protected static Hashtable _anyHash2 = new Hashtable();
	protected static Hashtable<String, Boolean> _instrument = new Hashtable();

	protected String newLocalVariableName = null;

	public static int _nTotalStatements = 0;
	public static int _nTotalProbes = 0;


	public MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
		super(api, mv);
		mMethodNode = new MethodNode(access, signature, signature, signature, exceptions);
		mLocalVariablesSorter = new MyLocalVariableSorter(api, access, desc, mv);
		mMethodName = name;
		mMethodSignature = desc;

		_instrument.put("Field", Boolean.TRUE);
		_instrument.put("DefUse", Boolean.TRUE);
		_instrument.put("MethodCall", Boolean.TRUE);

	}

	@Override
	public void visitCode() {
		super.visitCode();
		super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		super.visitLdcInsn("method: " + mMethodName + " " + mMethodSignature);
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
		m_localLocationCount = 0;
		m_localLocation = new LocalInfo[100000];
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
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
				if (!(var == 0 && mMethodName.equals("<init>"))) {
					super.visitVarInsn(Opcodes.ALOAD, var);
					LoadOrStore(var, "handleLocalVariableObjectUse", "(Ljava/lang/Object;ILjava/lang/String;ILjava/lang/String;)V");
				}

			}
		}
		super.visitVarInsn(opcode, var);
		mCounter++;
	}

	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    	
		if(_instrument.get("MethodCall").booleanValue()){

			if(opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE){
				_nTotalProbes++;

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
					System.out.println("adding variable with index " + newVariableIndexes[i]);
				}

				// Create a local variable to save THIS
				int instanceVariableIndex =
						mLocalVariablesSorter.createLocalVariable(Type.getType(Object.class));
				System.out.println("adding new variable with index " + instanceVariableIndex);

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
				super.visitLdcInsn(mMethodSignature);
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
				super.visitLdcInsn(mMethodSignature);
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
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		mCounter++;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		super.visitJumpInsn(opcode, label);
		mCounter++;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
		System.out.println("Local variable: " + name + " with index " + index + " " + desc);
		super.visitLocalVariable(name, desc, signature, start, end, index);
		mCounter++;
		// TODO: do we need it here?
		//		mLocalVariablesSorter.incrementLocalCounter(index);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc){        
		if(_instrument.get("Field").booleanValue()){

			if(opcode == Opcodes.GETFIELD){
				Type fieldType = Type.getType(desc);

				int tempindex = mLocalVariablesSorter.createLocalVariable(Type.getType( (new Object()).getClass() ));

				mCounter += 10;

				super.visitVarInsn(Opcodes.ASTORE, tempindex);
				super.visitVarInsn(Opcodes.ALOAD, tempindex);
				super.visitInsn(Opcodes.DUP);
				super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
				super.visitLdcInsn(fieldType.getClassName());
				super.visitLdcInsn(name);
				super.visitLdcInsn(mCounter);
				super.visitLdcInsn(mMethodSignature);

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
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefINT", "(Ljava/lang/Object;ILjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					mCounter += 10;
					
					int valueTempIndex = mLocalVariablesSorter.newLocal(Type.DOUBLE_TYPE);
					int objectTempIndex = mLocalVariablesSorter.newLocal(Type.getType( (new Object()).getClass() ));
				
					super.visitVarInsn(Opcodes.DSTORE, valueTempIndex);
					super.visitVarInsn(Opcodes.ASTORE, objectTempIndex);
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.DLOAD, valueTempIndex);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefDOUBLE", "(Ljava/lang/Object;DLjava/lang/String;ILjava/lang/String;)V", false);
				
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.DLOAD, valueTempIndex);
				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefFLOAT", "(Ljava/lang/Object;FLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.LONG_TYPE)){
					mCounter += 10;
					
					int valueTempIndex = mLocalVariablesSorter.newLocal(Type.LONG_TYPE);
					int objectTempIndex = mLocalVariablesSorter.newLocal(Type.getType( (new Object()).getClass() ));
				
					super.visitVarInsn(Opcodes.LSTORE, valueTempIndex);
					super.visitVarInsn(Opcodes.ASTORE, objectTempIndex);
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.LLOAD, valueTempIndex);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldDefLONG", "(Ljava/lang/Object;JLjava/lang/String;ILjava/lang/String;)V", false);
				
					super.visitVarInsn(Opcodes.ALOAD, objectTempIndex);
					super.visitVarInsn(Opcodes.LLOAD, valueTempIndex);
				}else{// reference type
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleInstanceFieldObjectDef", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;ILjava/lang/String;)V", false);
				}
			} else if(opcode == Opcodes.GETSTATIC){ //GETSTATIC
				Type fieldType = Type.getType(desc);

				mCounter += 6;
				super.visitFieldInsn(opcode, owner, name, desc);
				super.visitLdcInsn(fieldType.getClassName());
				super.visitLdcInsn(name);
				super.visitLdcInsn(mCounter);
				super.visitLdcInsn(mMethodSignature);

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
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefINT", "(ILjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.DOUBLE_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefDOUBLE", "(DLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.FLOAT_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefFLOAT", "(FLjava/lang/String;ILjava/lang/String;)V", false);
				}else if(fieldType.equals(Type.LONG_TYPE)){
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP2);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldDefLONG", "(JLjava/lang/String;ILjava/lang/String;)V", false);
				}else{// reference type
					mCounter += 5;
					
					super.visitInsn(Opcodes.DUP);
					super.visitLdcInsn(strParamWrapperField);
					super.visitLdcInsn(mCounter);
					super.visitLdcInsn(mMethodName);
					
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleStaticFieldObjectDef", "(Ljava/lang/Object;Ljava/lang/String;ILjava/lang/String;)V", false);
				}
			}
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		mCounter++;
	}

	@Override
	public void visitInsn(int opcode) {
		if(_instrument.get("DefUse").booleanValue()){
			
			 if (opcode == Opcodes.AASTORE) {
                int index = mLocalVariablesSorter.createLocalVariable(Type.getType(Object.class));

                mCounter += 7;
            super.visitVarInsn(Opcodes.ASTORE, index);
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.ALOAD, index);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodSignature);
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/asmproj/IFProfiler",
                    "handleArrayElementObjectDef",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, Type.getType(Object.class), Type.INT_TYPE, Type.getType(String.class)),
                    false);
            super.visitVarInsn(Opcodes.ALOAD, index);
        } else if (opcode == Opcodes.DASTORE) {
            int index = mLocalVariablesSorter.createLocalVariable(Type.DOUBLE_TYPE);

            mCounter += 7;
            super.visitVarInsn(Opcodes.DSTORE, index);
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.DLOAD, index);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodSignature);
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/asmproj/IFProfiler",
                    "handleArrayElementDefDOUBLE",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, Type.DOUBLE_TYPE, Type.INT_TYPE, Type.getType(String.class)),
                    false);
            super.visitVarInsn(Opcodes.DLOAD, index);

        } else if (opcode == Opcodes.FASTORE) {
            int index = mLocalVariablesSorter.createLocalVariable(Type.FLOAT_TYPE);

            mCounter += 7;
            super.visitVarInsn(Opcodes.FSTORE, index);
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.FLOAD, index);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodSignature);
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/asmproj/IFProfile",
                    "handleArrayElementDefFLOAT",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, Type.FLOAT_TYPE, Type.INT_TYPE, Type.getType(String.class)),
                    false);
            super.visitVarInsn(Opcodes.FLOAD, index);
        } else if (opcode == Opcodes.LASTORE) {
            int index = mLocalVariablesSorter.createLocalVariable(Type.LONG_TYPE);

            mCounter += 7;
            super.visitVarInsn(Opcodes.LSTORE, index);
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.LLOAD, index);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodSignature);
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/asmproj/IFProfile",
                    "handleArrayElementDefLONG",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, Type.LONG_TYPE, Type.INT_TYPE, Type.getType(String.class)),
                    false);
            super.visitVarInsn(Opcodes.LLOAD, index);
        } else if (opcode == Opcodes.BASTORE ||
                opcode == Opcodes.CASTORE ||
                opcode == Opcodes.IASTORE ||
                opcode == Opcodes.SASTORE) {
            arrayStore(Type.INT_TYPE, Opcodes.ISTORE, Opcodes.ILOAD, "handleArrayElementDefINT");
        }
		}
		super.visitInsn(opcode);
		mCounter++;
	}




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

	protected void LoadOrStore(int index, String methodHandler, String arguments){
		super.visitLdcInsn(index);
		super.visitLdcInsn("var" + index);
		super.visitLdcInsn(mCounter);
		super.visitLdcInsn(mMethodSignature);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", methodHandler, arguments, false);

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
        super.visitLdcInsn(mCounter);
        super.visitLdcInsn(mMethodSignature);
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                "com/asmproj/IFProfiler",
                methodName,
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.INT_TYPE, type, Type.INT_TYPE, Type.getType(String.class)),
                false);
        super.visitVarInsn(loadOpCode, index);
    }
}
