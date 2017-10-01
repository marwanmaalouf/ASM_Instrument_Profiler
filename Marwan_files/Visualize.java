import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Visualize {
		
	 @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		 
	//	 JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
	//     int result = javac.run(null, null, null, "C:\\Users\\User\\workspace\\Eclipse Test\\src\\Main1.java");
	//     if (result != 0) {
	//        throw new RuntimeException("compile failed: exit " + result);
	//     }
		 
	    if (args.length != 1) {
            System.out.println("Provide a path to the class file as argument");
            return;
        }
        
        Path currentRelativePath = Paths.get("");
		String filePath = currentRelativePath.toAbsolutePath().toString() + args[0];
		System.out.println("Loading: " + filePath);
        InputStream in = new FileInputStream(filePath);
        
	     ClassReader reader = new ClassReader(in);
	     ClassNode classNode = new ClassNode();
	     reader.accept(classNode,0);
	     
	     @SuppressWarnings("unchecked")
	     final List<MethodNode> methods = classNode.methods;
	     int count = 0;
	     for(MethodNode m: methods){
	    	 InsnList inList = m.instructions;
	         System.out.println(m.name);
	         
	         for(int i = 0; i< inList.size(); i++){
	        	 count++;     	 
	        	 AbstractInsnNode currIns = inList.get(i);
	        	 System.out.print(insnToString(inList.get(i)));    	 
	         }
	     }
	     System.out.println("Total number of instructions: " + count);
	     
	     
	     	//write classNode
			ClassWriter out=new ClassWriter(0);
			classNode.accept(out);
			output("C:\\Users\\User\\workspace\\Eclipse Test\\src\\Main1.class",  out.toByteArray());
	    }

	    public static String insnToString(AbstractInsnNode insn){
	        insn.accept(mp);
	        StringWriter sw = new StringWriter();
	        printer.print(new PrintWriter(sw));
	        printer.getText().clear();
	        return sw.toString();
	    }

		public static void output(String filename, byte[] data) throws IOException {
			FileOutputStream out=new FileOutputStream(filename);
			out.write(data);
			out.close();
		}
		
	    private static Printer printer = new Textifier();
	    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer); 

	

}
