package com.asmproj;

import org.objectweb.asm.*;

public class MyClassVisitor extends ClassVisitor {

	protected String cClassName;
	protected MyMethodVisitor mMethodVisitor;
	protected String cJarFile;
	
	
    private MyClassVisitor(int api) {
        super(api);
    }

    public MyClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
        mMethodVisitor = null;
        cJarFile = "N/A";
    }
    
    public MyClassVisitor(int api, ClassVisitor cv, String jarFile) {
        super(api, cv);
        mMethodVisitor = null;
        cJarFile = jarFile;
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        cClassName = name;
    	System.out.println("Visiting class: " + name);
        System.out.println("Class Major Version: " + version);
        System.out.println("Super class: " + superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * This is the main reason why we extend the ClassVisitor: replace the MethodVisitor by our MethodVisitor
     */
    @Override
    public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions) {
        System.out.println("Visiting method: " + name + desc);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        mMethodVisitor = new MyMethodVisitor(api, mv, access, name, desc, signature, cClassName, exceptions, cJarFile);
        return mMethodVisitor;
    }

    /**
     * Invoked only when the class being visited is an inner class
     */
    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        System.out.println("Outer class: " + owner);
        super.visitOuterClass(owner, name, desc);
    }

    /**
     * Invoked when a class level annotation is encountered
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc,
                                             boolean visible) {
        System.out.println("Annotation: " + desc);
        return super.visitAnnotation(desc, visible);
    }

    /**
     * When a class attribute is encountered
     */
    @Override
    public void visitAttribute(Attribute attr) {
        System.out.println("Class Attribute: " + attr.type);
        super.visitAttribute(attr);
    }

    /**
     * When an inner class is encountered
     */
    @Override
    public void visitInnerClass(String name, String outerName,
                                String innerName, int access) {
        System.out.println("Inner Class: " + innerName + " defined in " + outerName);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    /**
     * When a field is encountered
     */
    @Override
    public FieldVisitor visitField(int access, String name,
                                   String desc, String signature, Object value) {
        System.out.println("Field: " + name + " " + desc + " value:" + value);
        return super.visitField(access, name, desc, signature, value);
    }


    @Override
    public void visitEnd() {
    	if(mMethodVisitor != null){
    		if(!mMethodVisitor.staticBlockFound){
    			MethodVisitor mv = super.visitMethod(Opcodes.ACC_STATIC, "<clinit>", 
    					Type.getMethodDescriptor(Type.VOID_TYPE), null, null);
    			mv.visitCode();
    			Label l0 = new Label();
    			mv.visitLabel(l0);
    			mv.visitLineNumber(10, l0);
    	    	mv.visitLdcInsn(cJarFile);
    	    	mv.visitLdcInsn(cClassName);
    	    	mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/asmproj/IFProfiler", "handleClass",
    					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(String.class)), false);
    	    	mv.visitInsn(Opcodes.RETURN);
    	    	mv.visitMaxs(0, 0);
    	    	mv.visitEnd();
    		}  		
    	}
    	
        System.out.println("Class ends here");
        super.visitEnd();
    }

    /**
     * When the optional source is encountered
     */
    @Override
    public void visitSource(String source, String debug) {
        System.out.println("Source: " + source);
        super.visitSource(source, debug);
    }

}
