package com.brawlbots;

import com.brawlbots.util.Position;

/**
 * Class representing an ammo container
 * @author nikol
 *
 */
public final class Ammo {

	private final double x, y;
	private final int id;
	
	/**
	 * Construct a new ammo container
	 * @param x x-coordinate in the arena
	 * @param y y-coordinate in the arena
	 * @param id the (unique) id of this ammo container
	 */
	public Ammo(double x, double y, int id){
		this.x=x;
		this.y=y;
		this.id = id;
	}
	
	/**
	 * Returns the absolute position of this ammo container
	 * @return
	 */
	public Position getPosition(){
		return new Position(x-16,y-16);
	}
	
	/**
	 * Returns the radius of this ammo container
	 * @return
	 */
	public double getRadius(){
		return 16.0;
	}
	
	/**
	 * Returns the unique ID of this ammo container
	 * @return
	 */
	public int getID(){
		return id;
	}
	
}
