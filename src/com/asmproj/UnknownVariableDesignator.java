package com.asmproj;

public class UnknownVariableDesignator extends VariableDesignator{
	private final static String _name = "UNKNOWN_SOURCE";
	
	public UnknownVariableDesignator() {
		id = _name;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof UnknownVariableDesignator)
		{
			return true;
		}
		else
			return false;
	}

	
	@Override
	public String toString() {
		return _name;
	}
}