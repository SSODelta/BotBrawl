package com.brawlbots.botscript.tokentrees;

import java.util.ArrayList;
import java.util.List;

public final class Composite extends Node{

	private List<Node> children;
	
	public Composite(TokenList data, List<Node> children) {
		super(data);
		this.children = children;
	}
	
	public Composite(TokenList data){
		this(data, new ArrayList<Node>());
	}
	
	public void addChild(Node n){
		children.add(n);
	}
	
	public List<Node> getChildren(){
		return children;
	}

	@Override
	public void print(String pre) {
		System.out.println(pre+"Composite, "+getData().toString());
		for(Node n : children)
			n.print(pre+"\t");
	}
	


}
