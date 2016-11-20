package com.brawlbots;

public class Dust {

	private static final double DUST_SIZE = 10;
	public  static final int    DUST_LIFE = 1200;
	
	
	public int t;
	public final double x,y,r;
	
	public Dust(double x, double y){
		this.x=x;
		this.y=y;
		this.t=DUST_LIFE+(int)(rand()/10*DUST_LIFE);
		this.r=DUST_SIZE+rand()/10*DUST_SIZE;
	}
	
	private static final double rand(){
		return Math.random()*2-1;
	}
	
}
