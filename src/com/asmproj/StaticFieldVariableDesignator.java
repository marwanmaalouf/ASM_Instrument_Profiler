package com.asmproj;


public class StaticFieldVariableDesignator extends VariableDesignator{
	protected String _className;
	protected String _fieldName;
	protected String _id; 
	
	protected String buildId(){
		return "S " + _className + "." + _fieldName;
	}
	
	public StaticFieldVariableDesignator(String className, String fieldName) {
		_className = className;
		_fieldName = fieldName;
		_id = buildId();
	}
	
	public StaticFieldVariableDesignator(StaticFieldVariableDesignator staticFieldVariableDesignator){
		_className = staticFieldVariableDesignator._className;
		_fieldName = staticFieldVariableDesignator._fieldName;
		
		_id = buildId();
	}
	
	
	public String getID()
	{
		return _id;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof StaticFieldVariableDesignator)
		{
			StaticFieldVariableDesignator d = (StaticFieldVariableDesignator)obj;
			return _className.equals(d._className) && _fieldName.equals(d._fieldName);
		}
		else
			return false;
	}

	
	@Override
	public String toString() {
		return _id;
	}
	public String getClassName() { return _className; }

	public String getFieldName() { return _fieldName; }

	public int hashCode() { return 91 * (91 * 17 + _className.hashCode()) + _fieldName.hashCode(); }

//	public String toString()
//	{
//		return "StaticField: " + _className + "." + _fieldName + " : " + getObjectToString(getObject()) + " value=" + _value;//  + "  T: " + getDefTime();
//	}
}