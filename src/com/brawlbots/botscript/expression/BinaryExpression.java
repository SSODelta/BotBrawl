package com.brawlbots.botscript.expression;

public abstract class BinaryExpression extends Expression {
	
	private Expression a,b;
	
	public BinaryExpression(Expression a, Expression b, int hashId){
		super(hashId);
		this.a=a;
		this.b=b;
	}
	
	protected Expression getFirst(){
		return a;
	}
	
	protected Expression getLast(){
		return b;
	}
}
