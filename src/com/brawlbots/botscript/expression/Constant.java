package com.brawlbots.botscript.expression;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class Constant extends Expression {

	private final double value;
	
	public Constant(double value, int hashId){
		super(hashId);
		this.value = value;
	}
	
	public Constant(String value, int hashId) throws TreeParseException {
		this(getDouble(value), hashId);
	}
	
	private static final double getDouble(String s) throws TreeParseException {
		try{
			return Double.parseDouble(s);
		} catch(NumberFormatException e){
			throw new TreeParseException("Unable to parse '"+s+"' as number.");
		}
	}
	
	@Override
	public double evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		return value;
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+value);
	}

	@Override
	public String toString() {
		return ""+value;
	}

}
