package com.asmproj;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.AbstractDocument.BranchElement;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.StaticInitMerger;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;


/**
 * Next  steps:
 * 1. When we enter a method want to call HandleMethodEntry
 * 2. When we exit a method want to call HandleMethodExit 
 * 
 * What we need: which basic blocks get executed and which did not
 * 	1. Identify the basic blocks leaders
 *  2. Call profiler we need pass className, method name, method signature, instruction line
 *  
 *  OUTPUT DATA:
 *  String identifiers (class name, method name)
 *  Basic block data (class name id, method name id, instruction number)
 *  count
 *  
 */






/**
 * @author Marwan Maalouf
 * Overrides the toArray method that was resulting in a {@link IndexOutOfBoundsException} when called
 * One way to do it without this class is to always add a null value at the end of our InsnList
 */
class InstructionList extends InsnList{
	public InstructionList(){
		super();
	}

	@Override
	public AbstractInsnNode [] toArray(){
		AbstractInsnNode [] array = new AbstractInsnNode[super.size()];
		AbstractInsnNode elem = super.getFirst();
		for(int i = 0; i < super.size(); i++){
			array[i] = elem;
			elem = elem.getNext();
		}
		return array;	
	}
}

/**
 * 
 * @author Marwan Maalouf
 * Creates a map between methods and basic blocks
 * Note that the printer is being reinitialized on every run in order to keep the same ordering of labels: labels are given a number in an 
 * incremental fashion starting at 0. So if we do not reset the counter by reinitializing the printer we will get during our second run 
 * L{Total #ofLabels + label number in first run} 
 * 
 */
public class BasicBlockGenerator {

	public static Map<String, List<InstructionList>> _basicBlocksPerMethod = new HashMap<String, List<InstructionList>>();
	public static List<Integer> basicBlockLeaders = new ArrayList<Integer>();
	public static Map<String, List<Integer>> _leadersPerMethod = new HashMap<String, List<Integer>>();
	public static List<InstructionList> basicBlocks = new ArrayList<InstructionList>();
	private static InstructionList temp = new InstructionList();
	public static List<Integer> instructionNumber = new ArrayList<Integer>();
	public static Map<String, Map> _localsPerMethod  = new HashMap<String, Map>();
	
//	public static void buildMethodBasicBlocks(MethodNode methodNode, String className, String methodName){
//		System.out.println("Building basic blocks of " + className + "." + methodName);
//
//		// Initialize printer
//		printer = new Textifier();
//		mp = new TraceMethodVisitor(printer);
//		basicBlocks = new ArrayList<InstructionList>();
//		temp = new InstructionList();
//		basicBlockLeaders = new HashSet<>();
//
//		/**
//		 * Handle case 1 and case 3 by splitting on conditional and unconditional jump
//		 * Saves the target labels of jump instructions for case 2
//		 * For LOOKUPSWITCH and TABLESWITCH notice that we are only splitting at the first label (be it the default one or a customized one) 
//		 * since the others will be split due to the GOTO instruction that preceeds them
//		 */
//
//		final List<TryCatchBlockNode> trycatchBlock = methodNode.tryCatchBlocks; // get try catch blocks try - catch - finally
//		List<Integer> targetLabels = new ArrayList<Integer>();// create a list of integer to be populated by the target labels
//
//		// Get all the target labels from the try catch blocks to be used during case 2
//		for(TryCatchBlockNode tcb: trycatchBlock){
//			Integer startLabel = Integer.valueOf(insnToString(tcb.start).split(" L")[1].trim());
//			Integer handlerLabel = Integer.valueOf(insnToString(tcb.handler).split(" L")[1].trim());
//			Integer endLabel = Integer.valueOf(insnToString(tcb.end).split(" L")[1].trim());
//
//			if(!targetLabels.contains(startLabel)){
//				targetLabels.add(startLabel);
//			}
//			if(!targetLabels.contains(handlerLabel)){
//				targetLabels.add(handlerLabel);
//			}
//			if(!targetLabels.contains(endLabel)){
//				targetLabels.add(endLabel);
//			}
//		}
//
//		for(AbstractInsnNode insn: methodNode.instructions.toArray()){
//			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels
//			temp.add(insn);
//
//			if(insn instanceof JumpInsnNode){// Jump instructions: YYYY LXX
//				basicBlocks.add(temp);
//				temp = new InstructionList();
//				Integer targetLabelNumber= Integer.valueOf(instructionName.split(" L")[1].trim()); 
//				if(!targetLabels.contains(targetLabelNumber)){
//					targetLabels.add(targetLabelNumber);
//				}
//			}else if(insn instanceof LookupSwitchInsnNode ){// Switch: YYYY: LXX  YYYY: LXX .... default: LXX  
//				LookupSwitchInsnNode lookupNode = (LookupSwitchInsnNode) insn;
//				Integer targetLabelNumber;
//				if(lookupNode.labels.size() > 0){
//					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.labels.get(0)).split(" L")[1].trim());
//				}else{
//					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.dflt).split(" L")[1].trim());
//				}
//				if(!targetLabels.contains(targetLabelNumber)){ 
//					targetLabels.add(targetLabelNumber);
//				}
//			}else if(insn instanceof TableSwitchInsnNode){// Switch: YYYY: LXX  YYYY: LXX .... default: LXX 
//				TableSwitchInsnNode tableSwitchNode = (TableSwitchInsnNode) insn;
//				Integer targetLabelNumber;
//				if(tableSwitchNode.labels.size() > 0){
//					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.labels.get(0)).split(" L")[1].trim());
//				}else{
//					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.dflt).split(" L")[1].trim());
//				}
//				if(!targetLabels.contains(targetLabelNumber)){
//					targetLabels.add(targetLabelNumber);
//				}
//			}else if((insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN)
//					|| insn.getOpcode() == Opcodes.ATHROW){// Returns and throws
//				basicBlocks.add(temp);
//				temp = new InstructionList();
//			}
//		}
//		basicBlocks.add(temp);
//
//		/**
//		 *  Handles case 2 by splitting on the retrieved target labels
//		 */
//		printer = new Textifier();
//		mp = new TraceMethodVisitor(printer);
//		temp = new InstructionList();
//
//		for(int i = 0; i < basicBlocks.size(); i++){
//			InstructionList someTemp = basicBlocks.remove(i);
//			if(someTemp.size() > 0 ){
//				for(AbstractInsnNode insn: someTemp.toArray()){
//					String instructionName = insnToString(insn);// Must keep it to maintain order of the labels	
//
//					if(insn.getType() == AbstractInsnNode.LABEL){
//						if(targetLabels.contains(Integer.valueOf(instructionName.split("L")[1].trim()))){
//							basicBlocks.add(i, temp);
//							i = i + 1;
//							temp = new InstructionList();
//						}
//						temp.add(insn);
//					}else{
//						temp.add(insn);
//					}
//				}
//
//				basicBlocks.add(i, temp);
//				temp = new InstructionList();
//			}
//		}
//
//		/**
//		 *  Remove empty blocks
//		 */
//		for(int i = 0; i < basicBlocks.size(); i++){
//			if(basicBlocks.get(i).size() == 0){
//				basicBlocks.remove(i);
//			}
//		}
//		for(InstructionList l: basicBlocks){
//			basicBlockLeaders.add(l.get(0));
//		}
//		
//	}


//	public void basicBlockCoverage(ClassNode cn){
//		for(MethodNode mn : cn.methods){
//			InsnList il =new InsnList();
//			System.out.println("# of local variables: " + ((List<LocalVariableNode>)mn.localVariables).size());
//			if(mn.parameters != null)
//				System.out.println("# of parameters: " + mn.parameters.size());
//			else
//				System.out.println("# of parameters: 0");
//			System.out.println("Class name: " + cn.name);
//			System.out.println("Method name: " + mn.name);
//			System.out.println("Method signature: " + mn.desc);
//			
//			for(final LocalVariableNode local : (List<LocalVariableNode>)mn.localVariables) {
//                System.out.println("Local Variable: " + local.name + " : " + local.desc + " : " + local.signature + " : " + local.index);
//            }
//			
////			int i = 0;
////			for(AbstractInsnNode insn : mn.instructions.toArray()){
////				if(basicBlockLeaders.contains(insn)){
////					il = new InsnList();
////					int lineNumber = ((LineNumberNode) insn.getNext()).line;
////					il.add(new LdcInsnNode(lineNumber));
////					il.add(new LdcInsnNode(i));
////					il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Profiler", "handleBBEntry", "(II;)V", false));
////					mn.instructions.insertBefore(insn, il);
////				}
////				i++;
////			}
//		}
//	}

	public static void testBasicBlocks(MethodNode mn){	
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		for(AbstractInsnNode insn : mn.instructions.toArray()){
			String str = insnToString(insn);
			if(insn instanceof LabelNode){
				LabelNode l = (LabelNode) insn;
				Integer loc = Integer.valueOf(str.split(" L")[1].trim());
				if(basicBlockLeaders.contains(loc)){
					System.out.println("-----------------------");
				}
			}
			System.out.print(str);
		}

	}

	
	public static void buildClassBasicBlockDesignators(ClassNode classNode){
		_leadersPerMethod = new HashMap<String, List<Integer>>();
		
		
		/**
		 * Get List of methods in the class
		 */
		final List<MethodNode> methods = classNode.methods;
		/**
		 *	Make basic blocks rules:
		 *		1. first statement of method
		 *		2. target of goto
		 *		3. statement after conditional and unconditional jump
		 */

		for(MethodNode m: (List<MethodNode>)methods){
			List<LocalVariableNode> locVariables = (List<LocalVariableNode>)m.localVariables;
			Map<Integer, String> locals = new HashMap<Integer, String>();
			
			if(locVariables != null){
				for(final LocalVariableNode local : locVariables) {
					locals.put(local.index, local.name);
	            }
				
			}
			
			
			buildMethodBasicBlockDesignators(m, classNode.name, m.name);
			_leadersPerMethod.put(classNode.name + "." + m.name + m.desc, instructionNumber);
		}     
		
		for(String key:_leadersPerMethod.keySet()){
			System.out.print(key + "{");
			List<Integer> l = (List<Integer>) _leadersPerMethod.get(key);
			for(int i = 0; i < l.size(); i++){
				System.out.print(l.get(i) + " ");
			}
			System.out.println("}");
		}
	}

	public static void buildMethodBasicBlockDesignators(MethodNode methodNode, String className, String methodName){
		// Initialize printer
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		basicBlockLeaders = new ArrayList<Integer>();

		/**
		 * Handle case 1 and case 3 by splitting on conditional and unconditional jump
		 * Saves the target labels of jump instructions for case 2
		 * For LOOKUPSWITCH and TABLESWITCH notice that we are only splitting at the first label (be it the default one or a customized one) 
		 * since the others will be split due to the GOTO instruction that preceeds them
		 */

		final List<TryCatchBlockNode> trycatchBlock = methodNode.tryCatchBlocks; // get try catch blocks try - catch - finally
		List<Integer> targetLabels = new ArrayList<Integer>();// create a list of integer to be populated by the target labels
		List<LabelNode> trycatches = new ArrayList<LabelNode>();// create a list of integer to be populated by the target labels

		
		// Get all the target labels from the try catch blocks to be used during case 2
		for(TryCatchBlockNode tcb: trycatchBlock){
			trycatches.add(tcb.start);
			trycatches.add(tcb.handler);
			trycatches.add(tcb.end);
		}
/*
		System.out.print("target labels : { ");
		for(Integer i : targetLabels){
			System.out.print(i + " ");
		}System.out.println("}");
		
		System.out.print("1.a : { ");
		for(Integer i : basicBlockLeaders){
			System.out.print(i + " ");
		}System.out.println("}");
		
	*/	
		
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		boolean isLeader = true;
		System.out.println(methodNode.name + methodNode.desc);
		for(AbstractInsnNode insn: methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels
			System.out.print(instructionName);
			if(isLeader){
				isLeader = false;
				Integer label = Integer.valueOf(instructionName.split(" L")[1].trim());
				if(!basicBlockLeaders.contains(label)){
					basicBlockLeaders.add(label);
				}
			}

			if(insn instanceof JumpInsnNode){// Jump instructions: YYYY LXX
				isLeader = true;
				Integer targetLabelNumber= Integer.valueOf(instructionName.split(" L")[1].trim()); 
				if(!targetLabels.contains(targetLabelNumber)){
					targetLabels.add(targetLabelNumber);
				}
			}else if(insn instanceof LookupSwitchInsnNode ){// Switch: YYYY: LXX  YYYY: LXX .... default: LXX  
				LookupSwitchInsnNode lookupNode = (LookupSwitchInsnNode) insn;
				Integer targetLabelNumber;
				if(lookupNode.labels.size() > 0){
					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.labels.get(0)).split(" L")[1].trim());
				}else{
					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.dflt).split(" L")[1].trim());
				}
				if(!targetLabels.contains(targetLabelNumber)){ 
					targetLabels.add(targetLabelNumber);
				}
			}else if(insn instanceof TableSwitchInsnNode){// Switch: YYYY: LXX  YYYY: LXX .... default: LXX 
				TableSwitchInsnNode tableSwitchNode = (TableSwitchInsnNode) insn;
				Integer targetLabelNumber;
				if(tableSwitchNode.labels.size() > 0){
					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.labels.get(0)).split(" L")[1].trim());
				}else{
					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.dflt).split(" L")[1].trim());
				}
				if(!targetLabels.contains(targetLabelNumber)){
					targetLabels.add(targetLabelNumber);
				}
			}else if((insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN)
					|| insn.getOpcode() == Opcodes.ATHROW){// Returns and throws
				isLeader = true;
			}
		}
		/*
		System.out.print("target labels : { ");
		for(Integer i : targetLabels){
			System.out.print(i + " ");
		}System.out.println("}");
		
		System.out.print("1.b : { ");
		for(Integer i : basicBlockLeaders){
			System.out.print(i + " ");
		}System.out.println("}");
		*/

		/**
		 *  Handles case 2 by splitting on the retrieved target labels
		 */
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);

		for(AbstractInsnNode insn : methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels	
			if(insn.getType() == AbstractInsnNode.LABEL){
				Integer label = Integer.valueOf(instructionName.split("L")[1].trim());
				if(targetLabels.contains(label)){
					if(!basicBlockLeaders.contains(label)){
						basicBlockLeaders.add(label);
					}
				}
				if(trycatches.contains((LabelNode)insn)){
					if(!basicBlockLeaders.contains(label)){
						basicBlockLeaders.add(label);
						//System.out.println("Added " + label);
					}
				}
			}
		}
		
		
		instructionNumber = new ArrayList<Integer>();
		int count = 0;
		for(AbstractInsnNode insn : methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels	
			if(insn instanceof LabelNode){
				Integer label = Integer.valueOf(instructionName.split("L")[1].trim());
				if(basicBlockLeaders.contains(label)){
					instructionNumber.add(count);
				}
			}
			count++;
		}
	}

//	public static void main(String[] args) throws Exception{
//
//		// Open class file
//		String filePath = "C:\\Users\\User\\Desktop\\ASM bytecode project\\out\\com\\asmproj\\test\\"
//				+ "\\Testing_2.class";
//		System.out.println("Loading: " + filePath);
//		InputStream in = new FileInputStream(filePath);
//
//		// Class reader
//		ClassReader reader = new ClassReader(in);
//		ClassNode classNode = new ClassNode();
//		reader.accept(classNode,0);
//
//
//		/**
//		 * Get List of methods in the class
//		 */
//		final List<MethodNode> methods = classNode.methods;
//		/**
//		 *	Make basic blocks rules:
//		 *		1. first statement of method
//		 *		2. target of goto
//		 *		3. statement after conditional and unconditional jump
//		 */
//
//		BasicBlockGenerator bbg = new BasicBlockGenerator();
//		for(MethodNode m: (List<MethodNode>)methods){
//			System.out.println(m.name);
//			bbg.buildMethodBasicBlockDesignators(m, classNode.name, m.name);
//			bbg.testBasicBlocks(m);
//			_basicBlocksPerMethod.put(m.name, bbg.basicBlocks);
//		}     
//
//		
//		//write classNode
//		ClassWriter out=new ClassWriter(0);
//		classNode.accept(out);
//		output("C:\\Users\\User\\Desktop\\ASM bytecode project\\out\\ExampleClass.class",  out.toByteArray());
//	}

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
