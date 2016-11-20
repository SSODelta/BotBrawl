package com.brawlbots.botscript.conditions;

abstract class Combiner extends Condition {
	
	private Condition a,b;
	
	public Combiner(Condition a, Condition b, int hashId){
		super(hashId);
		this.a=a;
		this.b=b;
	}
	
	protected Condition getFirst(){
		return a;
	}
	protected Condition getLast(){
		return b;
	}

}
