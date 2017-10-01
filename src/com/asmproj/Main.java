package com.asmproj;

import org.objectweb.asm.*;

import java.io.*;

import java.lang.String;

public class Main {

//    private static final String filePath = "/Users/elieh/IdeaProjects/ASMProject/out/production/ASMProject/com/asmproj/Testing.class";


    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Provide a path to the class file as argument");
            return;
        }
        String filePath = args[0];
        InputStream in = new FileInputStream(filePath);
        ClassReader classReader = new ClassReader(in);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM5, classWriter);
        classReader.accept(myClassVisitor, 0);
        final DataOutputStream dout = new DataOutputStream(new FileOutputStream(filePath));
        dout.write(classWriter.toByteArray());
    }


}
