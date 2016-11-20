package com.brawlbots.botscript.script;

import com.brawlbots.botscript.expression.Expression;
import com.brawlbots.botscript.expression.Variable;
import com.brawlbots.botscript.tokentrees.TreeParseException;

/**
 * @author nikol
 *
 */
public final class Assignment extends ScriptFragment {
	
	private Variable var;
	private Expression val;
	
	public Assignment(Variable variable, String value, int hashId) throws TreeParseException{
		super(hashId);
		this.var = variable;
		this.val = Expression.fromString(value, hashId);
	}
	
	@Override
	public void doAction(ScriptEngine engine) throws ScriptRuntimeException {
		String varr = var.toString(engine);
		double vall = val.evaluate(engine);
		engine.setVar(getHashID(), varr, vall);
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"assignment: "+var.toString()+" := "+val);
		var.print(prefix+"\t");
	}


}
