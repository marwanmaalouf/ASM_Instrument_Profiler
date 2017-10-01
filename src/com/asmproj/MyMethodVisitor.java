package com.asmproj;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodNode;

import java.util.Hashtable;


public class MyMethodVisitor extends MethodVisitor {

    private final String mMethodName;
    private final MethodNode mMethodNode;
    private final LocalVariablesSorter mLocalVariablesSorter;

    protected int mCounter = 0;// need to increment it at the end of each visit

//	protected AnalyzerAdapter aa;


    protected int m_localLocationCount;
    //    protected LocalInfo[] m_localLocation;
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

    protected void LoadOrStore(int index, String methodHandler, String arguments) {
        super.visitLdcInsn(index);
        super.visitLdcInsn("var" + index);
        super.visitLdcInsn(mCounter);
        super.visitLdcInsn(mMethodName);
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com.asmproj.IFProfiler", methodHandler, arguments, false);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        System.out.println(owner);
        System.out.println(name);
        System.out.println(desc);
        if (opcode == Opcodes.GETFIELD) {

        }
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
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com.asmproj.IFProfiler", "handleArrayElementDefINT", "(Ljava/lang/Object;IIILjava/lang/String;)V", false);
            super.visitVarInsn(Opcodes.ILOAD, index);
        }

        super.visitInsn(opcode);
        mCounter++;
    }


}
