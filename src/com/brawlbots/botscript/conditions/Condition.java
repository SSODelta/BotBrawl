package com.brawlbots.botscript.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.Token;
import com.brawlbots.botscript.tokentrees.TokenList;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public abstract class Condition {

	private int hashId;
	
	protected Condition(int hashId){
		this.hashId = hashId;
	}
	
	public int getHashID(){
		return hashId;
	}
	
	public static Condition fromString(String s, int hashId) throws TreeParseException {
		if(s.startsWith("(") && s.endsWith(")"))
			return fromString(s.substring(1, s.length()-1), hashId);
		
		return fromTokens(new TokenList(s), hashId);
	}
	
	public static final Condition fromTokens(TokenList tl, int hashId) throws TreeParseException{
		return fromTokens(new ConditionTokenList(tl), hashId);
	}
	
	private static final Condition fromTokens(ConditionTokenList tl, int hashId) throws TreeParseException{
		List<Token> rpn = toRPN(tl);
		return parseRPN(rpn, hashId);
	}
	
	private static final Condition parseRPN(List<Token> rpn, int hashId) throws TreeParseException{
		Stack<Condition> stack = new Stack<Condition>();
		
		for(Token t : rpn){
			if(t.getType() == Token.TYPE_TEXT || t.getType() == Token.TYPE_FUNCTION_CALL){
				
				String s = t.getData();
				if(s.contains("||") || s.contains("&&"))
					stack.push(Condition.fromString(s, hashId));
				else
					stack.push(Comparison.fromString(s, hashId));
				continue;
			} else if(t.getType() == Token.TYPE_CONDITION_AND){
				if(stack.size()<2)
					throw new TreeParseException("Not enough operands to produce this node: "+t.getData());
				
				Condition right = stack.pop(),
						  left  = stack.pop();
				
				stack.push(new And(left, right, hashId));
			} else if(t.getType() == Token.TYPE_CONDITION_OR){
				if(stack.size()<2)
					throw new TreeParseException("Not enough operands to produce this node");
				
				Condition right = stack.pop(),
						  left  = stack.pop();
				
				stack.push(new Or(left, right, hashId));
			}
			
		}
		
		if(stack.size()!=1)
			throw new TreeParseException("Invalidly formed RPN tokenlist");
		
		return stack.pop();
	}
	
	
	
	private static final List<Token> toRPN(ConditionTokenList tl) throws TreeParseException{
		List<Token> outputQueue = new ArrayList<Token>();
		Stack<Token> operatorStack = new Stack<Token>();
		
		for(Token t : tl){
			switch(t.getType()){

				case Token.TYPE_FUNCTION_CALL:
				case Token.TYPE_TEXT:
					outputQueue.add(t);
					break;
					
				case Token.TYPE_CONDITION_OR:
					if(!operatorStack.isEmpty()){
						int type = operatorStack.peek().getType();
						while(type == Token.TYPE_CONDITION_AND  || type == Token.TYPE_CONDITION_OR){
							if(operatorStack.isEmpty())break;		
							outputQueue.add(operatorStack.pop());
							if(!operatorStack.isEmpty())type = operatorStack.peek().getType();
						}
					}
					operatorStack.add(t);
					break;
					
				case Token.TYPE_CONDITION_AND:
					operatorStack.add(t);
					break;
					
				case Token.TYPE_LEFT_PARANTHESIS:
					operatorStack.add(t);
					break;
					
				case Token.TYPE_RIGHT_PARANTHESIS:
					if(!operatorStack.isEmpty()){
						int type = operatorStack.peek().getType();
						while(type != Token.TYPE_LEFT_PARANTHESIS){
							outputQueue.add(operatorStack.pop());
							if(!operatorStack.isEmpty())type = operatorStack.peek().getType();
							if(operatorStack.isEmpty())
								throw new TreeParseException("Mismatched parentheses");
						}
						type = operatorStack.peek().getType();
						if(type == Token.TYPE_LEFT_PARANTHESIS)
							operatorStack.pop();
					}
					break;
			}
		}
		
		while(!operatorStack.isEmpty()){
			Token t = operatorStack.pop();
			if(t.getType() == Token.TYPE_LEFT_PARANTHESIS || t.getType() == Token.TYPE_RIGHT_PARANTHESIS)
				throw new TreeParseException("Mismatched parentheses");
			
			outputQueue.add(t);
		}
		
		return outputQueue;
	}
	
	public abstract boolean evaluate(ScriptEngine engine) throws ScriptRuntimeException;
	
	public abstract String toString();
	
}
