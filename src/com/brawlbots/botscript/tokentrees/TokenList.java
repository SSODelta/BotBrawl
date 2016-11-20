package com.brawlbots.botscript.tokentrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class TokenList implements Iterable<Token> {

	private List<Token> tokens;
	
	public TokenList(){
		tokens = new ArrayList<Token>();
	}
	
	public TokenList(String s){
		
		this();
		
		String queue = "";
		boolean takeNext = false;
		int round_depth = 0,
			square_depth = 0;
		for(char c : clean(s).toCharArray()){
			
			if(!(queue.equals("if") || queue.equals("while")) && c=='('){
				round_depth++;
				queue+=c;
				takeNext=false;
				continue;
			} else if(round_depth>0){
				if(c==')' && --round_depth==0){
					tokens.add(new Token(queue+c));
					queue="";
					takeNext=false;
					continue;
				}
				takeNext=false;
				queue+=c;
				continue;
			}
			
			if(c == '['){
				square_depth++;
			} if (c==']' && --square_depth==0){
				tokens.add(new Token(queue+c));
				queue="";
				takeNext=false;
				continue;
			} else if(square_depth==0){
			
			if(c == '(' || c==')' || c=='{' || c=='}' || c==';' || c=='+' || c=='-' || c=='*' || c=='/'){
				if(queue.length()>0)
					tokens.add(new Token(queue));
				tokens.add(new Token(""+c));
				queue="";
				takeNext=false;
				continue;
			}
			if(takeNext && (c=='=' || (c=='&') || c=='|')){
				tokens.add(new Token(queue+c));
				queue="";
				takeNext=false;
				continue;
			} else if(c==':' || c=='>' || c=='!' || c=='<' || c=='=' || c=='|' || c=='&'){
				takeNext=true;
				if(queue.length()>0){
					tokens.add(new Token(queue));
					queue="";}
			}}
			
			if(!takeNext)
				queue += c;
			else if(takeNext){
				if(c!=':' && c!='&' && c!='|' && c!='>' && c!='<')
				takeNext=false;
				if (queue.length()>0)
				tokens.add(new Token(queue));
				queue=""+c;
			}
		}
		
		if(queue.length()>0)
			tokens.add(new Token(queue));
		
		collectMinus();
	}
	
	private TokenList(Collection<Token> tokens){
		this();
		this.tokens.addAll(tokens);
	}
	
	public void collectMinus(){
		if(tokens.size()<3)return;
		List<Token> newTokens = new ArrayList<Token>();
		for(int i=0; i<tokens.size(); i++){
			
			if(i+2<tokens.size() 
			 &&(tokens.get(i).getType() == Token.TYPE_EXPRESSION_ADD_SUB
			 || tokens.get(i).getType() == Token.TYPE_EXPRESSION_MULT_DIV
			 || tokens.get(i).getType() == Token.TYPE_LEFT_PARANTHESIS
			 || tokens.get(i).getData().equals(":="))
			 && tokens.get(i+1).getData().equals("-")
			 && tokens.get(i+2).getType() == Token.TYPE_TEXT){
				
				newTokens.add(tokens.get(i));
				newTokens.add(new Token("-" + tokens.get(i+2).getData()));
				i+=2;
				continue;
				
			}
			
			newTokens.add(tokens.get(i));
		}
		tokens=newTokens;
	}
	
	public String getDataAt(int i){
		return tokens.get(i).getData();
	}
	
	public TokenList subList(int start, int end){
		return new TokenList(tokens.subList(start, end));
	}
	
	public boolean isAssignment(){
		if(tokens.size()<3)return false;
		return tokens.get(1).getData().equalsIgnoreCase(":=");
	}
	
	public boolean isMethod(){
		if(tokens.size()<3)return false;
		
		return tokens.get(0).getType() 				 == Token.TYPE_TEXT
			&& tokens.get(1).getType() 				 == Token.TYPE_LEFT_PARANTHESIS 
			&& tokens.get(tokens.size()-1).getType() == Token.TYPE_RIGHT_PARANTHESIS;

	}
	
	public boolean isConditional(){
		if(tokens.size()<3)return false;

		return tokens.get(0).getType() 				 == Token.TYPE_TEXT
			&& tokens.get(1).getType() 				 == Token.TYPE_LEFT_PARANTHESIS 
			&& tokens.get(tokens.size()-1).getType() == Token.TYPE_RIGHT_PARANTHESIS;
		
	}
	
	public int len(){
		return tokens.size();
	}
	
	public void addToken(Token t){
		tokens.add(t);
	}
	
	private static final String clean(String s){
		s = s.replaceAll("[^A-Za-z\\-:;,*_+'\\^$/!.=|&<>0-9()\\[\\]\\{\\}]", "");
		s = s.toLowerCase().replace(" ", "").replace(":=-", ":=0-");
		return s;
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
