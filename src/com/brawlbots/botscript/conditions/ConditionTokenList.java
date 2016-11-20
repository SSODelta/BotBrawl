package com.brawlbots.botscript.conditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.brawlbots.botscript.tokentrees.Token;
import com.brawlbots.botscript.tokentrees.TokenList;

final class ConditionTokenList implements Iterable<Token> {

	private List<Token> tokens;
	
	public ConditionTokenList(TokenList tl){
		tokens = new ArrayList<Token>();
		String queue = "";
		
		for(Token t : tl){
			
			if(t.getType() == Token.TYPE_FUNCTION_CALL ||t.getType() == Token.TYPE_TEXT || t.getType() == Token.TYPE_EXPRESSION_ADD_SUB || t.getType() == Token.TYPE_EXPRESSION_MULT_DIV)
				queue+=t.getData();
			
			if(t.getType() == Token.TYPE_LEFT_PARANTHESIS || t.getType() == Token.TYPE_RIGHT_PARANTHESIS
					|| t.getData().equals("||") || t.getData().equals("&&")){
				if(queue.length()>0)
					tokens.add(new Token(queue));
				tokens.add(t);
				queue="";
			}
		}
		
		if(queue.length()>0)
			tokens.add(new Token(queue));
	}

	@Override
	public Iterator<Token> iterator() {
		return tokens.iterator();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(Token t : this){
			sb.append(t.getData());
			sb.append(", ");
		}
		
		sb.append("]");
		
		return sb.toString().replace(", ]", "]");
	}
}
