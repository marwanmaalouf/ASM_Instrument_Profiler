package com.asmproj;

public class BasicBlockPairsDesignator{
	public final BasicBlockDesignator caller;
	public final BasicBlockDesignator called;
	
	public BasicBlockPairsDesignator(BasicBlockDesignator caller, BasicBlockDesignator called) {
		this.caller = new BasicBlockDesignator(caller);
		this.called = new BasicBlockDesignator(called);
	}
	
	@Override
	public String toString() {
		return "{"  + caller.toString() + ", " + called.toString() + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BasicBlockPairsDesignator){
			BasicBlockPairsDesignator mpd = (BasicBlockPairsDesignator) obj;
			return this.caller.equals(mpd.caller) && this.called.equals(mpd.called);
		}
		return false;
	}
}