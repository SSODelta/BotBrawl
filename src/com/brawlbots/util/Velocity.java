package com.brawlbots.util;

public final class Velocity {

	private final double dx,dy;
	
	public Velocity(double dx, double dy){
		this.dx = dx;
		this.dy = dy;
	}
	
	public static Velocity fromAngular(double vel, double theta){
		return new Velocity(vel*Math.cos(theta),
							vel*Math.sin(theta));
	}
	
	public Velocity add(Velocity p){
		return new Velocity(this.dx+p.dx, this.dy+p.dy);
	}
	
	public Velocity sub(Velocity p){
		return new Velocity(this.dx-p.dx, this.dy-p.dy);
	}
	
	public Velocity divide(double d){
		return new Velocity(this.dx / d, this.dy / d);
	}

	public Velocity multiply(double d){
		return new Velocity(this.dx * d, this.dy * d);
	}
	
	public double getMagnitude(){
		return Math.hypot(dx, dy);
	}
	
	public double getDx(){
		return dx;
	}
	
	public double getDy(){
		return dy;
	}
	
	public Velocity rotateRad(double angle){
		double newx = dx * Math.cos(angle) - dy * Math.sin(angle);
		double newy = dx * Math.sin(angle - dy * Math.cos(angle)); 
		return new Velocity(newx, newy);
	}
	public Velocity rotateDeg(double angleRad){
		double angle = Math.toDegrees(angleRad);
		double newx = dx * Math.cos(angle) - dy * Math.sin(angle);
		double newy = dx * Math.sin(angle - dy * Math.cos(angle)); 
		return new Velocity(newx, newy);
	}
		
	public double getAngle(){
		return Math.atan2(dy, dx);
	}
	
}
