package com.brawlbots.botscript.expression;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;

public final class Add extends BinaryExpression {

	public Add(Expression a, Expression b, int hashId) {
		super(a, b, hashId);
	}

	@Override
	public double evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		return getFirst().evaluate(engine)
			 + getLast().evaluate(engine);
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"add");
		getFirst().print(prefix+"\t");
		getLast().print(prefix+"\t");
	}

	@Override
	public String toString() {
		return "("+getFirst().toString() + ") + (" +getLast().toString()+")";
	}

}
