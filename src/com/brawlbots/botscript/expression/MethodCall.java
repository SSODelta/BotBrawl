package com.brawlbots.botscript.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public class MethodCall extends Expression {

	private final String identifier;
	private final Expression[] arguments;
	private final String[] string_args;
	
	public MethodCall(String identifier, String args, int hashId) throws TreeParseException{
		super(hashId);
		this.identifier = identifier;
		
		if(args.length()==0){
			string_args = new String[0];
			arguments   = new Expression[0];
			return;
		}
		
		String queue = "";
		List<Expression> exps = new ArrayList<Expression>();
		List<String> strs = new ArrayList<String>();
		int depth = 0;
	    for (char c : args.toCharArray()){
	    	if(c == '(')
	    		depth++;
	    	if(c == ')')
	    		depth--;
	      
	    	//If we meet a comma which is meant to separate arguments (aka. not within a nested method call), then add what we already have
	    	if(c==',' && depth==0){
	    		exps.add(Expression.fromString(queue, hashId));
	    		strs.add(queue);
	    		queue = "";
	    		continue;
	    	}
	    	
	    	queue+=c;
	    }
	    
	    if(queue.length()>0){
    		exps.add(Expression.fromString(queue, hashId));
    		strs.add(queue);
	    }
	    
	    arguments = new Expression[exps.size()];
	    string_args = new String[exps.size()];
	    int i=0;
	    for(Expression e : exps)
	    	arguments[i++]=e;
	    i=0;
	    for(String str : strs)
	    	string_args[i++]=str;
	}
	
	@Override
	public double evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		
		//Remember the old variables
		HashMap<String, Double> prevVars = engine.getVars();
		
		//Set default return value
		engine.setVar(getHashID(), "return", 0.0);
		
		//Check if this is checking existence of a variable
		if(identifier.equals("local")){
			if(arguments.length!=1)
				throw new ScriptRuntimeException("Expected 1 argument(s) to procedure 'var', but received "+arguments.length+".");
				
			if(engine.hasLocalVar(string_args[0]))
				return 1.0;

			return 0.0;
		}
		//Check if this is checking existence of a variable
		if(identifier.equals("global")){
			if(arguments.length!=1)
				throw new ScriptRuntimeException("Expected 1 argument(s) to procedure 'var', but received "+arguments.length+".");
				
			if(engine.hasGlobalVar(string_args[0]))
				return 1.0;
			return 0.0;
		}
		
		//Check if this is checking existence of a variable
		if(identifier.equals("var")){
			if(arguments.length!=1)
				throw new ScriptRuntimeException("Expected 1 argument(s) to procedure 'var', but received "+arguments.length+".");
		
			if(engine.hasVar(getHashID(), string_args[0]))
				return 1.0;

			return 0.0;
		}
		
		//Check if this is checking existence of a variable
		if(identifier.equals("method")){
			if(arguments.length!=1)
				throw new ScriptRuntimeException("Expected 1 argument(s) to procedure 'var', but received "+arguments.length+".");
				
				if(engine.hasMethod(string_args[0]))
					return 1.0;
					
				return 0.0;
		}
		
		//Check system methods
		if(engine.getSystemLibrary().hasMethod(identifier)){
			double[] args = new double[arguments.length];
			for(int i=0; i<arguments.length; i++)
				args[i]=arguments[i].evaluate(engine);
			
			return engine.getSystemLibrary().evaluate(identifier, args);
		}
		
		//Check if the method has any arguments
		if(engine.hasArguments(identifier)){
		
			//Fetch all argument names and check we have the same amount
			String[] names = engine.getArguments(identifier);
		
			if(names.length != arguments.length)
				throw new ScriptRuntimeException("Expected "+names.length+" argument(s) to procedure '"+identifier+"', but received "+arguments.length);
	
			//Then evaluate all expressions and assign them to the correct variables in the engine
			for(int i=0; i<names.length; i++)
				engine.setVar(getHashID(), names[i], arguments[i].evaluate(engine));
		} else if(arguments.length > 0)
			throw new ScriptRuntimeException("Expected zero arguments to procedure '"+identifier+"', but received "+arguments.length);
		
		//Then run the method
		engine.execute(engine.getMethod(identifier));
		
		//Get the return values
		double ret = engine.getVar(getHashID(), "return");
		
		//Put the old variables back
		engine.setVars(prevVars);
		
		//And return the return value
		return ret;
	}

	@Override
	protected void print(String prefix) {
		System.out.println(prefix+"method call ("+identifier+")");
		for(Expression e : arguments)
			System.out.println(prefix+"\t arg: "+prefix+"\t\t"+e.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("method call, ");
		sb.append(identifier);
		sb.append('(');
		int i=0;
		for(Expression e : arguments)
			sb.append(e.toString()).append(i++!=arguments.length-1 ? ", " : "");
		sb.append(')');
		return sb.toString();
	}

}
