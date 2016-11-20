package com.brawlbots.botscript.conditions;

interface ComparisonType {

	public boolean compare(double var, double val);
	
	public static final ComparisonType EQUALS            = (a,b) -> a==b,
									   LESS_THAN         = (a,b) -> a<b,
									   GREATER_THAN      = (a,b) -> a>b,
									   LESS_OR_EQUALS    = (a,b) -> a<=b,
									   GREATER_OR_EQUALS = (a,b) -> a>=b,
									   NOT_EQUALS        = (a,b) -> a!=b,
									   ALMOST_EQUALS     = (a,b) -> Math.abs(a-b) < 0.05;
}
