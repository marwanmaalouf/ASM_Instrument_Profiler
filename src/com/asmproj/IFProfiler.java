package com.asmproj;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/*
 * BUGS:
 * 1. I think that we should add the instance in the equality when considering instance Field variables 
 */

/*
 * TODO: Add thread management
 * TODO: create instrumented classes in another directory
 * TODO: Handle throw
 * TODO: Handle clinit
 * TODO: check skiplist to handle uninthialized this
 * TODO: I should rewrite the entire Profiler more efficiently and using some standards and hierarchy:
 *  1. use a factory framework for all the tables
 *  2. use a factory framework for all the designators
 *  3. Create a directory for the output files
 *  4. Some things can be done more easily: for method coverage and method pair coverage, instead of using a stack I can use the method call
 * 	   instruction such as INVOKESTATIC, INVOKEXXXX, ect.. no need for a stack
 */

public class IFProfiler
{
	private final static String _DIRECTORY = "Profiler\\";
	private final static String _METHOD_COVERAGE = _DIRECTORY + "Method_Coverage.csv";
	private final static String _METHOD_PAIR_COVERAGE = _DIRECTORY + "Method_Pair_Coverage.csv";
	private final static String _BASICBLOCKS_COVERAGE = _DIRECTORY + "BasicBlocks_Coverage.csv";
	private final static String _BASICBLOCKS_PAIR_COVERAGE = _DIRECTORY + "BasicBlocks_Pair_Coverage.csv";
	private final static String _DEF_COVERAGE = _DIRECTORY + "Def_Coverage.csv";
	private final static String _USE_COVERAGE = _DIRECTORY + "Use_Coverage.csv";
	private final static String _DEFUSE_PAIR_COVERAGE = _DIRECTORY + "DefUse_Pair_Coverage.csv";
	
	
	private static Stack<Designator> stack;
	private static Tokeneizer methodTokenizer;
	private static Tokeneizer basicBlockTokeniser;
	private static Tokeneizer variableTokenizer;

	// Method Coverage
	private static Hashtable<Integer, MethodDesignator> methodDesignatorMap;
	private static Hashtable<Integer, Integer> methodMap;

	// Method Pair Coverage
	private static Hashtable<Pair<Integer, Integer>, MethodPairDesignator> methodPairDesignatorMap;
	private static Hashtable<Pair<Integer, Integer>, Integer> methodPairsMap;

	//  Basic Block Coverage
	private static Stack<BasicBlockDesignator> bbStack;
	private static Hashtable<Integer, BasicBlockDesignator> basicBlockDesignatorMap;
	private static Hashtable<Integer, Integer> basicBlockMap;

	// Basic Block Pair Coverage
	private static Hashtable<Pair<Integer, Integer>, BasicBlockPairsDesignator> basicBlockPairsDesignatorMap;
	private static Hashtable<Pair<Integer, Integer>, Integer> basicBlockPairsMap;

	// Debugger
	private static final String _CLASS_INDENTIFIER_ = "com.asmproj.IFProfiler";


	// Def use coverage
	private static Hashtable<Integer, VariableDesignator> variableDesignatorMap;
	private static Hashtable<Integer, Integer> defMap;
	private static Hashtable<Integer, Integer> useMap;

	// Def use pair coverage
	private static Hashtable<Integer, Integer> lastDefInstructionMap;
	private static Hashtable<Vector<Integer>, Integer> defUsePairsMap;

	private static int parameterIndex = 0;
	private static boolean isStatic = true;
	private static int callInstruction = -1;
	
	public static void handleClass(String jarFile, String className)
	{
	}
	
	public static void toggleStatic(boolean value, int methodCallInstruction){
		isStatic = value;
		callInstruction = methodCallInstruction;
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
		//System.out.println("Def: {method: " + methodSignature + " type: ARRAY_INT" + " index: " + index + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Integer(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addDefDesignator(arrayElementVariableDesignator, defInstruction);

	}
	public static void handleArrayElementDefLONG(Object array, int index, long value, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: ARRAY_LONG" + " index: " + index + " value: " + value + "}");
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Long(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addDefDesignator(arrayElementVariableDesignator, defInstruction);
	}
	public static void handleArrayElementDefFLOAT(Object array, int index, float value, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: ARRAY_FLOAT" + " index: " + index + " value: " + value + "}");
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Float(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addDefDesignator(arrayElementVariableDesignator, defInstruction);
	}
	public static void handleArrayElementDefDOUBLE(Object array, int index, double value, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: ARRAY_DOUBLE" + " index: " + index + " value: " + value + "}");
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Double(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addDefDesignator(arrayElementVariableDesignator, defInstruction);
	}

	public static void handleArrayElementObjectDef(Object array, int index, Object theObject, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: ARRAY_Object" + " index: " + index + " value: " + theObject + "}");
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, theObject, index, methodDesignatorMap.get(methodDesignatorKey));
		addDefDesignator(arrayElementVariableDesignator, defInstruction);
	}

	public static void handleLocalVariableDefINT(int value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: INT" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addDefDesignator(localVariableDesignator, defInstruction);

	}
	public static void handleLocalVariableDefLONG(long value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: LONG" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addDefDesignator(localVariableDesignator, defInstruction);
	}
	public static void handleLocalVariableDefFLOAT(float value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: FLOAT" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addDefDesignator(localVariableDesignator, defInstruction);
	}
	public static void handleLocalVariableDefDOUBLE(double value, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: DOUBLE" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addDefDesignator(localVariableDesignator, defInstruction);
	}


	public static void handleLocalVariableObjectDef(Object theObject, int variableIndex, String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Def: {method: " + methodSignature + " type: " + theObject.getClass().getName()  + " name: " + variableName + " value: " + theObject + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addDefDesignator(localVariableDesignator, defInstruction);
	}


	public static void handleParameterDef(String variableName, int defInstruction, String methodSignature)
	{
		//System.out.println("Parameter Def: {method: " + methodSignature  + " name: " + variableName + " index: " + parameterIndex + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), "var_" + parameterIndex, parameterIndex);
		addDefDesignator(localVariableDesignator, defInstruction);
		parameterIndex++;
	}

	public static void handleStaticFieldDefINT(int value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Static Field Def: {class: " + className + " type: INT" + " name: " + defParam + " value: " + value + "}");

		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, defParam);
		addDefDesignator(staticFieldVariableDesignator, defInstruction);
	}
	public static void handleStaticFieldDefLONG(long value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Static Field Def: {method: " + className + " type: LONG" + " name: " + defParam + " value: " + value + "}");

		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, defParam);
		addDefDesignator(staticFieldVariableDesignator, defInstruction);
	}
	public static void handleStaticFieldDefFLOAT(float value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Static Field Def: {method: " + className + " type: FLOAT" + " name: " + defParam + " value: " + value + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, defParam);
		addDefDesignator(staticFieldVariableDesignator, defInstruction);
	}
	public static void handleStaticFieldDefDOUBLE(double value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Static Field Def: {method: " + className + " type: DOUBLE" + " name: " + defParam + " value: " + value + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, defParam);
		addDefDesignator(staticFieldVariableDesignator, defInstruction);
	}

	public static void handleStaticFieldObjectDef(Object theObject, String defParam, int defInstruction, String className)
	{
		//System.out.println("Static Field Def: {method: " + className + " type: " + theObject.getClass().getName()  + " name: " + defParam + " value: " + theObject + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, defParam);
		addDefDesignator(staticFieldVariableDesignator, defInstruction);
	}

	public static void handleInstanceFieldDefINT(Object instance, int value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Field Def: {class: " + className + " type: INT" + " name: " + defParam + " value: " + value + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, defParam, className);
		addDefDesignator(instanceFieldVariableDesignator, defInstruction);

	}
	public static void handleInstanceFieldDefLONG(Object instance, long value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Field Def: {class: " + className + " type: LONG" + " name: " + defParam + " value: " + value + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, defParam, className);
		addDefDesignator(instanceFieldVariableDesignator, defInstruction);
	}
	public static void handleInstanceFieldDefFLOAT(Object instance, float value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Field Def: {class: " + className + " type: FLOAT" + " name: " + defParam + " value: " + value + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, defParam, className);
		addDefDesignator(instanceFieldVariableDesignator, defInstruction);
	}
	public static void handleInstanceFieldDefDOUBLE(Object instance, double value, String defParam, int defInstruction, String className)
	{
		//System.out.println("Field Def: {class: " + className + " type: DOUBLE" + " name: " + defParam + " value: " + value + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, defParam, className);
		addDefDesignator(instanceFieldVariableDesignator, defInstruction);
	}

	public static void handleInstanceFieldObjectDef(Object instance, Object theObject, String defParam, int defInstruction, String className)
	{
		//System.out.println("Field Def: {class: " + className + " type: " + theObject.getClass().getName()  + " name: " + defParam + " value: " + theObject + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, defParam, className);
		addDefDesignator(instanceFieldVariableDesignator, defInstruction);
	}

	public static void handleInstanceFieldUseINT(Object instance, int value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{        
		//System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, fieldName, declaringClass);
		addUseDesignator(instanceFieldVariableDesignator, useInstruction);
	}
	public static void handleInstanceFieldUseLONG(Object instance, long value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{        
		//System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, fieldName, declaringClass);
		addUseDesignator(instanceFieldVariableDesignator, useInstruction);
	}
	public static void handleInstanceFieldUseFLOAT(Object instance, float value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, fieldName, declaringClass);
		addUseDesignator(instanceFieldVariableDesignator, useInstruction);

	}
	public static void handleInstanceFieldUseDOUBLE(Object instance, double value, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, fieldName, declaringClass);
		addUseDesignator(instanceFieldVariableDesignator, useInstruction);

	}
	public static void handleInstanceFieldObjectUse(Object instance, Object theObject, String declaringClass, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Field use: {name: " + fieldName + " class: " + declaringClass + " instance: " + instance + " value: " + theObject + " inst#: " + useInstruction + "}");
		InstanceFieldVariableDesignator instanceFieldVariableDesignator = new InstanceFieldVariableDesignator(instance, fieldName, declaringClass);
		addUseDesignator(instanceFieldVariableDesignator, useInstruction);
	}

	public static void handleInstanceMethodCall(Object instance, String calledMethodName, String calledMethodSignature, int numberOfParameters, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
		//System.out.println("Method call: " + calledMethodName + calledMethodSignature + " { instance: " + instance + " # of param " + numberOfParameters + " returns " + bReturnsValue + " method: " + methodSignature + "}");
	}

	public static void handleInstanceMethodReturn(Object instance,
			String calledClassName, String calledMethodName, String calledMethodSignature, int callInstruction, boolean bReturnsValue, String methodSignature)
	{
		//System.out.println("Method return: " + calledMethodName + calledMethodSignature + " { instance: " + instance + " called @" + callInstruction + " returns void <" + bReturnsValue + ">" + "} in method: " + methodSignature);

	}

	public static void handleMainMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc)
	{
		parameterIndex = 0;
		// Initialize
		stack = new Stack();
		methodTokenizer = new Tokeneizer("Method");


		MethodDesignator main = new MethodDesignator(className, methodName, desc);
		stack.push(main);

		Integer token = methodTokenizer.tokenize(main.toString());


		if(Control._METHOD_COVERAGE){
			methodDesignatorMap = new Hashtable<Integer, MethodDesignator>();
			methodMap = new Hashtable<Integer, Integer>();

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
			basicBlockTokeniser = new Tokeneizer("BasicBlock");
			bbStack = new Stack<BasicBlockDesignator>();
			basicBlockDesignatorMap = new Hashtable<Integer, BasicBlockDesignator>();
			basicBlockMap = new Hashtable<Integer, Integer>();
			if(Control._BASICBLOCK_PAIR_COVERAGE){ 
				basicBlockPairsDesignatorMap = new Hashtable<Pair<Integer, Integer>, BasicBlockPairsDesignator>();
				basicBlockPairsMap = new Hashtable<Pair<Integer, Integer>, Integer>();

			}
		}
		if(Control._DEFUSE_COVERAGE){
			variableTokenizer = new Tokeneizer("Variable");
			variableDesignatorMap = new Hashtable<Integer, VariableDesignator>();
			defMap = new Hashtable<Integer, Integer>();
			useMap = new Hashtable<Integer, Integer>();

			if(Control._DEFUSE_PAIR_COVERAGE){
				lastDefInstructionMap = new Hashtable<Integer, Integer>() ;
				defUsePairsMap = new Hashtable<Vector<Integer>, Integer>();
			}
		}
	}

	public static void handleMethodEntry(String methodParam, int nLocals, int nParams, String className, String methodName, String desc)
	{
		parameterIndex = 0;
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
		
		
		if(!isStatic){
			System.out.println("Method: " + methodDesignator.toString());
			handleParameterDef("this", callInstruction, methodDesignator.toString());
		}else{
			System.out.println("Static Method" + methodDesignator.toString());
		}
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
		//System.out.println("Array element use of type " + theObject.getClass().getName() + " in " + methodSignature + " @ instruction #" + useInstruction);
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, theObject, index, methodDesignatorMap.get(methodDesignatorKey));
		addUseDesignator(arrayElementVariableDesignator, useInstruction);
	}
	public static void handleArrayElementUseINT(Object array, int index, int value, int useInstruction, String methodSignature)
	{
		//System.out.println("Array element use of type INT in " + methodSignature + " @ instruction #" + useInstruction);
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Integer(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addUseDesignator(arrayElementVariableDesignator, useInstruction);
	}
	public static void handleArrayElementUseFLOAT(Object array, int index, float value, int useInstruction, String methodSignature)
	{
		//System.out.println("Array element use of type FLOAT in " + methodSignature + " @ instruction #" + useInstruction);
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Float(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addUseDesignator(arrayElementVariableDesignator, useInstruction);
	}
	public static void handleArrayElementUseLONG(Object array, int index, long value, int useInstruction, String methodSignature)
	{
		//System.out.println("Array element use of type LONG in " + methodSignature + " @ instruction #" + useInstruction);
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Long(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addUseDesignator(arrayElementVariableDesignator, useInstruction);
	}
	public static void handleArrayElementUseDOUBLE(Object array, int index, double value, int useInstruction, String methodSignature)
	{
		//System.out.println("Array element use of type DOUBLE in " + methodSignature + " @ instruction #" + useInstruction);
		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		ArrayElementVariableDesignator arrayElementVariableDesignator = new ArrayElementVariableDesignator(array, new Double(value), index, methodDesignatorMap.get(methodDesignatorKey));
		addUseDesignator(arrayElementVariableDesignator, useInstruction);
	}

	public static void handleLocalVariableUseI(int value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		//System.out.println("Use: {method: " + methodSignature + " type: INT" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addUseDesignator(localVariableDesignator, useInstruction);
	}
	public static void handleLocalVariableUseL(long value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		//System.out.println("Use: {method: " + methodSignature + " type: " + "LONG" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addUseDesignator(localVariableDesignator, useInstruction);
	}
	public static void handleLocalVariableUseF(float value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		//System.out.println("Use: {method: " + methodSignature + " type: " + "FLOAT" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addUseDesignator(localVariableDesignator, useInstruction);
	}
	public static void handleLocalVariableUseD(double value, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		//System.out.println("Use: {method: " + methodSignature + " type: " + "DOUBLE" + " name: " + variableName + " value: " + value + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addUseDesignator(localVariableDesignator, useInstruction);

	}
	public static void handleLocalVariableObjectUse(Object theObject, int variableIndex, String variableName, int useInstruction, String methodSignature)
	{
		//System.out.println("Use: {method: " + methodSignature + " type: " + theObject.getClass().getName() + " name: " + variableName + " value: " + theObject + "}");

		int methodDesignatorKey = methodTokenizer.getToken(methodSignature);		
		LocalVariableDesignator localVariableDesignator = new LocalVariableDesignator(methodDesignatorMap.get(methodDesignatorKey), variableName, variableIndex);
		addUseDesignator(localVariableDesignator, useInstruction);
	}
	public static void handleIINC(int value, int variableIndex, String variableName, int incInstruction, String methodSignature)
	{
	}

	public static void handleStaticFieldUseINT(int value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, fieldName);
		addUseDesignator(staticFieldVariableDesignator, useInstruction);
	}
	public static void handleStaticFieldUseLONG(long value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, fieldName);
		addUseDesignator(staticFieldVariableDesignator, useInstruction);
	}
	public static void handleStaticFieldUseFLOAT(float value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, fieldName);
		addUseDesignator(staticFieldVariableDesignator, useInstruction);
	}
	public static void handleStaticFieldUseDOUBLE(double value, String className, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + value + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, fieldName);
		addUseDesignator(staticFieldVariableDesignator, useInstruction);
	}
	public static void handleStaticFieldObjectUse(Object theObject, String className, String fieldName, int useInstruction, String methodSignature)
	{
		//System.out.println("Static Field use: {name: " + fieldName + " class: " + className + " value: " + theObject + " method: " + methodSignature + " inst#: " + useInstruction + "}");
		StaticFieldVariableDesignator staticFieldVariableDesignator = new StaticFieldVariableDesignator(className, fieldName);
		addUseDesignator(staticFieldVariableDesignator, useInstruction);
	}




	public static void generateProfile(){
		PrintWriter writer;
		methodTokenizer.printToFile();
		
		
		if(Control._METHOD_COVERAGE){
			writeToCSV(_METHOD_COVERAGE, methodMap, methodDesignatorMap);
		}
		if(Control._METHOD_PAIR_COVERAGE){
			writePairToCSV(_METHOD_PAIR_COVERAGE, methodPairsMap, methodDesignatorMap);
		}
		if(Control._BASICBLOCK_COVERAGE){
			basicBlockTokeniser.printToFile();
			writeToCSV(_BASICBLOCKS_COVERAGE, basicBlockMap, basicBlockDesignatorMap);
		}
		if(Control._BASICBLOCK_PAIR_COVERAGE){
			writePairToCSV(_BASICBLOCKS_PAIR_COVERAGE, basicBlockPairsMap, basicBlockDesignatorMap);
		}
		if(Control._DEFUSE_COVERAGE){
			variableTokenizer.printToFile();
			writeToCSV(_DEF_COVERAGE, defMap, variableDesignatorMap);
			writeToCSV(_USE_COVERAGE, useMap, variableDesignatorMap);
//
//			
//			try {
//				writer = new PrintWriter("out\\" + "useCoverage.txt", "UTF-8");
//				for (Integer key: useMap.keySet()) {
//					writer.printf("%-10d %d", key, useMap.get(key));
//					writer.println();
//				}
//				writer.close();
//			} catch (FileNotFoundException | UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			if(Control._DEFUSE_PAIR_COVERAGE){
				writeDefUseToCSV();
//				try {
//					writer = new PrintWriter("out\\" + "defUsePairCoverage.txt", "UTF-8");
//					writer.printf("%-10s %-10s %-10s %-10s", "TOKEN", "DEF INS", "USE INS", "COUNT");
//					writer.println();
//					for (Vector<Integer> key: defUsePairsMap.keySet()) {
//						writer.printf("%-10d %-10d %-10d %d", key.get(0), key.get(1), key.get(2), defUsePairsMap.get(key));
//						writer.println();
//					}
//					writer.close();
//				} catch (FileNotFoundException | UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
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


	private static void addDefDesignator(VariableDesignator variableDesignator, int instruction){
		int token = variableTokenizer.tokenize(variableDesignator.id);
		if(!variableDesignatorMap.containsKey(token)){
			variableDesignatorMap.put(token, variableDesignator);
		}

		if(defMap.containsKey(token)){
			Integer count = (Integer) defMap.get(token);
			count = new Integer(count.intValue() + 1);
			defMap.put(token, count);
			DebugLog.Log(_CLASS_INDENTIFIER_ + ".addDefDesignator(VariableDesignator variableDesignator, int instruction): " 
					+ "Variable def logged " + count);
		}else{
			defMap.put(token, 1);
			DebugLog.Log(_CLASS_INDENTIFIER_ + ".addDefDesignator(VariableDesignator variableDesignator, int instruction): " 
					+ "Variable def logged 1");			
		}

		if(Control._DEFUSE_PAIR_COVERAGE){
			lastDefInstructionMap.put(token, instruction);
		}


	}

	private static void addUseDesignator(VariableDesignator variableDesignator, int instruction){
		int token = variableTokenizer.tokenize(variableDesignator.id);

		if(!variableDesignatorMap.containsKey(token)){
			System.out.println("###################################################");
			System.out.println("WARNING: Use not defined: " + variableDesignator.id);
			System.out.println("###################################################");
			return;
		}
		
		if(useMap.containsKey(token)){
			Integer count = (Integer) useMap.get(token);
			count = new Integer(count.intValue() + 1);
			useMap.put(token, count);
			DebugLog.Log(_CLASS_INDENTIFIER_ + ".addUseDesignator(VariableDesignator variableDesignator, int instruction): " 
					+ "Variable def logged " + count);
		}else{
			useMap.put(token, 1);
			DebugLog.Log(_CLASS_INDENTIFIER_ + ".addUseDesignator(VariableDesignator variableDesignator, int instruction): " 
					+ "Variable def logged 1");			
		}

		if(Control._DEFUSE_PAIR_COVERAGE){
			if(!lastDefInstructionMap.containsKey(token)){
				return;
			}
			int def_instruction = lastDefInstructionMap.get(token);			
			Vector<Integer> duPair = new Vector<Integer>();
			duPair.add(token);
			duPair.add(def_instruction);
			duPair.add(instruction);

			if(defUsePairsMap.containsKey(duPair)){
				Integer count = (Integer) defUsePairsMap.get(duPair);
				count = new Integer(count.intValue() + 1);
				defUsePairsMap.put(duPair, count);
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".addUseDesignator(VariableDesignator variableDesignator, int instruction): " 
						+ "DEF USE pair " + duPair + " logged with count " + count);
			}else{
				defUsePairsMap.put(duPair, 1);
				DebugLog.Log(_CLASS_INDENTIFIER_ + ".addUseDesignator(VariableDesignator variableDesignator, int instruction): " 
						+ "DEF USE pair " + duPair + " logged with count 1");		
			}
		}	
	}


	private final static String COMMA_DELIMITER = ",";
	private final static String NEW_LINE_SEPARATOR= "\n";	
	private final static String COLUMNS_2_HEADER = "NAME,COUNT";
	public static void writeToCSV(String fileName, Hashtable<Integer, Integer> toWrite, Hashtable<Integer, ?> helper){
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(fileName);

			//Write the CSV file header
			fileWriter.append(COLUMNS_2_HEADER);

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new list to the CSV file
			for (int key : toWrite.keySet()) {
				fileWriter.append(helper.get(key).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(toWrite.get(key).toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}		
			System.out.println("CSV file '" + fileName +"' was created successfully");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter in '" + fileName +"'");
			e.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter in '" + fileName +"'");
				e.printStackTrace();
			}
		}
	}
	private final static String COLUMNS_3_HEADER = "CALLER,CALLED,COUNT";
	public static void writePairToCSV(String fileName, Hashtable<Pair<Integer, Integer>, Integer> toWrite, Hashtable<Integer, ?> helper){
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(fileName);

			//Write the CSV file header
			fileWriter.append(COLUMNS_3_HEADER);

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new student object list to the CSV file
			for (Pair<Integer, Integer> key : toWrite.keySet()) {
				fileWriter.append(helper.get(key.getFirst()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(helper.get(key.getSecond()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(toWrite.get(key).toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file '" + fileName +"' was created successfully");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter in '" + fileName +"'");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter in '" + fileName +"'");
				e.printStackTrace();
			}
		}
	}
	private final static String COLUMNS_4_HEADER = "VARIABLE,DEF INSTRUCTION,USE INSTRUCTION,COUNT";
	public static void writeDefUseToCSV(){
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(_DEFUSE_PAIR_COVERAGE);

			//Write the CSV file header
			fileWriter.append(COLUMNS_4_HEADER);

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new student object list to the CSV file
			for (Vector<Integer> key : defUsePairsMap.keySet()) {
				fileWriter.append(variableDesignatorMap.get(key.get(0)).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(key.get(1).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(key.get(2).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(defUsePairsMap.get(key).toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file '" + _DEFUSE_PAIR_COVERAGE +"' was created successfully");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter in '" + _DEFUSE_PAIR_COVERAGE +"'");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter in '" + _DEFUSE_PAIR_COVERAGE +"'");
				e.printStackTrace();
			}
		}
	}
	
}