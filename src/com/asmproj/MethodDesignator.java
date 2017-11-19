package com.asmproj;

public class MethodDesignator extends Designator{
	public final String className;
	public final String methodName;
	public final String methodSignature;
	
	public MethodDesignator(String className, String methodName, String methodSignature) {
		this.className = new String(className);
		this.methodName = new String(methodName);
		this.methodSignature = new String(methodSignature);
	}
	
	public MethodDesignator(MethodDesignator methodDesignator) {
		this.className = methodDesignator.className;
		this.methodName = methodDesignator.methodName;
		this.methodSignature = methodDesignator.methodSignature;
	}

	@Override
	public String toString() {
		return className + "." + methodName + methodSignature;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MethodDesignator){
			MethodDesignator md = (MethodDesignator) obj;
			return this.toString().equals(md.toString());
		}
		return false;
	}
}