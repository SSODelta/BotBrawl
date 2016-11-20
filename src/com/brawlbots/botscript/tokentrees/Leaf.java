package com.brawlbots.botscript.tokentrees;

public final class Leaf extends Node {

	public Leaf(TokenList tl){
		super(tl);
	}

	@Override
	public void print(String pre) {
		System.out.println(pre+"Leaf, "+getData().toString());
	}

}
