package com.brawlbots;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brawlbots.action.BotAction;
//import com.brawlbots.lorekeeper.ScrollOfTraditions;
import com.brawlbots.util.BotSpawn;
import com.brawlbots.util.Misc;
import com.brawlbots.util.Position;
import com.brawlbots.util.Rotation;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.Velocity;
import com.brawlbots.util.ViewType;

/**
 * This class represents a Bot to play a game of BrawlBots. To create a Bot
 * which is actually capable of playing the game, extend this class. To see some
 * examples, see com.brawlbots.bots.SimpleBot and the likes to get an idea of
 * how the implementation works.
 * 
 * Basically, you need to implement the method body to the method updateBot().
 * It needs to return a BotAction-Double which represents some action a bot can
 * perform, which includes:
 * 
 * x Should it accelerate, decelerate? x Should it rotate - if so, which
 * direction? x Should the bot be shooting?
 * 
 * This class also introduces some auxillary helping methods to aid
 * Bot-developrs, which includes but is not limited to:
 * 
 * x getPosition() x getRotation() x ...
 * 
 * @author nikol
 *
 */
public abstract class Bot {
	/*
	//For recording purposes
	int lastDirection = 0;
	int thisDirection = 0;
	static final int straight = 0;
	static final int right = 1;
	static final int left = 2;*/
	
	
	//Not for recording purposes
	private double x, y, vel, theta, radius;

	private int health, ammo, id, reload, bulletsFired, bulletHits, ammoCollects, ammoSeen;

	private Arena arena;	

	private final Map<String, Double> vars;

	private String name;

	private boolean view_loaded;

	private List<Sighting> lastView;

	/**
	 * Instantiates a bot
	 * 
	 * @param name
	 *            The display name for the bot
	 */
	public Bot(String name) {
		this.x = 0;
		this.y = 0;
		this.ammoSeen=0;
		this.setAmmoCollects(0);
		this.setBulletHits(0);
		this.setBulletsFired(0);
		this.vel = 0;
		this.theta = 0;
		this.radius = 0;
		this.arena = null;
		this.id = -1;
		this.reload = 0;
		
		this.health = 0;
		this.name = name;
		view_loaded = false;
		lastView = null;

		vars = new HashMap<String, Double>();
	}
	
	protected final void setName(String name){
		this.name = name;
	}

	/**
	 * Checks if this bot contains an entry for the variable 'var'
	 * 
	 * @param var
	 * @return
	 */
	public final boolean has(String var) {
		return vars.containsKey(var);
	}

	/**
	 * Returns the Double associated with the variable 'var'
	 * 
	 * @param var
	 * @return
	 */
	public final Double get(String var) {
		return vars.get(var);
	}

	/**
	 * Sets the Double 'val' associated with the variable 'var'
	 * 
	 * @param var
	 * @param val
	 */
	public final void set(String var, double val) {
		vars.put(var, val);
	}

	/**
	 * Links this bot to an arena, which basically consists of spawning the
	 * spawn.
	 * 
	 * @param a
	 *            The Arena
	 */
	public final void linkArena(Arena a) {
		this.vel = 0;
		this.reload = 0;
		this.arena = a;
		BotSpawn info = arena.nextBot();
		this.x = info.getX();
		this.y = info.getY();
		this.health = info.getHealth();
		this.ammo = info.getAmmo();
		this.theta = info.getTheta();
		this.radius = info.getRadius();
	}

	/**
	 * Respawns the bot within the arena. Will throw NullPointerException if
	 * arena is not linked.
	 */
	public final void reset() {
		BotSpawn info = arena.nextBot();
		this.x = info.getX();
		this.y = info.getY();
		this.health = info.getHealth();
		this.ammo = info.getAmmo();
		this.theta = info.getTheta();
		this.radius = info.getRadius();
	}

	/**
	 * This command is meant to be an update to the bot. No changes can be made
	 * directly to the local variables, instead a BotAction Double must be
	 * returned. The Bot-framework will handle moving the bots (changing private
	 * field variables).
	 * 
	 * @return
	 */
	public abstract BotAction updateBot();

	/**
	 * This command is called whenever the bot is hurt. This may be relevant if
	 * the strategy changes after the bot has lost some health.
	 */
	public abstract void hurt();

	/**
	 * This command is called whenever the bot picks up some ammo.
	 */
	public abstract void ammo(int id);
	
	/**
	 * This command is called whenenver the bot is terminated from a game
	 */
	public abstract void battleEnd();

	/**
	 * Returns the display name of this bot
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the width (in pixels) of the arena this bot is within. Will throw
	 * NullPointerException if arena is not linked.
	 * 
	 * @return
	 */
	public final double getArenaWidth() {
		return arena.getWidth();
	}

	/**
	 * Returns the height (in pixels) of the arena this bot is within. Will
	 * throw NullPointerException if arena is not linked.
	 * 
	 * @return
	 */
	public final double getArenaHeight() {
		return arena.getHeight();
	}

	/**
	 * Damages this bot (ie. when a bullet hits).
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final boolean damageBot(int arenaKey) {
		if (arena.confirmKey(arenaKey)) {
			health--;
			hurt();
			return true;
		}
		return false;
	}

	/**
	 * Returns the (unique) ID of this bot
	 * 
	 * @return
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Sets the ID of this bot
	 * 
	 * @param id
	 */
	public final void setID(int id) {
		if (id > 0)
			this.id = id;
	}

	/**
	 * Gets the size of the view-port (in radians) from either side of the
	 * direction the bot is pointing.
	 * 
	 * @return
	 */
	public final double getViewAngle() {
		return arena.getViewAngle();
	}

	/**
	 * Gets the total reload time (in frames) for this bot.
	 * 
	 * @return
	 */
	public final double getTotalReloadTime() {
		return arena.getReload();
	}

	/**
	 * Gets the radius of this bot (usually 16)
	 * 
	 * @return
	 */
	public final double getRadius() {
		return radius;
	}

	/**
	 * Grants this bot an extra bullet (ie. when picking up ammo).
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final boolean findAmmo(int arenaKey) {
		if (arena.confirmKey(arenaKey)) {
			this.ammo++;
			this.ammoCollects++;
			return true;
		}
		return false;
	}

	/**
	 * Sets the velocity of this bot.
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final boolean setVelocity(double vel, int arenaKey) {
		if (arena.confirmKey(arenaKey)) {
			this.vel = vel;
			return true;
		}
		return false;
	}

	/**
	 * Sets the y-coordinate of this bot.
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final boolean setY(double y, int arenaKey) {
		if (arena.confirmKey(arenaKey)) {
			this.y = y;
			return true;
		}
		return false;
	}

	/**
	 * Sets the x-coordinate of this bot.
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final boolean setX(double x, int arenaKey) {
		if (arena.confirmKey(arenaKey)) {
			this.x = x;
			return true;
		}
		return false;
	}

	/**
	 * Checks if this bot collides with a bullet.
	 * 
	 * @param b
	 * @return
	 */
	public final boolean collides(Bullet b) {
		Position p = b.getPosition(), q = b.getPrevPosition();
		double dist = Misc.pointDistance(x+radius, y+radius, p.getX(), p.getY(), q.getX(), q.getY());

		return dist <= (radius + b.getRadius()) / 2;
	}

	/**
	 * checks if this bot pollides with a box of ammunition.
	 * 
	 * @param a
	 * @return
	 */
	public final boolean collides(Ammo a) {
		Position p = a.getPosition();
		double dist = Misc.pointDistance(p.getX(), p.getY(), x+radius, y+radius, x + getDx(), y + getDy());
		return dist <= (radius + a.getRadius())/2;
	}

	/**
	 * Updates this bot.
	 * 
	 * Note: This method will *not* do anything if arenaKey is not correct
	 * (which is a private inaccessible field variable within Arena)
	 * 
	 * @param arenaKey
	 *            The secret key of the arena (no cheating).
	 * @return
	 */
	public final void update(int arenaKey) {
		if(!arena.confirmKey(arenaKey))
			return;
		
		view_loaded = false;
		reload--;

		BotAction action = updateBot();

		if (action != null) {
			if (action.isAccelerating())
				vel += arena.getAcceleration();
			else if (action.isDecelerating()){
				vel -= arena.getDeceleration();
				if(vel<=0)vel=0;
			}

			if (!action.isNotRotating()) {
				if (action.isRotatingLeft()){
					theta += arena.getRotationSpeed();
					//thisDirection = left;
				}
				else if (action.isRotatingRight()){
					theta -= arena.getRotationSpeed();
					//thisDirection = right;
				}
			}/*
			else{
				thisDirection = straight;
			}*/
			if (theta < 0)
				theta += 2 * Math.PI;

			if (theta > 2 * Math.PI)
				theta %= 2 * Math.PI;

			if (action.isShooting() && arena.canShoot() && ammo > 0 && reload <= 0) {
				ammo--;
				this.reload = arena.getReload();
				arena.shootFrom(getCenter(), getRotation(), getVelocity(), id);
			}
		}
		this.x += getDx();
		this.y += getDy();
		/*if(thisDirection != lastDirection){
			if(thisDirection == straight) ScrollOfTraditions.recordStraightTurn(arena.masterScribe, id);
			if(thisDirection == right) ScrollOfTraditions.recordRightTurn(arena.masterScribe, id);
			if(thisDirection == left) ScrollOfTraditions.recordLeftTurn(arena.masterScribe, id);
			lastDirection = thisDirection;
		}*/
		
	}
	
	public final double getArenaMargin(){
		return arena.getArenaMargin();
	}

	/**
	 * Gets a list of all the things this Bot can currently see within its
	 * viewport.
	 * 
	 * @return A list of Sighting Doubles
	 */
	public final List<Sighting> view() {
		if (!view_loaded)
			lastView = arena.viewFrom(getCenter(), getRotation(), id);
		view_loaded = true;
		return lastView;
	}

	/**
	 * Gets the health of this bot.
	 * 
	 * @return
	 */
	public final int getHealth() {
		return health;
	}

	/**
	 * Gets the remaining ammunition this bot is carrying.
	 * 
	 * @return
	 */
	public final int getAmmo() {
		return ammo;
	}

	/**
	 * Gets the position of this bot (top-left-corner).
	 * 
	 * @return
	 */
	public final Position getPosition() {
		return new Position(x, y);
	}

	/**
	 * Gets the position of the center of this center
	 * 
	 * @return
	 */
	public final Position getCenter() {
		return new Position(x + radius, y + radius);
	}

	/**
	 * Gets the change in x per time-step for this bot.
	 * 
	 * @return
	 */
	private final double getDx() {
		return Math.cos(theta) * vel;
	}

	/**
	 * Gets the change in y per time-step for this bot.
	 * 
	 * @return
	 */
	private final double getDy() {
		return Math.sin(theta) * vel;
	}

	/**
	 * Gets the velocity of this bot.
	 * 
	 * @return
	 */
	public final Velocity getVelocity() {
		return new Velocity(getDx(), getDy());
	}

	/**
	 * Gets the rotation of this bot.
	 * 
	 * @return
	 */
	public final Rotation getRotation() {
		return new Rotation(theta);
	}

	@Override
	public String toString() {
		return name + " (id=" + id + "):\n\tpos: (x: " + Misc.form(x) + ", y:" + Misc.form(y) + ")\n\tvel: " + Misc.form(vel)
				+ " with theta=" + Misc.form(theta) + " (deg=" + Misc.form(Math.toDegrees(theta)) + ")\n\thealth: " + health
				+ "\n\tammo: " + ammo;
	}

	public int getBulletsFired() {
		return bulletsFired;
	}

	public void setBulletsFired(int bulletsFired) {
		this.bulletsFired = bulletsFired;
	}

	public int getBulletHits() {
		return bulletHits;
	}

	public void setBulletHits(int bulletHits) {
		this.bulletHits = bulletHits;
	}

	public int getAmmoCollects() {
		return ammoCollects;
	}

	public void setAmmoCollects(int ammoCollects) {
		this.ammoCollects = ammoCollects;
	}

	public int getAmmoSeen() {
		return ammoSeen;
	}

	public void addAmmoSeen() {
		this.ammoSeen++;
	}

}
