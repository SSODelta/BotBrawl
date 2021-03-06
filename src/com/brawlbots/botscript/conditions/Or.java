package com.brawlbots.botscript.conditions;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;

public final class Or extends Combiner {

	public Or(Condition a, Condition b, int hashId) {
		super(a, b, hashId);
	}

	@Override
	public boolean evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		return getFirst().evaluate(engine) || getLast().evaluate(engine);
	}

	@Override
	public String toString() {
		return "("+getFirst().toString() + ") or ("+getLast().toString()+")";
	}

}
