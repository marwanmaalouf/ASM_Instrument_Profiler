package com.asmproj;

public class MethodPairDesignator extends Designator{
	public final MethodDesignator caller;
	public final MethodDesignator called;
	
	public MethodPairDesignator(MethodDesignator caller, MethodDesignator called) {
		this.caller = new MethodDesignator(caller);
		this.called = new MethodDesignator(called);
	}
	
	@Override
	public String toString() {
		return "{"  + caller.toString() + ", " + called.toString() + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MethodPairDesignator){
			MethodPairDesignator mpd = (MethodPairDesignator) obj;
			return this.caller.equals(mpd.caller) && this.called.equals(mpd.called);
		}
		return false;
	}
}