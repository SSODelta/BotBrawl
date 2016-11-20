package com.brawlbots.util;

public class BotSpawn {

	private final double x,y, theta, radius;
	private final int health, ammo;
	
	public BotSpawn(double x, double y, double theta, double radius, int health, int ammo){
		this.x=x;
		this.y=y;
		this.theta=theta;
		this.radius=radius;
		this.health=health;
		this.ammo=ammo;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getTheta(){
		return theta;
	}
	public double getRadius(){
		return radius;
	}
	
	public int getHealth(){
		return health;
	}
	public int getAmmo(){
		return ammo;
	}
	
}
