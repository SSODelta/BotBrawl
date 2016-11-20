package com.brawlbots.botscript.expression;

import com.brawlbots.botscript.script.ScriptRuntimeException;

public class LibraryMethod {

	private String identifier;
	private LibraryMethodInterface func;
	private int args;
	
	public LibraryMethod(String name, int args, LibraryMethodInterface func){
		this.identifier = name;
		this.func       = func;
		this.args       = args;
	}
	
	public boolean is(String name){
		return identifier.equals(name);
	}
	
	public double eval(double... ds) throws ScriptRuntimeException{
		if(ds.length != args)
			throw new ScriptRuntimeException("Trying to call library method '"+identifier+"' with "+ds.length+" arguments. Expected "+args+".");
		return func.evaluate(ds);
	}
	
	
}
