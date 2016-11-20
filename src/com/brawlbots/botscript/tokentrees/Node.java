package com.brawlbots.botscript.tokentrees;

public abstract class Node {

	private TokenList data;
	
	protected Node(TokenList data){
		this.data = data;
	}
	
	protected Node(String data){
		this(new TokenList(data));
	}
	
	public TokenList getData(){
		return data;
	}
	
	public abstract void print(String pre);
	
	private static final Composite getRoot(){
		return new Composite(new TokenList("root"));
	}
	
	public static final Composite parseToken(TokenList tl) throws TreeParseException{
		return parseTokens(getRoot(), tl);
	}
	
	public static final Composite parseTokens(Composite root, TokenList tl) throws TreeParseException{
		TokenList queue = new TokenList();
		int depth = 0;

		Composite newRoot = null;
		
		for(Token token : tl){
			
			if(depth == 0){
				if(token.getType() == Token.TYPE_LINE_BREAK){
					root.addChild(new Leaf(queue));
					queue = new TokenList();
				}else if(token.getType() == Token.TYPE_LEFT_METHOD_BLOCK){
					newRoot = new Composite(queue);
					root.addChild(newRoot);
					queue = new TokenList();
					depth++;
				}else if(token.getType() == Token.TYPE_RIGHT_METHOD_BLOCK){
					throw new TreeParseException("Error: Did not expect end-method token here.");
				}else queue.addToken(token);
				
			} else {
				if(token.getType() == Token.TYPE_LEFT_METHOD_BLOCK)
					depth++;
				if(token.getType() == Token.TYPE_RIGHT_METHOD_BLOCK)
					depth--;
				
				if(depth==0){
					parseTokens(newRoot, queue);
					queue = new TokenList();
				}else {
					queue.addToken(token);
				}
			}
		}
		
		if(depth>0)
			throw new TreeParseException("Mismatched method-block delimiters");
		
		if(queue.len()>0)
			root.addChild(new Leaf(queue));
		
		return root;
	}
	
}
