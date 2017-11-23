package com.asmproj;

import java.lang.ref.WeakReference;

public class InstanceFieldVariableDesignator extends VariableDesignator{
	protected WeakReference _instance; 
	protected String _fieldName;
	protected String _className;
	private String _id;
	
	public WeakReference get_instance() {
		return _instance;
	}
	public void set_instance(WeakReference instance) {
		this._instance = new WeakReference(instance);
	}
	public String get_fieldName() {
		return _fieldName;
	}
	public void set_fieldName(String _fieldName) {
		this._fieldName = _fieldName;
	}
	public String get_className() {
		return _className;
	}
	public void set_className(String className) {
		this._className = className;
	}
	public String get_id() {
		return _id;
	}
	
	private void buildId(){
		this._id =  "F " + _className + "." + _fieldName;
	}
	
	public InstanceFieldVariableDesignator(Object instance, String fieldName, String className){
		set_instance((WeakReference) instance);
		set_fieldName(fieldName);
		set_className(className);
		
		buildId();
	}
	
	public InstanceFieldVariableDesignator(InstanceFieldVariableDesignator instanceFieldVariableDesignator){
		set_instance(instanceFieldVariableDesignator.get_instance());
		set_fieldName(instanceFieldVariableDesignator._fieldName);
		set_className(instanceFieldVariableDesignator._className);
		
		buildId();
	}
	
	@Override
	public String toString() {
		return _id;
	}


	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof InstanceFieldVariableDesignator)
		{
			InstanceFieldVariableDesignator d = (InstanceFieldVariableDesignator) obj;
			return _className.equals(d._className) && _fieldName.equals(d._fieldName);
		}
		else
		{
			return false;
		}
	}

//	public Object getInstance() { return _instance.get(); }
//
//	public String getFieldName() { return _fieldName; }
//
//	public String getDeclaringClass() { return _declaringClass; }
//	public String getClassName() { return _declaringClass; }
//
//	public int hashCode() { return 91 * (91 * 17 + _objectHashCode) +
//			_fieldName.hashCode() + _declaringClass.hashCode(); }
//
//	public String toString()
//	{
//		return "InstanceField: " + _declaringClass + "." + _fieldName + "  " + _instance.get().getClass().getName() + " : " + getObjectToString(getObject()) + " value=" + _value;
//	}
}