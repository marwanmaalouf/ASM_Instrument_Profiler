package com.asmproj;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;


/**
 * Next  steps:
 * 1. When we enter a method want to call HandleMainMethod entry or HandleMethodEntry
 * 2. When we exit a method call handleMethodExit
 * 
 * 
 * What we need: which basic blocks get executed and which did not
 * 	1. Identify the basic blocks leaders
 *  2. Call profiler we need pass className, method name, method signature, instruction line
 *   
 *  
 *  
 *  
 *  
 *  
 *  
 *  OUTPUT DATA:
 *  String identifiers (class name, method name)
 *  Basic block data (class name id, method name id, instruction number)
 *  count
 *  
 */







/**
 * 
 * @author Marwan Maalouf
 * Creates a map between methods and basic blocks
 * Note that the printer is being reinitialized on every run in order to keep the same ordering of labels: labels are given a number in an 
 * incremental fashion starting at 0. So if we do not reset the counter by reinitializing the printer we will get during our second run 
 * L{Total #ofLabels + label number in first run} 
 * 
 */
public class Visualize {
	
	static List<InstructionList> _basicBlocks = new ArrayList<InstructionList>();
	static InstructionList _statementsBytecode = new InstructionList();
	static Map<String, List<InstructionList>> _basicBlocksPerMethod = new HashMap<String, List<InstructionList>>();


	public static void main(String[] args) throws Exception{

		// Open class file
		String filePath = "C:\\Users\\User\\Desktop\\ASM bytecode project\\out"
				//+ "\\com\\asmproj\\Testing_2.class");
				+ "\\ExampleClass.class";
		System.out.println("Loading: " + filePath);
		InputStream in = new FileInputStream(filePath);

		/*
		 * initiate a class reader and the class writer
		 * TODO: do we need a class writer?
		 */
		ClassReader reader = new ClassReader(in);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode,0);


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
		for(MethodNode m: methods){
			// Initialize printer
			printer = new Textifier();
			mp = new TraceMethodVisitor(printer);

			/**
			 * Handle case 1 and case 3 by splitting on conditional and unconditional jump
			 * Saves the target labels of jump instructions for case 2
			 * For LOOKUPSWITCH and TABLESWITCH notice that we are only splitting at the first label (be it the default one or a customized one) 
			 * since the others will be split due to the GOTO instruction that preceeds them
			 */
			
			
			final List<TryCatchBlockNode> trycatchBlock = m.tryCatchBlocks; // get try catch blocks try - catch - finally
			List<Integer> targetLabels = new ArrayList<Integer>();// create a list of integer to be populated by the target labels

			// Get all the target labels from the try catch blocks to be used during case 2
			for(TryCatchBlockNode tcb: trycatchBlock){
				Integer startLabel = Integer.valueOf(insnToString(tcb.start).split(" L")[1].trim());
				Integer handlerLabel = Integer.valueOf(insnToString(tcb.handler).split(" L")[1].trim());
				Integer endLabel = Integer.valueOf(insnToString(tcb.end).split(" L")[1].trim());


				if(!targetLabels.contains(startLabel)){
					targetLabels.add(startLabel);
				}
				if(!targetLabels.contains(handlerLabel)){
					targetLabels.add(handlerLabel);
				}
				if(!targetLabels.contains(endLabel)){
					targetLabels.add(endLabel);
				}
			}

			for(AbstractInsnNode insn: m.instructions.toArray()){

				String instructionName = insnToString(insn);// Must keep it to maintain order of the labels
				_statementsBytecode.add(insn);

				if(insn instanceof JumpInsnNode){// Jump instructions: YYYY LXX
					_basicBlocks.add(_statementsBytecode);
					_statementsBytecode = new InstructionList();
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
					_basicBlocks.add(_statementsBytecode);
					_statementsBytecode = new InstructionList();
				}
			}
			_basicBlocks.add(_statementsBytecode);

			/**
			 *  Handles case 2 by splitting on the retrieved target labels
			 */
			printer = new Textifier();
			mp = new TraceMethodVisitor(printer);
			_statementsBytecode = new InstructionList();

			for(int i = 0; i < _basicBlocks.size(); i++){
				InstructionList temp = _basicBlocks.remove(i);
				if(temp.size() > 0 ){
					for(AbstractInsnNode insn: temp.toArray()){
						String instructionName = insnToString(insn);// Must keep it to maintain order of the labels	

						if(insn.getType() == AbstractInsnNode.LABEL){
							if(targetLabels.contains(Integer.valueOf(instructionName.split("L")[1].trim()))){
								_basicBlocks.add(i, _statementsBytecode);
								i = i + 1;
								_statementsBytecode = new InstructionList();
							}
							_statementsBytecode.add(insn);
						}else{
							_statementsBytecode.add(insn);
						}
					}

					_basicBlocks.add(i, _statementsBytecode);
					_statementsBytecode = new InstructionList();
				}
			}

			/**
			 *  Remove empty blocks
			 */
			for(int i = 0; i < _basicBlocks.size(); i++){// Take care of case 2
				if(_basicBlocks.get(i).size() == 0){
					_basicBlocks.remove(i);
				}
			}

			/**
			 * Save and Clear blocks for next method
			 */
			_basicBlocksPerMethod.put(m.name, _basicBlocks);
			_basicBlocks = new ArrayList<InstructionList>();
		}     


		/**
		 *  Prints Basic blocks
		 */
		for (Entry<String, List<InstructionList>> method : _basicBlocksPerMethod.entrySet()) {
			printer = new Textifier();
			mp = new TraceMethodVisitor(printer);
			System.out.println(method.getKey());
			int counter = 1;
			for(InstructionList bb : method.getValue()){
				System.out.println("===================BASIC BLOCK #"+ counter + "=======================");
				counter++;
				for(AbstractInsnNode insn: bb.toArray()){
					System.out.print(insnToString(insn));
				}
			}
			System.out.println();
		}

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
