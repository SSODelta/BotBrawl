package com.brawlbots.botscript.conditions;

import com.brawlbots.botscript.expression.Expression;
import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class Comparison extends Condition {
	
	private String search;
	private Expression left, right;
	
	private ComparisonType com;
	
	private Comparison(String left, String right, ComparisonType type, String search, int hashId) throws TreeParseException{
		super(hashId);
		this.left  = Expression.fromString(left, hashId);
		this.right = Expression.fromString(right, hashId);
		this.com = type;
		this.search = search;
	}
	
	public static final Comparison fromString(String s, int hashId) throws TreeParseException {
		
		String search = null;
		
		if(s.contains("=="))
			search = "==";
		else if(s.contains("!="))
			search = "!=";
		else if(s.contains("<="))
			search = "<=";
		else if(s.contains(">="))
			search = ">=";
		else if(s.contains("<"))
			search = "<";
		else if(s.contains(">"))
			search = ">";
		else if(s.contains("~="))
			search = "~=";
		
		if(search==null){
			s+="==1";
			search="==";
		}
		
		String[] split = s.split(search);
		if(split.length!=2)
			throw new TreeParseException("Invalidly formed comparison: '"+s+"'.");
		
		String left  = split[0],
			   right = split[1];
		
		if(left.length()==0 || right.length()==0)
			throw new TreeParseException("Comparisons cannot have blank left- or right side.");
			
		return new Comparison(left, right, getComparisonType(search), search, hashId);
	}
	
	private static final ComparisonType getComparisonType(String search) throws TreeParseException{
		switch(search){
			case "==":
				return ComparisonType.EQUALS;
			case "!=":
				return ComparisonType.NOT_EQUALS;
			case "<=":
				return ComparisonType.LESS_OR_EQUALS;
			case ">=":
				return ComparisonType.GREATER_OR_EQUALS;
			case "<":
				return ComparisonType.LESS_THAN;
			case ">":
				return ComparisonType.GREATER_THAN;
			case "~=":
				return ComparisonType.ALMOST_EQUALS;
		}
		throw new TreeParseException("Invalid comparison type: '"+search+"'.");
	}

	@Override
	public boolean evaluate(ScriptEngine engine) throws ScriptRuntimeException {
		return com.compare(left.evaluate(engine), right.evaluate(engine));
	}

	@Override
	public String toString() {
		return left.toString() + " "+search+" "+right.toString();
	}
	
	
}
