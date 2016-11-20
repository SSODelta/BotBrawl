package com.brawlbots.botscript.script;

public abstract class ScriptFragment {

	private int hashId;
	
	protected ScriptFragment(int hashId){
		this.hashId = hashId;
	}
	
	protected int getHashID(){
		return hashId;
	}
	
	public abstract void doAction(ScriptEngine engine) throws ScriptRuntimeException;
	
	protected abstract void print(String prefix);
	
	protected void print(){
		print("");
	}
}
