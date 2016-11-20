package com.brawlbots.util;

public final class Sighting {
	private final double distance;
	private final Rotation angle;
	private final Velocity vel;
	private final ViewType type;
	private final int id;
	private final Position pos;
	
	public Sighting(ViewType type, double distance, Rotation angle, Position pos, Velocity v, int id){
		this.type = type;
		this.distance = distance;
		this.angle = angle;
		this.id = id;
		this.vel = v;
		this.pos=pos;
	}
	
	public final double getDistance(){
		return distance;
	}
	
	public final Rotation getAngle(){
		return angle;
	}
	
	public final int getID(){
		return id;
	}
	
	public final ViewType getType(){
		return type;
	}
	
	public final Position getAbsolutePosition(){
		return pos;
	}
	
	public final Velocity getVelocity(){
		return vel;
	}
}
