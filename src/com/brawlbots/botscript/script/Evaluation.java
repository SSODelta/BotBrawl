package com.brawlbots.botscript.script;

import com.brawlbots.botscript.expression.Expression;

public class Evaluation extends ScriptFragment {

	private Expression exp;
	
	public Evaluation(Expression e, int hashId){
		super(hashId);
		this.exp = e;
	}
	
	@Override
	public void doAction(ScriptEngine engine) throws ScriptRuntimeException {
		exp.evaluate(engine);
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"evaluation: \n"+prefix+"\t"+exp.toString());
	}

}
