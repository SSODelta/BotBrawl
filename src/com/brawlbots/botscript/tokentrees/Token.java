package com.brawlbots.botscript.tokentrees;

public final class Token {

	public final static int  TYPE_TEXT                = 2048,
							 TYPE_LEFT_PARANTHESIS    = 2049,
							 TYPE_RIGHT_PARANTHESIS   = 2050,
							 TYPE_LEFT_METHOD_BLOCK   = 2051,
							 TYPE_RIGHT_METHOD_BLOCK  = 2052,
							 TYPE_LINE_BREAK          = 2053,
							 TYPE_CONDITION_AND       = 2054,
							 TYPE_CONDITION_OR        = 2055,
							 TYPE_EXPRESSION_ADD_SUB  = 12000,
							 TYPE_EXPRESSION_MULT_DIV = 12001,
							 TYPE_FUNCTION_CALL       = 18035; 
	
	private String data;
	private int type;
	
	public Token(String data){
		this.data=data;
		this.type=TYPE_TEXT;
		
		if(data.equals("("))
			this.type=TYPE_LEFT_PARANTHESIS;
		if(data.equals(")"))
			this.type=TYPE_RIGHT_PARANTHESIS;
		if(data.equals("{"))
			this.type=TYPE_LEFT_METHOD_BLOCK;
		if(data.equals("}"))
			this.type=TYPE_RIGHT_METHOD_BLOCK;
		if(data.equals(";"))
			this.type=TYPE_LINE_BREAK;
		if(data.equals("||"))
			this.type=TYPE_CONDITION_OR;
		if(data.equals("&&"))
			this.type=TYPE_CONDITION_AND;
		if(data.equals("+"))
			this.type=TYPE_EXPRESSION_ADD_SUB;
		if(data.equals("-"))
			this.type=TYPE_EXPRESSION_ADD_SUB;
		if(data.equals("*"))
			this.type=TYPE_EXPRESSION_MULT_DIV;
		if(data.equals("/"))
			this.type=TYPE_EXPRESSION_MULT_DIV;
		
		if(data.contains("(") && data.endsWith(")"))
			this.type=TYPE_FUNCTION_CALL;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
