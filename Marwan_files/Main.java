
import org.objectweb.asm.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;

import java.lang.String;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Provide a path to the class file as argument");
            return;
        }
        
        Path currentRelativePath = Paths.get("");
		String filePath = currentRelativePath.toAbsolutePath().toString() + args[0];
		System.out.println("Loading: " + filePath);
        InputStream in = new FileInputStream(filePath);
        ClassReader classReader = new ClassReader(in);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM5, classWriter);
        classReader.accept(myClassVisitor, 0);
        final DataOutputStream dout = new DataOutputStream(new FileOutputStream(filePath));
        dout.write(classWriter.toByteArray());
    }


}
