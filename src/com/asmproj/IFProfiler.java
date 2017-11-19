package com.asmproj;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;

/*
 * BUGS: 
 * 1. Jar file bug
 * 
 */
/*
 * TODO: Handle throw
 * TODO: Handle def-use
 * TODO: Handle clinit
 */

public class IFProfiler
{
	private static Stack<Designator> stack;
	private static Tokeneizer methodTokenizer;
	private static Tokeneizer basicBlockTokeniser;
	
	// Method Coverage
	private static Hashtable<Integer, MethodDesignator> methodDesignatorMap;
	private static HashMap<Integer, Integer> methodMap;

	// Method Pair Coverage
	private static Hashtable<Pair<Integer, Integer>, MethodPairDesignator> methodPairDesignatorMap;
	private static Hashtable<Pair<Integer, Integer>, Integer> methodPairsMap;

	//  Basic Block Coverage
	private static int basicBlockCounter = 0;
	private static Stack<BasicBlockDesignator> bbStack;
//	private static HashMap<String, Integer> basicBlockTokeniser;
	private static Hashtable<Integer, BasicBlockDesignator> basicBlockDesignatorMap;
	private static Hashtable<Integer, Integer> basicBlockMap;

	// Basic Block Pair Coverage
	private static Hashtable<Pair<Integer, Integer>, BasicBlockPairsDesignator> basicBlockPairsDesignatorMap;
	private static Hashtable<Pair<Integer, Integer>, Integer> basicBlockPairsMap;

	// Debugger
	private static final String _CLASS_INDENTIFIER_ = "com.asmproj.IFProfiler";



	public static void handleClass(String jarFile, String className)
	{
	}


	public static void handleIfSelect(int instruction, String methodSignature)
	{
	}

	public static void handleMisc(int instruction, String methodSignature)
	{
	}

	public static void handleMiscAndCompute(int instruction, String methodSignature)
	{

	}

	public static void handleThrow(String className, String methodName, String desc)
	{
		if(methodName.charAt(0) == '<'){return;}
		String message = "Exiting method " + className + "." + methodName + desc;
		DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleThrow(String className, String methodName, String desc): " + message, 0);

		
		MethodDesignator method = (MethodDesignator)stack.pop();
		MethodDesignator methodDesignator = new MethodDesignator(className, methodName, desc);
		
		
		if(!method.equals(methodDesignator)){
			message = "Error in poping stack, expecting: " + methodDesignator.toString() + "received: " + method.toString();
			DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleThrow(String className, String methodName, String desc): " + message, 10);
		}
	}

	public static void handleArrayElementDefINT(Object array, int index, int value, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: ARRAY_INT" + " index: " + index + " value: " + value + "}");
	}
	public static void handleArrayElementDefLONG(Object array, int index, long value, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: ARRAY_LONG" + " index: " + index + " value: " + value + "}");
	}
	public static void handleArrayElementDefFLOAT(Object array, int index, float value, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: ARRAY_FLOAT" + " index: " + index + " value: " + value + "}");

	}
	public static void handleArrayElementDefDOUBLE(Object array, int index, double value, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: ARRAY_DOUBLE" + " index: " + index + " value: " + value + "}");

	}

	public static void handleArrayElementObjectDef(Object array, int index, Object theObject, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: ARRAY_Object" + " index: " + index + " value: " + theObject + "}");
	}

	public static void handleLocalVariableDefINT(int value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: INT" + " name: " + variableName + " value: " + value + "}");

	}
	public static void handleLocalVariableDefLONG(long value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: LONG" + " name: " + variableName + " value: " + value + "}");

	}
	public static void handleLocalVariableDefFLOAT(float value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: FLOAT" + " name: " + variableName + " value: " + value + "}");

	}
	public static void handleLocalVariableDefDOUBLE(double value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: DOUBLE" + " name: " + variableName + " value: " + value + "}");

	}


	public static void handleLocalVariableObjectDef(Object theObject, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		System.out.println("Def: {method: " + methodSignature + " type: " + theObject.getClass().getName()  + " name: " + variableName + " value: " + theObject + "}");

	}

	public static void handleStaticFieldDefINT(int value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Static Field Def: {method: " + methodSignature + " type: INT" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleStaticFieldDefLONG(long value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Static Field Def: {method: " + methodSignature + " type: LONG" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleStaticFieldDefFLOAT(float value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Static Field Def: {method: " + methodSignature + " type: FLOAT" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleStaticFieldDefDOUBLE(double value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Static Field Def: {method: " + methodSignature + " type: DOUBLE" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}

	public static void handleStaticFieldObjectDef(Object theObject, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Static Field Def: {method: " + methodSignature + " type: " + theObject.getClass().getName()  + " name: " + defParam.split("\\|")[1] + " value: " + theObject + "}");

	}

	public static void handleInstanceFieldDefINT(Object instance, int value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Field Def: {method: " + methodSignature + " type: INT" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleInstanceFieldDefLONG(Object instance, long value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Field Def: {method: " + methodSignature + " type: LONG" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleInstanceFieldDefFLOAT(Object instance, float value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Field Def: {method: " + methodSignature + " type: FLOAT" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}
	public static void handleInstanceFieldDefDOUBLE(Object instance, double value, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Field Def: {method: " + methodSignature + " type: DOUBLE" + " name: " + defParam.split("\\|")[1] + " value: " + value + "}");

	}

	public static void handleInstanceFieldObjectDef(Object instance, Object theObject, String defParam, int defInstruction, String methodSignature)
	{
		System.out.println("Field Def: {method: " + methodSignature + " type: " + theObject.getClass().getName()  + " name: " + defParam.split("\\|")[1] + " value: " + theObject + "}");

	}

	public static void handleInstanceFieldUseINT(Object instance, int value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{        
		System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleInstanceFieldUseLONG(Object instance, long value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{        
		System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleInstanceFieldUseFLOAT(Object instance, float value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleInstanceFieldUseDOUBLE(Object instance, double value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleInstanceFieldObjectUse(Object instance, Object theObject, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + theObject + " method: " + methodSignature + " inst#: " + useInstruction + "}");
	}

	public static void handleInstanceMethodCall(Object instance, String calledMethodName, String calledMethodSignature, int numberOfParameters, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
		System.out.println("Method call: " + calledMethodName + calledMethodSignature + " { instance: " + instance + " # of param " + numberOfParameters + " returns " + bReturnsValue + " method: " + methodSignature + "}");
	}

	public static void handleInstanceMethodReturn(Object instance,
			String calledClassName, String calledMethodName, String calledMethodSignature, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
		System.out.println("Method return: " + calledMethodName + calledMethodSignature + " { instance: " + instance + " called @" + callInstruction + " returns void <" + bReturnsValue + ">" + "} in method: " + methodSignature);

	}

	public static void handleMainMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc)
	{
		// Initialize
		stack = new Stack();
		methodTokenizer = new Tokeneizer("Method_Tokenizer");


		MethodDesignator main = new MethodDesignator(className, methodName, desc);
		stack.push(main);

		Integer token = methodTokenizer.tokenize(main.toString());


		if(Control._METHOD_COVERAGE){
			methodDesignatorMap = new Hashtable<Integer, MethodDesignator>();
			methodMap = new HashMap<Integer, Integer>();

			addMethodDesignator(main);

			if(methodMap.containsKey(token)){
				Integer count = (Integer) methodMap.get(token);
				count = new Integer(count.intValue() + 1);
				methodMap.put(token, count);
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMainMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): "
				+"Entering method " + main.toString() + ", count: " + count);
			}
			else{
				methodMap.put(token, new Integer(1));
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMainMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): "
						+"Entering method " + main.toString() + ", count: 1");			
			}
		}

		if(Control._METHOD_PAIR_COVERAGE){
			methodPairsMap = new Hashtable<Pair<Integer, Integer>, Integer>();
			methodPairDesignatorMap = new Hashtable<Pair<Integer, Integer>, MethodPairDesignator>();
		}

		if(Control._BASICBLOCK_COVERAGE || Control._BASICBLOCK_PAIR_COVERAGE){
			basicBlockTokeniser = new Tokeneizer("BasicBlock_Tokenizer");
			bbStack = new Stack<BasicBlockDesignator>();
			basicBlockDesignatorMap = new Hashtable<Integer, BasicBlockDesignator>();
			basicBlockMap = new Hashtable<Integer, Integer>();
			if(Control._BASICBLOCK_PAIR_COVERAGE){ 
				basicBlockPairsDesignatorMap = new Hashtable<Pair<Integer, Integer>, BasicBlockPairsDesignator>();
				basicBlockPairsMap = new Hashtable<Pair<Integer, Integer>, Integer>();
				
			}
		}
	}

	public static void handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc)
	{
		if(methodName.equals("<clinit>")){return;}


		MethodDesignator methodDesignator = new MethodDesignator(className, methodName, desc);
		Integer token = methodTokenizer.tokenize(methodDesignator.toString());


		if(Control._METHOD_COVERAGE){
			addMethodDesignator(methodDesignator);

			if(methodMap.containsKey(token)){
				Integer count = (Integer) methodMap.get(token);
				count = new Integer(count.intValue() + 1);
				methodMap.put(token, count);
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): " 
				+"Entering method " + methodDesignator.toString() + ", count: " + count);
			}else{
				methodMap.put(token, new Integer(1));
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): " 
						+"Entering method " + methodDesignator.toString() + ", count: 1");			
			}	
		}

		if(Control._METHOD_PAIR_COVERAGE){
			MethodDesignator caller = (MethodDesignator)stack.peek();
			MethodPairDesignator methodPairDesignator = new MethodPairDesignator(caller, methodDesignator);
			Integer caller_token = methodTokenizer.tokenize(caller.toString());
			Pair<Integer, Integer> pair = new Pair(caller_token, token);

			// Add methodPairDesignator
			addMethodPairDesignator(pair, methodPairDesignator);

			if(methodPairsMap.containsKey(pair)){
				Integer count = (Integer) methodPairsMap.get(pair);
				count = new Integer(count.intValue() + 1);
				methodPairsMap.put(pair, count);
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): " 
				+ "Method pair " + methodPairDesignator.toString() + " executed with count: " + count);
			}else{
				methodPairsMap.put(pair, new Integer(1));
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc): " 
						+ "Method pair " + methodPairDesignator.toString() + " executed with count: 1");			
			}
		}

		if(Control._BASICBLOCK_COVERAGE){
			if(bbStack.size() > 0){
				BasicBlockDesignator caller = bbStack.peek();
				bbStack.push(caller);
			}
		}


		stack.push(methodDesignator);
	}

	public static void handleMethodExit(boolean bReturnsValue, int returnInstruction, String className, String methodName, String desc)
	{
		String message = _CLASS_INDENTIFIER_ + ".handleMethodExit(boolean bReturnsValue, int returnInstruction, String className, String methodName, String desc)";
		if(methodName.equals("<clinit>")){return;}
		DebugLog.Log(message + ": " + "Exiting method " + className + "." + methodName + desc);

		MethodDesignator method = (MethodDesignator)stack.pop();
		MethodDesignator methodDesignator = new MethodDesignator(className, methodName, desc);

		if(!method.equals(methodDesignator)){
			DebugLog.Log(message + ": Error in poping stack, expecting: " + methodDesignator.toString() + "received: " + method.toString(), 10);
		}

		if(Control._BASICBLOCK_COVERAGE){
			if(methodName.equals("main")){
				while(bbStack.size() > 0)
					bbStack.pop();
			}
			else{
				BasicBlockDesignator lastBlock = bbStack.pop();
				BasicBlockDesignator callerBlock = bbStack.peek();

				if(Control._BASICBLOCK_CROSSFUNCTION_COVERAGE){
					Integer lastBlock_token = basicBlockTokeniser.tokenize(lastBlock.toString());
					Integer caller_token = basicBlockTokeniser.tokenize(callerBlock.toString());
					
					Pair<Integer, Integer> pair = new Pair(lastBlock_token, caller_token);
					BasicBlockPairsDesignator basicBlockPairsDesignator = new BasicBlockPairsDesignator(lastBlock, callerBlock);
					addBasicBlockPairsDesignator(pair, basicBlockPairsDesignator);

					if(basicBlockPairsMap.containsKey(pair)){
						Integer count = (Integer) basicBlockPairsMap.get(pair);
						count = new Integer(count.intValue() + 1);
						basicBlockPairsMap.put(pair, count);
						DebugLog.Log(message + ": Basic Block pair " + basicBlockPairsDesignator.toString() + " executed with count: " + count);
					}else{
						basicBlockPairsMap.put(pair, new Integer(1));
						DebugLog.Log(message + ": Basic Block pair " + basicBlockPairsDesignator.toString() + " executed with count: 1");
					}
				}
			}
		}



		if(methodName.equals("main")){
			generateProfile();
		}
	}

	public static void handleBasicBlockEntry(String className, String methodName, String methodSignature, int lineNumber, int instructionNumber){
		String methodIdentifier = _CLASS_INDENTIFIER_ + ".handleBasicBlockEntry(String className, String methodName, String methodSignature, int lineNumber, int instructionNumber)";
		if(methodName.equals("<clinit>")){return;}

		if(Control._BASICBLOCK_COVERAGE){
			BasicBlockDesignator bbd = new BasicBlockDesignator(className, methodName, methodSignature, lineNumber, instructionNumber);
			Integer key = basicBlockTokeniser.tokenize(bbd.toString());
			addBasicBlockDesignator(key, bbd);

			if(basicBlockMap.containsKey(key)){
				Integer count = (Integer) basicBlockMap.get(key);
				count = new Integer(count.intValue() + 1);
				basicBlockMap.put(key, count);
				DebugLog.Log(methodIdentifier+ ": Entering basic block " + bbd.toString() + ", count: " + count);
			}else{
				basicBlockMap.put(key, new Integer(1));
				DebugLog.Log(methodIdentifier+ ": Entering basic block " + bbd.toString() + ", count: 1");
			}


			if(bbStack.size() == 0){
				// First basic Block of a method
			}else{
				BasicBlockDesignator caller = bbStack.pop();
				boolean addPair = false;
				if(Control._BASICBLOCK_CROSSFUNCTION_COVERAGE){
					addPair = true;
				}else{
					String callerIdentifier = caller.className + "." + caller.methodName + caller.methodSignature;
					String currentIdentifier = className + "." + methodName + methodSignature;
					addPair = callerIdentifier.equals(currentIdentifier);
					
				}
				if(addPair){
					Integer caller_token = basicBlockTokeniser.tokenize(caller.toString());
					Pair<Integer, Integer> pair = new Pair(caller_token, key);
					BasicBlockPairsDesignator basicBlockPairsDesignator = new BasicBlockPairsDesignator(caller, bbd);
					addBasicBlockPairsDesignator(pair, basicBlockPairsDesignator);

					if(basicBlockPairsMap.containsKey(pair)){
						Integer count = (Integer) basicBlockPairsMap.get(pair);
						count = new Integer(count.intValue() + 1);
						basicBlockPairsMap.put(pair, count);
						DebugLog.Log(methodIdentifier + ": Basic Block pair " + basicBlockPairsDesignator.toString() + " executed with count: " + count);
					}else{
						basicBlockPairsMap.put(pair, new Integer(1));
						DebugLog.Log(methodIdentifier + ": Basic Block pair " + basicBlockPairsDesignator.toString() + " executed with count: 1");
					}
				}
			}

			bbStack.push(bbd);
		}
	}




	public static void handleStaticMethodCall(String calledmethodInfo, int numberOfParameters, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
	}
	public static void handleSpecialMethodCall(String calledmethodInfo, int numberOfParameters, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
	}
	public static void handleSpecialOrStaticMethodReturn(String calledmethodInfo,
			int callInstruction, boolean bReturnsValue, String methodSignature)
	{
	}



	public static void handleArrayElementObjectUse(Object array, int index, Object theObject, int useInstruction, String methodSignature)
	{
	}
	public static void handleArrayElementUseINT(Object array, int index, int value, int useInstruction, String methodSignature)
	{
	}
	public static void handleArrayElementUseFLOAT(Object array, int index, float value, int useInstruction, String methodSignature)
	{
	}
	public static void handleArrayElementUseLONG(Object array, int index, long value, int useInstruction, String methodSignature)
	{
	}
	public static void handleArrayElementUseDOUBLE(Object array, int index, double value, int useInstruction, String methodSignature)
	{
	}

	public static void handleLocalVariableUseI(int value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		System.out.println("Use: {method: " + methodSignature + " type: INT" + " name: " + variableName + " value: " + value + "}");
	}
	public static void handleLocalVariableUseL(long value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		System.out.println("Use: {method: " + methodSignature + " type: " + "LONG" + " name: " + variableName + " value: " + value + "}");
	}
	public static void handleLocalVariableUseF(float value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		System.out.println("Use: {method: " + methodSignature + " type: " + "FLOAT" + " name: " + variableName + " value: " + value + "}");
	}
	public static void handleLocalVariableUseD(double value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		System.out.println("Use: {method: " + methodSignature + " type: " + "DOUBLE" + " name: " + variableName + " value: " + value + "}");

	}
	public static void handleLocalVariableObjectUse(Object theObject, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		System.out.println("Use: {method: " + methodSignature + " type: " + theObject.getClass().getName() + " name: " + variableName + " value: " + theObject + "}");

	}
	public static void handleIINC(int value, int variableIndex, String variableName, int incInstruction, String methodSignature)
	{
	}

	public static void handleStaticFieldUseINT(int value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
	}
	public static void handleStaticFieldUseLONG(long value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
	}
	public static void handleStaticFieldUseFLOAT(float value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleStaticFieldUseDOUBLE(double value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}
	public static void handleStaticFieldObjectUse(Object theObject, String className, String fieldName, int useInstruction, String methodSignature)
	{
		System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + theObject + " method: " + methodSignature + " inst#: " + useInstruction + "}");

	}




	public static void generateProfile(){
		PrintWriter writer;
		methodTokenizer.printToFile();

		if(Control._METHOD_COVERAGE){
			try {
				writer = new PrintWriter("methodCoverage.txt", "UTF-8");
				for (Integer key: methodMap.keySet()) {
					writer.printf("%-10d %d", key, methodMap.get(key));
					writer.println();
				}
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(Control._METHOD_PAIR_COVERAGE){
			try {
				writer = new PrintWriter("methodPairCoverage.txt", "UTF-8");
				for (Pair<Integer, Integer> key: methodPairsMap.keySet()) {
					String pair = "(" + key.getFirst() + "," + key.getSecond() + ")";
					writer.printf("%-10s %d", pair, methodPairsMap.get(key));
					writer.println();
				}
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(Control._BASICBLOCK_COVERAGE){
			basicBlockTokeniser.printToFile();


			try {
				writer = new PrintWriter("basicBlockCoverage.txt", "UTF-8");
				for (Integer key: basicBlockMap.keySet()) {
					writer.printf("%-10s %d", key, basicBlockMap.get(key));
					writer.println();
				}
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(Control._BASICBLOCK_PAIR_COVERAGE){
			try {
				writer = new PrintWriter("basicBlockPairCoverage.txt", "UTF-8");
				for (Pair<Integer, Integer> key: basicBlockPairsMap.keySet()) {
					String pair = "(" + key.getFirst() + "," + key.getSecond() + ")";
					writer.printf("%-10s %d", pair, basicBlockPairsMap.get(key));
					writer.println();
				}
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}


	private static void addMethodDesignator(MethodDesignator m){
		if(!methodDesignatorMap.containsKey(methodTokenizer.tokenize(m.toString()))){
			methodDesignatorMap.put(methodTokenizer.tokenize(m.toString()), m);
		}
	}

	private static void addMethodPairDesignator(Pair<Integer, Integer> key, MethodPairDesignator value){
		if(!methodPairDesignatorMap.containsKey(key)){
			methodPairDesignatorMap.put(key, value);
		}
	}

	private static void addBasicBlockDesignator(Integer key, BasicBlockDesignator value){
		if(!basicBlockDesignatorMap.containsKey(key)){
			basicBlockDesignatorMap.put(key, value);
		}
	}
	
	private static void addBasicBlockPairsDesignator(Pair<Integer, Integer> key,
			BasicBlockPairsDesignator value) {
		if(!basicBlockPairsDesignatorMap.containsKey(key)){
			basicBlockPairsDesignatorMap.put(key, value);
		}
		
	}
}



