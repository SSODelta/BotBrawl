package com.brawlbots.botscript.script;

import com.brawlbots.botscript.conditions.Condition;
import com.brawlbots.botscript.tokentrees.TokenList;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class If extends ScriptFragment {

	private Condition condition;
	private Script methodBody, methodElse = null;
	
	public If(TokenList condition, Script methodBody, int hashId) throws TreeParseException{
		this(Condition.fromTokens(condition, hashId), methodBody, hashId);
	}
	
	private If(Condition condition, Script methodBody, int hashId){
		super(hashId);
		this.condition  = condition;
		this.methodBody = methodBody;
	}
	
	public void addElse(Script methodElse){
		this.methodElse = methodElse;
	}
	
	@Override
	public void doAction(ScriptEngine engine) throws ScriptRuntimeException {
		if(condition.evaluate(engine))
			engine.execute(methodBody);
		else if(methodElse != null)
			engine.execute(methodElse);
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"if "+condition.toString()+" then");
		for(ScriptFragment exp : methodBody)
			exp.print(prefix+"\t");
		
	}

}
