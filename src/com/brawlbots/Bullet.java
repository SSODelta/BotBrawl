package com.brawlbots;

import com.brawlbots.util.AngleMode;
import com.brawlbots.util.Position;
import com.brawlbots.util.Rotation;
import com.brawlbots.util.Velocity;

public class Bullet {
	private double x,y;
	private final double radius,dx,dy;
	private final int id, author;
	
	public Bullet(Position p, Velocity v, double radius, int id, int author){
		this.x = p.getX();
		this.y = p.getY();
		
		this.dx = v.getDx();
		this.dy = v.getDy();
		
		this.radius = radius;
		
		this.id = id;
		this.author = author;
	}
	
	public final int getID(){
		return id;
	}
	
	public final int getAuthor(){
		return author;
	}
	
	public final void update(){
		this.x += dx;
		this.y += dy;
	}
	
	public final double getRadius(){
		return radius;
	}
	
	public Position getPosition(){
		return new Position(x,y);
	}
	public Position getPrevPosition(){
		return new Position(x-dx,y-dy);
	}

	public Rotation getRotation() {
		return new Rotation(Math.atan2(dy, dx), AngleMode.RADIANS);
	}

	public Velocity getVelocity() {
		return new Velocity(dx,dy);
	}
}
