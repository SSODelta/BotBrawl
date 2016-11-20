package com.brawlbots.botscript.expression;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.Token;
import com.brawlbots.botscript.tokentrees.TokenList;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public abstract class Expression {

	private int hashId;
	
	protected Expression(int hashId){
		this.hashId = hashId;
	}
	
	protected int getHashID(){
		return hashId;
	}
	
	public abstract double evaluate(ScriptEngine engine) throws ScriptRuntimeException ;
	
	protected abstract void print(String prefix);
	
	public abstract String toString();
	
	public void print(){
		print("");
	}
	
	public static final Expression fromString(String s, int hashId) throws TreeParseException{
		TokenList tl = new TokenList(s);
		return fromTokens(tl, hashId);
	}
	
	protected static final Expression fromTokens(TokenList tl, int hashId) throws TreeParseException{
		List<Token> rpn = toRPN(tl);
		Expression e = parseRPN(rpn, hashId);
		return e;
	}
	
	private static final Expression parseRPN(List<Token> rpn, int hashId) throws TreeParseException{
		Stack<Expression> stack = new Stack<Expression>();
		
		for(Token t : rpn){
			if(t.getType() == Token.TYPE_FUNCTION_CALL){
				int first = t.getData().indexOf("(");
				String method = t.getData().substring(0, first),
					   args   = t.getData().substring(first+1, t.getData().length()-1);
				Expression q = new MethodCall(method,args, hashId);
				stack.push(q);
				continue;
			} else if(t.getType() == Token.TYPE_TEXT){
				try{ //Try to parse the text as a number
					double val = Double.parseDouble(t.getData());
					stack.push(new Constant(val, hashId));
				} catch(NumberFormatException e){ //Otherwise, assume it's a variable
					Variable v = Variable.varFromString(t.getData(), hashId);
					stack.push(v);
				}
				continue;
			} else if(t.getType() == Token.TYPE_EXPRESSION_ADD_SUB || t.getType() == Token.TYPE_EXPRESSION_MULT_DIV){   
				if((t.getType() == Token.TYPE_EXPRESSION_MULT_DIV && stack.size()<2) || stack.size()==0)
					throw new TreeParseException("Not enough operands to produce this node");
				
				if(stack.size()>=2){
					Expression right = stack.pop(),
							   left  = stack.pop();
				
					switch(t.getData().charAt(0)){
						case '+':
							stack.push(new Add(left, right, hashId));
							break;
						case '-':
							stack.push(new Sub(left, right, hashId));
							break;
						case '*':
							stack.push(new Mult(left, right, hashId));
							break;
						case '/':
							stack.push(new Div(left, right, hashId));
							break;
					}
				} else if(stack.size()==1){
					Expression exp = stack.pop();
					switch(t.getData().charAt(0)){
					case '+':
						stack.push(exp);
						break;
					case '-':
						stack.push(new Sub(new Constant(0, hashId), exp, hashId));
						break;
					}
				}
				continue;
			}
		}
		
		
		if(stack.size()!=1)
			throw new TreeParseException("Invalidly formed RPN tokenlist.");
	
		return stack.pop();
	}

	private static final List<Token> toRPN(TokenList tl) throws TreeParseException{
		List<Token> outputQueue = new ArrayList<Token>();
		Stack<Token> operatorStack = new Stack<Token>();
		
		for(Token t : tl){
			switch(t.getType()){
			
				case Token.TYPE_FUNCTION_CALL:
				case Token.TYPE_TEXT:
					outputQueue.add(t);
					break;
					
				case Token.TYPE_EXPRESSION_ADD_SUB:
					if(!operatorStack.isEmpty()){
						int type = operatorStack.peek().getType();
						while(type == Token.TYPE_EXPRESSION_ADD_SUB  || type == Token.TYPE_EXPRESSION_MULT_DIV){
							if(operatorStack.isEmpty())break;		
							outputQueue.add(operatorStack.pop());
							if(!operatorStack.isEmpty())type = operatorStack.peek().getType();
						}
					}
					operatorStack.add(t);
					break;
					
				case Token.TYPE_EXPRESSION_MULT_DIV:
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
}
