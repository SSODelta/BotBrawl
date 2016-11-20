package com.brawlbots.botscript.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.brawlbots.botscript.expression.Expression;
import com.brawlbots.botscript.expression.Variable;
import com.brawlbots.botscript.tokentrees.Composite;
import com.brawlbots.botscript.tokentrees.Leaf;
import com.brawlbots.botscript.tokentrees.Node;
import com.brawlbots.botscript.tokentrees.TokenList;
import com.brawlbots.botscript.tokentrees.TreeParseException;

public final class Script implements Iterable<ScriptFragment> {

	private List<ScriptFragment> exps;
	private int hashId;

	
	private Script(int hashId){
		exps = new ArrayList<ScriptFragment>();
	}
	
	public int getHashID(){
		return hashId;
	}
	
	private Script(int hashId, Collection<ScriptFragment> exps){
		this.exps.addAll(exps);
	}
	
	public void addScriptFragment(ScriptFragment e){
		exps.add(e);
	}
	
	
	public static final Script fromString(String scriptString, int hashId) throws TreeParseException {
		TokenList tl = new TokenList(scriptString);
		Collection<Node> nodes = Node.parseToken(tl).getChildren();
		return fromNodes(hashId, nodes);
	}
	
	private static If lastIf=null;
	private static final Script fromNodes(int hashId, Collection<Node> exps) throws TreeParseException {
		Script script = new Script(hashId);
		for(Node n : exps){
			ScriptFragment frag = parseNode(hashId, n);
			
			if(frag!=null){
				if(frag instanceof If)
					lastIf = (If) frag;
				else
					lastIf = null;
				
				script.addScriptFragment(frag);
			}
		}
		return script;
	}
	
	private static final ScriptFragment parseNode(int hashId, Node n) throws TreeParseException{
		if(n==null)return null;
		if(n instanceof Leaf){
			StringBuilder exp = new StringBuilder();;
			if(n.getData().isAssignment()){
				for(int i=2; i<n.getData().len(); i++)
					exp.append(n.getData().getDataAt(i));
				return new Assignment(	Variable.varFromString(n.getData().getDataAt(0), hashId), 
										exp.toString(),
										hashId);
			}

			for(int i=0; i<n.getData().len(); i++)
				exp.append(n.getData().getDataAt(i));
			return new Evaluation(Expression.fromString(exp.toString(), hashId), hashId);
			
		} else if (n instanceof Composite){
			Composite c = (Composite) n;
			
			if(c.getData().isConditional()){
				
				if(c.getData().getDataAt(0).equals("if"))
					return new If(c.getData().subList(2, c.getData().len()-1), fromNodes(hashId, c.getChildren()), hashId);
				
				if(c.getData().getDataAt(0).equals("while"))
					return new While(c.getData().subList(2, c.getData().len()-1), fromNodes(hashId, c.getChildren()), hashId);
			} else if(c.getData().getDataAt(0).equals("else") && lastIf!=null){
				List<Node> nodes = c.getChildren();
				If i = lastIf;
				Script s = Script.fromNodes(hashId, nodes);
				i.addElse(s);
				return null;
			}
		}
		
		throw new TreeParseException("Unable to interpret the node "+n.getData().toString());
	}
	
	public void print(){
		for(ScriptFragment exp : this)
			exp.print();
	}

	@Override
	public Iterator<ScriptFragment> iterator() {
		return exps.iterator();
	}

}
