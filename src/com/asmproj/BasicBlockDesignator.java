package com.asmproj;

public class BasicBlockDesignator{
	public String className;
    public String methodName;
    public String methodSignature;
    public int lineNumber;
    public int instruction;

    public BasicBlockDesignator(String className, String methodName, String methodSignature, int lineNumber, int instruction) 
    {
    	this.className = className;
    	this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.lineNumber = lineNumber;
		this.instruction = instruction;
    }
    
    public BasicBlockDesignator(BasicBlockDesignator bbd) {
    	this.className = new String(bbd.className);
    	this.methodName = new String(bbd.methodName);
		this.methodSignature = new String(bbd.methodSignature);
		this.lineNumber = bbd.lineNumber;
		this.instruction = bbd.instruction;
	}
    
    @Override
    public String toString() {
    	return className + "." + methodName + methodSignature + " @line: " + lineNumber;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof BasicBlockDesignator){
    		BasicBlockDesignator bbd = (BasicBlockDesignator) obj;
    		return this.className.equals(bbd.className)
    				&& this.methodName.equals(bbd.methodName)
    				&& this.methodSignature.equals(bbd.methodSignature)
    				&& this.lineNumber == bbd.lineNumber
    				&& this.instruction == bbd.instruction;
    	}
    	return false;
    }
}