package com.brawlbots.gui;

public final class Tile {

	private TileType type;
	private int rotation = 0; 
	private double r = Math.random()*2.5;
	
	public Tile(Tile t){
		this.type = t.getType();
		this.rotation = t.getRotation();
	}
	
	public Tile(TileType type){
		this.setType(type);
	}
	
	public Tile(TileType type, int rotation){
		this.setType(type);
		this.setRotation(rotation);
	}
	
	public double getVal(){
		return type.ordinal()+r;
	}
	
	public boolean satisfies(TileType t, int rot){
		return t==type && rot == rotation;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public TileType getType() {
		return type;
	}

	public void setType(TileType type) {
		this.type = type;
	}
	
}
