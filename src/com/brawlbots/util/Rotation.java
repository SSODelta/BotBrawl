package com.brawlbots.util;

public final class Rotation {
	private final double theta;
	
	public Rotation(double theta){
		this(theta, AngleMode.RADIANS);
	}
	
	public Rotation(double rotation, AngleMode mode){
		if(mode == AngleMode.DEGREE){
			this.theta = Math.toRadians(rotation);
		} else if (mode == AngleMode.RADIANS){
			this.theta = rotation;
		} else /* Is never thrown */ {
			theta = 0;
		}
	}
	
	public double getRadians(){
		return theta;
	}
	
	public double getDegrees(){
		return Math.toDegrees(theta);
	}
}
