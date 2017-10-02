package com.asmproj;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class MyMethodVisitor extends MethodVisitor {

    private final String mMethodName;
    private final MethodNode mMethodNode;
    private final LocalVariablesSorter mLocalVariablesSorter;

    protected int mCounter = 0;// need to increment it at the end of each visit

//	protected AnalyzerAdapter aa;


    protected int m_localLocationCount;
    protected LocalInfo[] m_localLocation;
    protected int m_nLocals;

    protected String m_strParamWrapper;
    protected String m_strMethodSignature;
    protected static Hashtable _anyHash1 = new Hashtable();
    protected static Hashtable _anyHash2 = new Hashtable();

    protected String newLocalVariableName = null;

    public static int _nTotalStatements = 0;
    public static int _nTotalProbes = 0;


    public MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        super(api, mv);
        mMethodNode = new MethodNode(access, signature, signature, signature, exceptions);
        mLocalVariablesSorter = new LocalVariablesSorter(access, desc, mv);
        mMethodName = name;
        this.m_strMethodSignature = signature;// TODO: gives null
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitLdcInsn("method: " + mMethodName);
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        m_localLocationCount = 0;
        m_localLocation = new LocalInfo[100000];
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
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

        super.visitVarInsn(opcode, var);
        mCounter++;
    }

    @Override 
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){
    	_nTotalProbes++;
    	
    	if(opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE){
    		String calledClassName = owner;
    		String calledMethodName = name;
			String calledMethodSignature = "";
			Type [] argumentTypes = Type.getArgumentTypes(desc); // get type of arguments
			Type returnType = Type.getReturnType(desc); // get return type
			int [] localVariableIndexes = new int[argumentTypes.length];
			boolean bReturnsValue = !(returnType.equals(Type.VOID_TYPE));
			
			System.out.println(Type.getType(Type.class));
			for(int i = 0; i < argumentTypes.length; i++){
				localVariableIndexes[i] = mLocalVariablesSorter.newLocal(argumentTypes[i]);
			}
			int instanceVariableIndex = mLocalVariablesSorter.newLocal(Type.getType(Object.class));
				
				
			
			
			System.out.println("Owner: " + owner);
			System.out.println("Name: " + name);
			System.out.println("Desc: " + desc);
			System.out.println("arguments: " );
			for(Type type: argumentTypes){
				System.out.println('\t' + type.toString());
			}
			System.out.println("returns: " + returnType);
			System.out.println("----------------------------");
    	}
    	super.visitMethodInsn(opcode, owner, name, desc, itf);
    	mCounter++;
    }
    
    protected void LoadOrStore(int index, String methodHandler, String arguments){
        super.visitLdcInsn(index);
        super.visitLdcInsn("var" + index);
        super.visitLdcInsn(mCounter);
        super.visitLdcInsn(mMethodName);
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", methodHandler, arguments, false);
        
        saveLocalLocation(mCounter, mCounter - 5, "var" + index);

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc){        
        if(opcode == Opcodes.GETFIELD){
            Type fieldType = Type.getType(desc);
            
            int tempindex = mLocalVariablesSorter.newLocal(Type.getType( (new Object()).getClass() ));
            mCounter += 10;
            
            super.visitVarInsn(Opcodes.ASTORE, tempindex);
            super.visitVarInsn(Opcodes.ALOAD, tempindex);
            super.visitInsn(Opcodes.DUP);
            super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
            super.visitLdcInsn(fieldType.getClassName());
            super.visitLdcInsn(name);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodName);
            
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
        } else if(opcode == Opcodes.GETSTATIC){ //GETSTATIC
            Type fieldType = Type.getType(desc);

        	mCounter += 6;
        	super.visitFieldInsn(opcode, owner, name, desc);
        	super.visitLdcInsn(fieldType.getClassName());
        	super.visitLdcInsn(name);
        	super.visitLdcInsn(mCounter);
        	super.visitLdcInsn(mMethodName);
        	
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
        	
        	
        }
        super.visitFieldInsn(opcode, owner, name, desc);
        mCounter++;
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.BASTORE || opcode == Opcodes.CASTORE || opcode == Opcodes.IASTORE ||
                opcode == Opcodes.SASTORE) {

            int index = mLocalVariablesSorter.newLocal(Type.INT_TYPE);

            mCounter += 7;
            super.visitVarInsn(Opcodes.ISTORE, index);
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.ILOAD, index);
            super.visitLdcInsn(mCounter);
            super.visitLdcInsn(mMethodName);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleArrayElementDefINT", "(Ljava/lang/Object;IIILjava/lang/String;)V", false);
            super.visitVarInsn(Opcodes.ILOAD, index);
        }

        super.visitInsn(opcode);
        mCounter++;
    }


    void saveLocalLocation(int loadOrStore, int push, String oldName)
    {
        m_localLocation[m_localLocationCount] = new LocalInfo();
        m_localLocation[m_localLocationCount].pushLocation = push; // instruction number
        m_localLocation[m_localLocationCount].loadOrStoreLocation = loadOrStore; // instruction number
        m_localLocation[m_localLocationCount].oldName = oldName;

        m_localLocationCount++;
    }

}
