package com.brawlbots.util;

public final class Position {

	private final double x,y;
	
	public Position(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double distanceTo(Position p){
		return Math.hypot(p.x-x, p.y-y);
	}
	
	public Position add(Position p){
		return new Position(this.x+p.x, this.y+p.y);
	}
	
	public Position sub(Position p){
		return new Position(this.x-p.x, this.y-p.y);
	}
	
	public double atan(){
		return Math.atan2(y, x);
	}
	
	public Position scale(double s){
		return new Position(this.x*s, this.y*s);
	}
	
}
