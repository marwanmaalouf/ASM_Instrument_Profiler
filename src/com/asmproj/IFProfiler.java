package com.asmproj;

public class IFProfiler
{

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

    public static void handleThrow()
    {

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

    public static void handleMainMethodEntry(String methodParam, int nLocals, int nParams)
    {
    }

    public static void handleMethodEntry(String methodParam, int nLocals, int nParams)
    {

    }

    public static void handleMethodExit(boolean bReturnsValue, int returnInstruction, String methodSignature)
    {
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



}



