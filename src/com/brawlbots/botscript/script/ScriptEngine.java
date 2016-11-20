package com.brawlbots.botscript.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.brawlbots.action.BotAction;
import com.brawlbots.botscript.expression.MathSysLibrary;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class ScriptEngine {

	private static final String botRoot = "/home/nignatieff/dank.business/bots/";
	
	private Map<String, Double>   variables, global_variables, permanent_variables;
	private Map<String, Script>   methods;
	private Map<String, String[]> method_arguments;
	
	private MathSysLibrary sysLib;
	
	public ScriptEngine(){
		variables        = new HashMap<String, Double>();
		global_variables = new HashMap<String, Double>();
		permanent_variables = new HashMap<String, Double>();
		methods          = new HashMap<String, Script>();
		method_arguments = new HashMap<String, String[]>();
		
		sysLib = new MathSysLibrary(MathSysLibrary.JAVA_MATH_LIBRARY);
				
		try {
			setVar(0,"'rotation",0);
			setVar(0,"'force",0);
			setVar(0,"'shoot",0);
		} catch (ScriptRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean hasLocalVar(String s){
		return variables.containsKey(s);
	}
	
	public boolean hasGlobalVar(String s){
		return global_variables.containsKey(s);
	}
	
	public void close() throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		
		for(Entry<String, Double> entry : permanent_variables.entrySet())
			sb.append(entry.getKey()).append('=').append(entry.getValue());
	
	}
	
	public MathSysLibrary getSystemLibrary(){
		return sysLib;
	}
	
	public HashMap<String, Double> loadPermanentVariables(){
		/*
		String line="no lines read yet";
		try{
			HashMap<String, Double> map = new HashMap<String, Double>();
			BufferedReader reader = new BufferedReader(new FileReader(new File(AutoBattler.botRoot + "permanent_vars.dat")));
		
			while ((line = reader.readLine()) != null) {
				if (line.contains("=")) {
					String[] strings = line.split("=");
					map.put(strings[0], Double.parseDouble(strings[1]));
				}
			}
			reader.close();
			return map;
		} catch(IOException o){
			System.out.println("error: couldn't load permanent variables");
			return new HashMap<String, Double>();
		} catch (NumberFormatException o){
			System.out.println("invalid value: "+line+"\nno permanent variables loaded");
			return new HashMap<String, Double>();
		}*/
		return null;
	}
	
	public HashMap<String, Double> getVars(){
		return new HashMap<String, Double>(variables);
	}
	
	public void setVars(Map<String, Double> vars){
		this.variables = vars;
	}
	
	public boolean hasArguments(String method){
		return method_arguments.containsKey(method);
	}
	
	public String[] getArguments(String method){
		return method_arguments.get(method);
	}
	
	//--VARIABLES
	public boolean hasVar(int hashId, String var){
		return variables.containsKey(var) || global_variables.containsKey(var) || permanent_variables.containsKey(getPermanentEquivalent(hashId,var));
	}
	
	public static String getPermanentEquivalent(int hashId, String var){
		return hashId + "__permanent-variable__"+var;
	}
	
	public double getVar(int hashId, String var) throws ScriptRuntimeException {
		if(!hasVar(hashId, var))
			throw new ScriptRuntimeException("The variable '"+var+"' is not defined at this point");
		
		//Prioritize local variables
		if(variables.containsKey(var)){
			return variables.get(var);
		}

		//Next up, global variables
		if(global_variables.containsKey(var))
			return global_variables.get(var);
		
		//And finally, the permanent variables
		return permanent_variables.get(getPermanentEquivalent(hashId, var));
	}
	
	public void setVar(int hashId, String var, String val) throws ScriptRuntimeException {
		try{
			setVar(hashId, var, Double.parseDouble(val));
		} catch(NumberFormatException e){
			throw new ScriptRuntimeException("Unable to parse value '"+val+"' as a number.");
		}
	}
	
	public void setVar(int hashId, String var, double val) throws ScriptRuntimeException{
		
		boolean global    = false,
				local     = false,
				permanent = false;
		
		String identifier = var;
		
		while(identifier.startsWith("'") || identifier.startsWith("^")|| identifier.startsWith("$")){
			if(identifier.startsWith("'")){
				global = !global;
				identifier = identifier.substring(1);
			}
			if(identifier.startsWith("^")){
				local = !local;
				identifier = identifier.substring(1);
			}
			if(identifier.startsWith("$")){
				permanent = !permanent;
				identifier = identifier.substring(1);
			}
		}
		
		if(!permanent && !global && !local){

			if(permanent_variables.containsKey(getPermanentEquivalent(hashId, identifier)))
				permanent_variables.put(getPermanentEquivalent(hashId, identifier), val);
			else{
			if(global_variables.containsKey(identifier))
					global_variables.put(identifier, val);
			else
				variables.put(identifier, val);}
				
			return;
				
		} else if (permanent && !global && !local){
			
			permanent_variables.put(identifier, val);

			return;
		}  else if (global && !local &&!permanent){
			
			global_variables.put(identifier, val);

			return;
		} else if (local && !global &&!permanent){

			variables.put(identifier, val);
			return;
		}
		throw new ScriptRuntimeException("invalid variable scope declaration");
	}
	
	//--ACTION
	public BotAction getAction() throws ScriptRuntimeException{
		BotAction action = new BotAction(0,0,false);
		
		if(getVar(0,"rotation")>0)
			action.setRotation(1);
		else
		if(getVar(0,"rotation")<0)
			action.setRotation(-1);

		if(getVar(0,"force")>0)
			action.setAcceleration(1);
		else
		if(getVar(0,"force")<0)
			action.setAcceleration(-1);
		
		if(getVar(0,"shoot")>=1)
			action.setShoot(true);
		
		return action;
	}
	
	//--METHODS
	public boolean hasMethod(String method){
		return methods.containsKey(method);
	}
	
	public Script getMethod(String method) throws ScriptRuntimeException {
		if(!hasMethod(method))
			throw new ScriptRuntimeException("No such method: '"+method+"'.");
		return methods.get(method);
	}
	
	public void setMethod(String method, Script script){
		String identifier = method;
		if(method.contains("(") && method.endsWith(")")){
			int first = method.indexOf("(");
			identifier = method.substring(0, first);
			String args = method.substring(first+1, method.length()-1);
			method_arguments.put(identifier, args.split(","));
		}
		
		methods.put(identifier, script);
	}
	
	private static final String getLibrary(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(botRoot+path)));
	}
	
	public void registerMethods(String scriptText, int hashId) throws TreeParseException, ScriptRuntimeException{
		scriptText = scriptText.replace("\r", "").replace("\n", "");
		//If the script requires imports, then include those first
		if(scriptText.toLowerCase().startsWith("#include ")){
			while(scriptText.toLowerCase().startsWith("#include ")){
				//Remove "include "
				scriptText = scriptText.substring("#include ".length());
			
				//Figure out where the line ends
				int del = scriptText.indexOf(";");
			
				//The path is from [0, del]
				String path = scriptText.substring(0, del);
			
				//If no extension is specified, assume it is .bot
				if(!path.contains("."))path+=".bot";
			
				//Remove entire line
				scriptText = scriptText.substring(del+1);
			
				//Load library & register it too
				try {
					String library = getLibrary(path);
					registerMethods(library, hashId);
				} catch (IOException e) {
					throw new TreeParseException("Unable to load library '"+path+"': "+e.getMessage());
				}
			
			}
			registerMethods(scriptText, hashId);
			return;
		}
		
		String[] methods = scriptText.replace(" ", "").toLowerCase().split("#method");
		for(String method : methods){
			if(!method.contains(":"))
				continue;
			
			String name = method.substring(0,method.indexOf(":")),
				   body_string = method.substring(method.indexOf(":")+1);
			Script body = Script.fromString(body_string, hashId);
			
			if(name.length()==0 || body_string.length()==0)
				continue;
			
			if(name.equals("on_import")){
				this.execute(body);
				continue;
			}
			
			setMethod(name, body);
		}
	}
	
	//--EXECUTE
	public void execute(Script s) throws ScriptRuntimeException{
		int i=1;
		for(ScriptFragment exp : s){
			i++;
			try {
				exp.doAction(this);
			} catch (ScriptRuntimeException e) {
				throw new ScriptRuntimeException("Error at line "+i+": "+e.getMessage());
			}
		}
	}
	
}
