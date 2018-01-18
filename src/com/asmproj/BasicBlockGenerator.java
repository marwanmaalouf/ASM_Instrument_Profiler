package com.asmproj;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;




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

		String methodIdentifier = null;
		Map<Integer, String> locals = null;
		List<LocalVariableNode> locVariables = null;
		
		System.out.println("------------------Basic Block generator------------------");

		for(MethodNode m: (List<MethodNode>)methods){
			methodIdentifier = classNode.name + "." + m.name + m.desc;
			
			buildMethodBasicBlockDesignators(m, classNode.name, m.name);
			_leadersPerMethod.put(methodIdentifier, instructionNumber);
			
			locals = new HashMap<Integer, String>();
			locVariables = (List<LocalVariableNode>)m.localVariables;
			if(locVariables != null){
				for(final LocalVariableNode local : locVariables) {
					//System.out.println(local.name + " " + local.index);
					locals.put(local.index, local.name);
	            }
				_localsPerMethod.put(methodIdentifier, locals);
			}else{
				_localsPerMethod.put(methodIdentifier, locals);
				System.out.println("local variables not available");
			}
		}     
				
		for(String key:_leadersPerMethod.keySet()){
			System.out.print(key + " Basic blocks -> {");
			List<Integer> l = (List<Integer>) _leadersPerMethod.get(key);
			for(int i = 0; i < l.size(); i++){
				System.out.print(l.get(i) + " ");
			}
			System.out.println("}");
		}
		Map<Integer, String> tempMap;
		for(String key:_localsPerMethod.keySet()){
			tempMap = _localsPerMethod.get(key);
			System.out.print(key + " local variables -> {");
			for(String name : tempMap.values()){
				System.out.print(name + " ");
			}
			System.out.println("}");
		}
		
		System.out.println("---------------------------------------------------------");
	}

	
	public static void goOverCode(MethodNode methodNode){
		// Initialize printer
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);

		for(AbstractInsnNode insn: methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels
			System.out.print(instructionName);
		}
	}
	
	public static void buildMethodBasicBlockDesignators(MethodNode methodNode, String className, String methodName){
		// Initialize printer
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		basicBlockLeaders = new ArrayList<Integer>();
		instructionNumber = new ArrayList<Integer>();

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
		
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		boolean isLeader = true;
		System.out.println(methodNode.name + methodNode.desc);
		int i = 0;
		for(AbstractInsnNode insn: methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels
			System.out.print(instructionName);
			if(isLeader){
				isLeader = false;
				if(insn instanceof LabelNode){
					if(insn.getNext() != null){
						Integer label = Integer.valueOf(instructionName.split(" L")[1].trim());
						if(!basicBlockLeaders.contains(label)){
							basicBlockLeaders.add(label);
					}
				}
				}else{
					if(!instructionNumber.contains(i)){
						instructionNumber.add(i);
					}
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
			i++;
		}


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
		
		int count = 0;
		for(AbstractInsnNode insn : methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);// Must keep it to maintain order of the labels	
			if(insn instanceof LabelNode){
				Integer label = Integer.valueOf(instructionName.split("L")[1].trim());
				if(basicBlockLeaders.contains(label)){
					int tempCount = count;
					AbstractInsnNode nextNode = insn;
					boolean findInstruction = (nextNode instanceof LabelNode) ||
												(nextNode instanceof LineNumberNode) ||
												(nextNode instanceof FrameNode);
					while(findInstruction){
						nextNode = nextNode.getNext();
						tempCount++;
						findInstruction = (nextNode instanceof LabelNode) ||
								(nextNode instanceof LineNumberNode) ||
								(nextNode instanceof FrameNode);
					}
					
					if(!instructionNumber.contains(tempCount)){
						instructionNumber.add(tempCount);
					}
				}
			}
			count++;
		}
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
