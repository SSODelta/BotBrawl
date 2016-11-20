package com.brawlbots.botscript.expression;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;

public class Map extends Variable {

	private Expression exp;
	
	public Map(String map, Expression exp, int hashId) {
		super(map, hashId);
		this.exp = exp;
	}

	public String toString(ScriptEngine engine) throws ScriptRuntimeException {
		return super.toString() + "_" +super.toString().hashCode() + "_" + Double.valueOf(Math.floor(exp.evaluate(engine))).hashCode();
	}
	
	@Override
	public void print(String prefix) {
		System.out.println(prefix+toString());
		exp.print(prefix+"\t");
	}
}
