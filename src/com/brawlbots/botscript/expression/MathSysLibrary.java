package com.brawlbots.botscript.expression;

import java.util.ArrayList;
import java.util.List;

import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;

public class MathSysLibrary {

	private List<LibraryMethod> ms;
	
	public MathSysLibrary(LibraryMethod[] methods){
		ms = new ArrayList<LibraryMethod>();
		for(LibraryMethod m : methods)
			ms.add(m);
		
	}
	
	public boolean hasMethod(String s){
		for(LibraryMethod lm : ms)
			if(lm.is(s))return true;
		return false;
	}
	
	public double evaluate(String method, double[] args) throws ScriptRuntimeException{
		for(LibraryMethod lm : ms)
			if(lm.is(method))return lm.eval(args);
		throw new ScriptRuntimeException("no such library method: "+method);
	}
	
	public static final LibraryMethod[] JAVA_MATH_LIBRARY
		= new LibraryMethod[]{
			new LibraryMethod("",       1, (x)->x[0]),
			new LibraryMethod("abs",    1, (x)->Math.abs(x[0])),
			new LibraryMethod("acos",   1, (x)->Math.acos(x[0])),
			new LibraryMethod("atan",   1, (x)->Math.atan(x[0])),
			new LibraryMethod("atan2",  2, (x)->Math.atan2(x[0], x[1])),
			new LibraryMethod("cbrt",   1, (x)->Math.cbrt(x[0])),
			new LibraryMethod("ceil",   1, (x)->Math.ceil(x[0])),
			new LibraryMethod("cos",    1, (x)->Math.cos(x[0])),
			new LibraryMethod("cosh",   1, (x)->Math.cosh(x[0])),
			new LibraryMethod("deg2rad",1, (x)->Math.toRadians(x[0])),
			new LibraryMethod("exp",    1, (x)->Math.exp(x[0])),
			new LibraryMethod("floor",  1, (x)->Math.floor(x[0])),
			new LibraryMethod("hypot",  2, (x)->Math.hypot(x[0], x[1])),
			new LibraryMethod("log",    1, (x)->Math.log(x[0])),
			new LibraryMethod("log10",  1, (x)->Math.log10(x[0])),
			new LibraryMethod("max",    2, (x)->Math.max(x[0], x[1])),
			new LibraryMethod("min",    2, (x)->Math.min(x[0], x[1])),
			new LibraryMethod("pow",    2, (x)->Math.pow(x[0], x[1])),
			new LibraryMethod("print",  1, (x)->{System.out.println(x[0]);return x[0];}),
			new LibraryMethod("rad2deg",1, (x)->Math.toDegrees(x[0])),
			new LibraryMethod("random", 0, (x)->Math.random()),
			new LibraryMethod("random2",0, (x)->Math.random()*2-1),
			new LibraryMethod("round",  1, (x)->Math.round(x[0])),
			new LibraryMethod("signum", 1, (x)->Math.signum(x[0])),
			new LibraryMethod("sin",    1, (x)->Math.sin(x[0])),
			new LibraryMethod("sinh",   1, (x)->Math.sinh(x[0])),
			new LibraryMethod("sqrt",   1, (x)->Math.sqrt(x[0])),
			new LibraryMethod("tan",    1, (x)->Math.tan(x[0])),
			new LibraryMethod("tanh",   1, (x)->Math.tanh(x[0]))
	};
	
}
