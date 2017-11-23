package com.asmproj;

import java.lang.ref.WeakReference;

@SuppressWarnings("rawtypes")
public class ArrayElementVariableDesignator extends VariableDesignator{
	
	private final WeakReference _array;
	private Object _object;
	private final int variableIndex;
	private final String _id;
	private final MethodDesignator methodDesignator;
	
	
	public WeakReference get_array() {
		return _array;
	}

	public Object get_object() {
		return _object;
	}

	public void setObject(Object obj){
		_object = obj;
	}
	
	public int getVariableIndex() {
		return variableIndex;
	}

	public String get_id() {
		return _id;
	}

	public MethodDesignator getMethodDesignator() {
		return methodDesignator;
	}

	public String getClassName() 
	{ 
		return methodDesignator.className;
	}
	
	private String buildId(){
		return "A" + variableIndex + ":" + _array.toString() + ":" + methodDesignator.className + "." + methodDesignator.methodName
				+ methodDesignator.methodSignature;
	}

	public ArrayElementVariableDesignator(Object array, Object theObject, int index, MethodDesignator methodDesignator) {
		 this._array = new WeakReference(array);
		 this.variableIndex = index;
		 this.methodDesignator = new MethodDesignator(methodDesignator);
		 
		 setObject(theObject);
		 
		 _id = buildId(); 
	}
	
	public ArrayElementVariableDesignator(ArrayElementVariableDesignator aevd) {
		 this._array = get_array();
		 this.variableIndex = getVariableIndex();
		 this.methodDesignator = new MethodDesignator(getMethodDesignator());
		 
		 setObject(get_object());
		 
		 _id = buildId(); 
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArrayElementVariableDesignator)
		{
			ArrayElementVariableDesignator d = (ArrayElementVariableDesignator)obj;
			return (variableIndex == d.variableIndex) && (methodDesignator.equals(d.methodDesignator));
		}
		else
		{
			return false;
		}
	}

	public Object getArray() { return _array.get(); }

	@Override
	public String toString()
	{
		return _id;
		//return "ArrayElement: index(" + _index + ")" + getObjectToString(_array.get()) + " : " + getObjectToString(getObject());// + "  T: " + getDefTime();
	}
}