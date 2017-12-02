package com.asmproj;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.io.*;

import java.lang.String;

public class Main {

	private static final String _JAR = ".jar";
	private static final String _CLASS = ".class";
	private static final String _INSTRUMENTED = "_instrumented";
	private static String _directoryPath;

	protected static void instrumentJarFile(String jarFile, String outputFileName){
		System.out.println("Loading Jar file: " + jarFile);

		JarFile jis;
		try {
			System.out.println(jarFile);
			jis = new JarFile(jarFile);
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputFileName));
			Enumeration<JarEntry> entries = jis.entries();

			while (entries.hasMoreElements()) {
				JarEntry inputJarEntry = entries.nextElement(); 
				JarEntry newEntry = null;
				byte [] bytes = null;
				String entryName = inputJarEntry.toString();

				System.out.println("Loading " + entryName);

				if(entryName.endsWith(_CLASS)){
					InputStream classFileInputStream;
					classFileInputStream = jis.getInputStream(inputJarEntry);
					ClassWriter cw = instrumentClassFile(classFileInputStream, entryName);
					classFileInputStream.close();
					bytes = cw.toByteArray();
				}else{
					InputStream inputJarStream = jis.getInputStream(inputJarEntry);
					int len = inputJarStream.available();
					bytes = new byte[len];
					int nRead = 0;
					int nReadTotal = 0;
					int nOffset = 0;
					while (len > 0)
					{
						nRead = inputJarStream.read(bytes, nOffset, len);
						nReadTotal += nRead;
						nOffset += nRead;
						len -= nRead;
					}
				}
				newEntry = new JarEntry(inputJarEntry.getName());
				
				newEntry.setMethod(java.util.zip.ZipOutputStream.DEFLATED);
				newEntry.setSize(bytes.length);

				jos.putNextEntry(newEntry);
				jos.write(bytes);
				jos.flush();
				jos.closeEntry();
			}
			jos.close();
		} catch (IOException e) {
			System.out.println("Failed to instrument " + jarFile);
			e.printStackTrace();
		}
	}

	private static ClassWriter instrumentClassFile(InputStream in, String className) throws IOException {
		System.out.println("Starting instrumentation of " + className);
		ClassReader classReader = new ClassReader(in);
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode,0);
		BasicBlockGenerator.buildClassBasicBlockDesignators(classNode);

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM5, classWriter);
		classReader.accept(myClassVisitor, 0);
		return classWriter;
	}

	private static void instrumentClassFile(String classFile, String outputFileName) {
		try {
			System.out.println("Loading " + classFile);
			InputStream in = new FileInputStream(classFile);
			ClassWriter classWriter = instrumentClassFile(in, classFile);
			

			final DataOutputStream dout = new DataOutputStream(new FileOutputStream(outputFileName));
			dout.write(classWriter.toByteArray());
		} catch (IOException e) {
			System.out.println("Failed to instrument " + classFile);
			e.printStackTrace();
		}	
	}
	
	public static void main(String [] args){
		if (args.length == 0) {
			System.out.println("Provide a path to the class file as argument");
			return;
		}
		
		_directoryPath = Paths.get("").toAbsolutePath().toString() + "\\";
		for(int i = 0; i < args.length; i++){
			String fileName = args[i];
			String filePath = _directoryPath + fileName;
			String outputFileName = _directoryPath + fileName;

			System.out.println("File found: " + filePath);
			
			if(fileName.contains(_JAR)){
				fileName = fileName.split(_JAR)[0] + _INSTRUMENTED + _JAR;
				outputFileName = _directoryPath + "instrumented\\" + fileName;
				System.out.println(outputFileName);
				instrumentJarFile(filePath, outputFileName);
			}else if(fileName.contains(_CLASS)){
				instrumentClassFile(filePath, outputFileName);
			}
			
			System.out.println("Output written to " + outputFileName);
			System.out.println();
			System.out.println();
		}
	}
	
	
	
//	public static void main(String[] args) throws IOException {
//
//		if (args.length != 1) {
//			System.out.println("Provide a path to the class file as argument");
//			return;
//		}
//
//		for(int i = 0; i < args.length; i++){
//			String filePath;
//			Path currentRelativePath = Paths.get("");
//			_directoryPath = currentRelativePath.toAbsolutePath().toString(); 
//			filePath =  _directoryPath + args[i];
//			System.out.println("Loading: " + filePath);
//
//			InputStream in = new FileInputStream(filePath);
//
//			ClassReader classReader = new ClassReader(in);
//			ClassNode classNode = new ClassNode();
//			classReader.accept(classNode,0);
//			BasicBlockGenerator.buildClassBasicBlockDesignators(classNode);
//
//			//ClassReader classReader = new ClassReader(in);
//			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//
//			MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM5, classWriter);
//			classReader.accept(myClassVisitor, 0);
//
//
//
//			final DataOutputStream dout = new DataOutputStream(new FileOutputStream(filePath));
//			dout.write(classWriter.toByteArray());
//		}
//	}
	
	
	
	public static void save(File jar, final List<ClassNode> nodes) {
        try {
            try(final JarOutputStream output = new JarOutputStream(new FileOutputStream(jar))) {
                for(ClassNode element : nodes) {
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    output.putNextEntry(new JarEntry(element.name.replaceAll("\\.", "/") + ".class"));
                    output.write(writer.toByteArray());
                    output.closeEntry();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ClassNode> load(File file) {
        try {
            JarFile jar = new JarFile(file);
            List<ClassNode> list = new ArrayList<>();
            Enumeration<JarEntry> enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry next = enumeration.nextElement();
                if(next.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(next));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    list.add(node);
                }
            }
            jar.close();
            return list;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
