package com.brawlbots.action;

public class BotAction {
	/**
	 * 
	 */
	private double ACCELERATION,
				   ROTATION;
	
	private boolean SHOOT, CHANGEABLE;
	
	/**
	 * Represents an action a Bot can perform at a given time-step.
	 * @param acceleration The acceleration (positive=accelerates, negative=decelerates)
	 * @param rotation The rotation (positive=counter-clockwise, negative=clockwise)
	 * @param shoot Whether or not this Bot shoots.
	 */
	public BotAction(double acceleration, double rotation, boolean shoot){
		this.ACCELERATION = acceleration;
		this.ROTATION = rotation;
		this.SHOOT = shoot;
		this.CHANGEABLE=true;
	}
	
	//Mutators
	/**
	 * Sets the acceleration of this BotAction-object (positive=accelerates, negative=decelerates)
	 * @param a 
	 */
	public void setAcceleration(double a){
		if(CHANGEABLE)
			ACCELERATION = a;
	}
	
	/**
	 * Sets the rotation of this BotAction-object (positive=counter-clockwise, negative=clockwise)
	 * @param rot
	 */
	public void setRotation(double rot){
		if(CHANGEABLE)
			ROTATION = rot;
	}
	
	/**
	 * Sets whether or not this BotAction shoots
	 * @param shoot
	 */
	public void setShoot(boolean shoot){
		if(CHANGEABLE)
			SHOOT = shoot;
	}
	
	/**
	 * Forces this object to be immutable. That is, no further changes can be made to the object after calling setImmutable();
	 */
	public void setImmutable(){
		CHANGEABLE=false;
	}
	
	/**
	 * Checks whether or not changes can still be made to this BotAction-object.
	 * @return
	 */
	public boolean isMutable(){
		return CHANGEABLE;
	}
	
	//ROTATION
	/**
	 * Checks whether or not this bot is turning right (i.e. clockwise, negative radians)
	 * @return
	 */
	public boolean isRotatingRight(){
		return ROTATION < 0;
	}

	/**
	 * Checks whether or not this bot is turning left (i.e. counter-clockwise, positive radians)
	 * @return
	 */
	public boolean isRotatingLeft(){
		return ROTATION > 0;
	}

	/**
	 * Checks whether or not this bot is rotating
	 * @return
	 */
	public boolean isNotRotating(){
		return ROTATION == 0;
	}
	
	//ACCELERATION
	/**
	 * Checks whether or not this bot is accelerating.
	 * @return
	 */
	public boolean isAccelerating(){
		return ACCELERATION > 0;
	}
	
	/**
	 * Checks whether or not this bot is decelerating,
	 * @return
	 */
	public boolean isDecelerating(){
		return ACCELERATION < 0;
	}

	/**
	 * Checks if this bot is accelerating at all (true = stagnant)
	 * @return
	 */
	public boolean noAccelerating(){
		return ACCELERATION == 0;
	}
	
	//SHOOT
	/**
	 * Checks whether or not this bot is currently shooting.
	 * @return
	 */
	public boolean isShooting(){
		return SHOOT;
	}
	
}
