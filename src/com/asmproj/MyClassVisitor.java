package com.asmproj;

import org.objectweb.asm.*;

public class MyClassVisitor extends ClassVisitor {

    public MyClassVisitor(int api) {
        super(api);
    }

    public MyClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
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
        System.out.println("Visiting method: " + name);
        System.out.println("Visiting signature: " + signature);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MyMethodVisitor(api, mv, access, name, desc, signature, exceptions);
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
        System.out.println("Method ends here");
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
