package com.brawlbots.botscript.script;

import com.brawlbots.botscript.conditions.Condition;
import com.brawlbots.botscript.tokentrees.TokenList;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class While extends ScriptFragment {

	private Condition condition;
	private Script methodBody;
	
	public While(TokenList condition, Script methodBody, int hashId) throws TreeParseException{
		this(Condition.fromTokens(condition, hashId), methodBody, hashId);
	}
	
	private While(Condition condition, Script methodBody, int hashId){
		super(hashId);
		this.condition  = condition;
		this.methodBody = methodBody;
	}
	
	private static final float MAX_TIME = 1*1000f;
	
	@Override
	public void doAction(ScriptEngine engine) throws ScriptRuntimeException {
		float start = System.currentTimeMillis();
		while(condition.evaluate(engine)){
			if(System.currentTimeMillis() - start > MAX_TIME)
				throw new ScriptRuntimeException("Loop took too long to execute.");
			engine.execute(methodBody);
		}
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"while "+condition.toString()+" then");
		for(ScriptFragment exp : methodBody)
			exp.print(prefix+"\t");
	}

}
