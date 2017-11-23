package com.asmproj;

class LocalVariableDesignator extends VariableDesignator{
 	// protected MethodThreadPair methodThreadPair;
	protected final MethodDesignator methodDesignator;
	protected final int variableIndex;
	protected final String variableName;
	private final String _id;
	
	private String builtID(){
		return "L" + variableIndex + ":" + variableName + ":" + methodDesignator.className + "." + methodDesignator.methodName
				+ methodDesignator.methodSignature;
	}
	
	public LocalVariableDesignator(MethodDesignator methodDesignator, String variableName, int variableIndex){
		this.methodDesignator = new MethodDesignator(methodDesignator);
		this.variableName = variableName;
		this.variableIndex = variableIndex;
		
		this._id = builtID();
	}
	
	public LocalVariableDesignator(LocalVariableDesignator localVariableDesignator){
		this.methodDesignator = new MethodDesignator(localVariableDesignator.methodDesignator);
		this.variableName = localVariableDesignator.variableName;
		this.variableIndex = localVariableDesignator.variableIndex;
		
		this._id = builtID();
	}	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LocalVariableDesignator){
			LocalVariableDesignator localVariableDesignator = (LocalVariableDesignator) obj;
			boolean equal = variableIndex == localVariableDesignator.variableIndex;
			if(!equal) return false;
			equal = variableName.equals(localVariableDesignator.variableName);
			if(!equal) return false;
			equal = localVariableDesignator.methodDesignator.equals(methodDesignator);
			return equal;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return _id;
	}
	
	
//	private String _obtId;
//
//	public String getOBTID()
//	{
//		return _obtId;
//	}
//	
//	private void buildOBTId()
//	{
//		MethodDesignator method = methodThreadPair.getMethod();
//		if (getObject() != null)
//		{
//			_obtId = "L " + variableIndex + ":" + variableName + ":" + method.getClassName() + "." + method.getMethodName() 
//					+ " " + method.getMethodSignature() + "|" + 
//					getObject().getClass().getName();
//		}
//		else
//		{
//			_obtId = "L " + variableIndex + ":" + variableName + ":" + method.getClassName() + "." + method.getMethodName() 
//					+ " " + method.getMethodSignature();
//		}
//	}
//
//	public LocalVariableDesignator(MethodThreadPair methodThreadPair, int variableIndex)
//	{
//		this.methodThreadPair = methodThreadPair;
//		this.variableIndex = variableIndex;
//		//this._lastComputed = IFProfiler._nClock;
//		buildOBTId();
//	}
//
//	public LocalVariableDesignator(MethodThreadPair methodThreadPair, int variableIndex, String variableName)
//	{
//		this.methodThreadPair = methodThreadPair;
//		this.variableIndex = variableIndex;
//		this.variableName = variableName;
//		//this._lastComputed = IFProfiler._nClock;
//		buildOBTId();
//	}
//	public LocalVariableDesignator(MethodThreadPair methodThreadPair, int variableIndex, String variableName, Number variableValue)
//	{
//		this.methodThreadPair = methodThreadPair;
//		this.variableIndex = variableIndex;
//		this.variableName = variableName;
//		this._value = variableValue;
//		//this._lastComputed = IFProfiler._nClock;
//		buildOBTId();
//	}
//
//	public LocalVariableDesignator(MethodThreadPair methodThreadPair, int variableIndex, String variableName, Object theObject)
//	{
//		this.methodThreadPair = methodThreadPair;
//		this.variableIndex = variableIndex;
//		this.variableName = variableName;
//		//this._lastComputed = IFProfiler._nClock;
//
//		computeObjectValue(theObject);
//		setObject(theObject);
//			
//		buildOBTId();
//	}
//
//	public int getVariableIndex()
//	{
//		return variableIndex;
//	}
//	public String getVariableName()
//	{
//		return variableName;
//	}
//
//	public String getClassName()
//	{
//		return methodThreadPair.getMethod().getClassName();
//	}
//
//	public boolean equals(Object o) 
//	{
//		if (o instanceof LocalVariableDesignator)
//		{	
//			LocalVariableDesignator d = (LocalVariableDesignator)o;
//			return methodThreadPair.equals(d.methodThreadPair) && 
//				variableIndex == d.variableIndex;
//		}
//		else
//		{
//			return false;
//		}
//	}
//
//	public int hashCode() { return (methodThreadPair.hashCode() + variableIndex); }
//
//	public String toString()
//	{
//		return "LocalVariable: index(" + variableIndex + ")  name(" + variableName + ") " + methodThreadPair.toString() + " : " + getObjectToString(getObject()) + " value=" + _value;//  + "  T: " + getDefTime() + "  H: " + System.identityHashCode(this);
//	}
}