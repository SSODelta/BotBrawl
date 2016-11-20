package com.brawlbots.botscript.expression;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public class Variable extends Expression {

	private String name;
	
	public Variable(String identifier, int hashId){
		super(hashId);
		this.name = identifier;
	}

	@Override
	public double evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		if(!engine.hasVar(getHashID(), toString(engine)))
			throw new ScriptRuntimeException("The variable '"+toString(engine)+"' is not defined at this point.");
		
		return engine.getVar(getHashID(), toString(engine));
	}

	@Override
	public void print(String prefix) {
		System.out.println(prefix+"variable "+toString());
	}

	public String toString(ScriptEngine engine) throws ScriptRuntimeException {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static Variable varFromString(String d, int hashId) throws TreeParseException{
		//Check if it references a map
		if(d.contains("[") && d.endsWith("]")){
			int first = d.indexOf('[');
			String mapName = d.substring(0, first);
			String expression = d.substring(first+1, d.length()-1);
			return new Map(mapName, Expression.fromString(expression, hashId), hashId);
		}
		return new Variable(d, hashId); //Otherwise it's a variable
	}
	
}
