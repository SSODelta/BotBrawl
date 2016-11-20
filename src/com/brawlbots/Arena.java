package com.brawlbots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.brawlbots.bots.PlayerControlledBot;
//import com.brawlbots.lorekeeper.Scribe;
//import com.brawlbots.lorekeeper.ScrollOfTraditions;
import com.brawlbots.util.BotSpawn;
import com.brawlbots.util.Position;
import com.brawlbots.util.Rotation;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.Velocity;
import com.brawlbots.util.ViewType;

/**
 * Represents an Arena in which bots can battle each other.
 * @author nikol
 *
 */
public class Arena {

	/**
	 * not in use
	 */
	//public Scribe masterScribe;
	
	private Set<Bot> bots, backup;
	private Set<Bullet> bullets;
	private Set<Ammo> ammos;
	private Set<Dust> dusts;
	
	private final double width, height;
	
	private final int arenaKey = (int)(Math.random()*Integer.MAX_VALUE);
	
	private static int globalNextBotID    = (int)(Math.random()*Integer.MAX_VALUE),
					   globalNextBulletID = (int)(Math.random()*Integer.MAX_VALUE),
					   globalNextAmmoID   = (int)(Math.random()*Integer.MAX_VALUE);
	
	private final double VIEW_ANGLE = 20,
						 ACCELERATION = 0.1,
						 DECELERATION = 0.025,
						 ROTATION_SPEED = Math.toRadians(1.4),
						 MAX_SPEED = 2.5,
						 BULLET_RADIUS = 2,
						 BULLET_SPEED = 5,
						 BULLET_SPREAD = Math.toRadians(5);
	
	private double BOT_RADIUS;
	private int t_fire;
	
	private PlayerControlledBot pcb;
	
	private final int RELOAD_TIME = 30,
					  MARGIN = 50,
					  AMMO_SPAWN_PROMILLE = 2,
					  AMMO_MAX = 25,
					  NO_FIRE_TIMER = 400,
					  STARTING_BULLETS = 1;
	
	private boolean ongoing = true, hasPlayer = false;
	
	public Map<Integer, Bot> bot_map;
	public Map<String, Integer> scores;
	public Map<Integer, Integer> kills;
	
	/*
	/**
	 * Construct a new Arena object
	 * @param radius The radius of the bots in the Arena
	 * @param width The width of the Arena 
	 * @param height The height of the Arena
	 * @param scribe
	 
	public Arena(double radius, double width, double height, Scribe scribe){
		this(radius, width, height);
		this.masterScribe = scribe;
	}*/
	
	/**
	 * Construct a new Arena object
	 * @param radius The radius of the bots in the Arena
	 * @param width The width of the Arena 
	 * @param height The height of the Arena
	 * @param scribe
	 */
	public Arena(double radius, double width, double height){
		
		this.bots = new HashSet<Bot>();
		this.backup = new HashSet<Bot>();
		this.bullets = new HashSet<Bullet>();
		this.ammos = new HashSet<Ammo>();
		this.dusts = new HashSet<Dust>();
		
		this.scores = new HashMap<String, Integer>();
		this.kills  = new HashMap<Integer, Integer>();
		this.bot_map = new HashMap<Integer, Bot>();
		
		this.width  = width;
		this.height = height;
		this.BOT_RADIUS = radius;
		this.pcb = null;
		this.t_fire = NO_FIRE_TIMER;
	}
	
	/**
	 * Resets this arena
	 */
	public void reset(){
		globalNextBulletID = (int)(Math.random()*Integer.MAX_VALUE);
		this.bots = new HashSet<Bot>();
		for(Bot b : backup)
			addBot(b);
		this.bullets = new HashSet<Bullet>();
		this.ammos = new HashSet<Ammo>();
		ongoing=true;
		t_fire = NO_FIRE_TIMER;
	}
	
	private void spawnPlayer(){
		if(hasPlayer)return;
		pcb = new PlayerControlledBot();
		addBot(pcb);
		hasPlayer=true;
	}
	
	public void setLeft(boolean val){
		spawnPlayer();
		pcb.setLEFT(val);
	}
	public void setRight(boolean val){
		spawnPlayer();
		pcb.setRIGHT(val);
	}
	public void setUp(boolean val){
		spawnPlayer();
		pcb.setUP(val);
	}
	public void setDown(boolean val){
		spawnPlayer();
		pcb.setDOWN(val);
	}
	public void setShoot(boolean val){
		spawnPlayer();
		pcb.setSHOOT(val);
	}
	
	/**
	 * Returns the set of all bots linked to the Arena
	 * @return
	 */
	public Set<Bot> getBots(){
		return new HashSet<Bot>(bots);
	}

	/**
	 * Returns the set of all bullets in the Arena
	 * @return
	 */
	public Set<Bullet> getBullets(){
		return new HashSet<Bullet>(bullets);
	}
	
	/**
	 * Returns the set of all ammo containers in the Arena
	 * @return
	 */
	public Set<Ammo> getAmmo(){
		return new HashSet<Ammo>(ammos);
	}
	
	/**
	 * Adds a bot to the Arena
	 * @param b Some bot
	 */
	public void addBot(Bot b){
		if(b.getID()==-1){
			b.setID(globalNextBotID);
			bot_map.put(globalNextBotID++, b);}else
				bot_map.put(b.getID(), b);
		b.linkArena(this);
		this.bots.add(b);
		this.backup.add(b);
		//if(masterScribe != null) ScrollOfTraditions.recordCombatant(masterScribe, b);
	}
	
	/**
	 * Get the reload time of the bots in Arena
	 * @return
	 */
	public final int getReload(){
		return RELOAD_TIME;
	}
	
	/**
	 * Gets the inner margin of the Arena
	 * @return
	 */
	public final double getArenaMargin(){
		return MARGIN;
	}
	
	/**
	 * Gets the acceleration of bots in length units / frame^2
	 * @return
	 */
	public final double getAcceleration(){
		return ACCELERATION;
	}
	
	/**
	 * Gets the deceleration of bots in length units / frame^2
	 * @return
	 */
	public final double getDeceleration(){
		return DECELERATION;
	}
	
	/**
	 * Gets the rotation speed in radians / frame of the bots in this Arena
	 * @return
	 */
	public final double getRotationSpeed(){
		return ROTATION_SPEED;
	}
	
	/**
	 * Returns a new Spawn configuration for a bot
	 * @return
	 */
	public BotSpawn nextBot(){
		return new BotSpawn(Math.random()*width, Math.random()*height, Math.random()*2*Math.PI, BOT_RADIUS, 1, STARTING_BULLETS);
	}
	
	private void spawnAmmo(){
		if(ammos.size()>=AMMO_MAX)return;
		for(Bot b:bots)
			b.addAmmoSeen();
		Ammo ammo = new Ammo(MARGIN+BOT_RADIUS+Math.random()*(width-(MARGIN+BOT_RADIUS)*2), MARGIN+BOT_RADIUS+Math.random()*(height-(MARGIN+BOT_RADIUS)*2), globalNextAmmoID++);
		ammos.add(ammo);
		//if(masterScribe != null) ScrollOfTraditions.spawnAmmo(masterScribe, (int)ammo.getPosition().getX(), (int)ammo.getPosition().getX());
	}
	
	/**
	 * Returns true if some number of frames has passed (bots can't instakill)
	 * @return
	 */
	public boolean canShoot(){
		return t_fire==0;
	}
	
	/**
	 * Advances the progress of this Arena by one frame.
	 */
	public void update(){
		//if(masterScribe != null) ScrollOfTraditions.markTheEndingOfAnEra(masterScribe);
		if(t_fire>0)t_fire--;
		
		//---DUST
		Set<Dust> survivingDust = new HashSet<Dust>();
		
		for(Dust d : dusts)
			if(--d.t>0)
				survivingDust.add(d);
			
		
		dusts = survivingDust;
		
		//---SPAWN SHIT
		if(Math.random() <= ((double)AMMO_SPAWN_PROMILLE/1000.0)){
			spawnAmmo();
		}
		
		//---BULLETS
		Set<Bullet> survivingBullets = new HashSet<Bullet>();
		
		for(Bullet bul : bullets){
			bul.update();
			Position p = bul.getPosition();
			
			boolean survived = true;
			if(p.getX() <= -bul.getRadius() || p.getX() >= width+bul.getRadius())
				survived=false;
			
			if(p.getY() <= -bul.getRadius() || p.getY() >= height+bul.getRadius())
				survived=false;
			
			if(survived)
				survivingBullets.add(bul);
		}
		
		bullets = survivingBullets;

		//---BOTS
		for(Bot bot : bots){
			bot.update(arenaKey);
			Position p = bot.getPosition();
			
			//X
			if(p.getX() <= MARGIN)
				bot.setX(MARGIN, arenaKey);
			else if(p.getX() >= width-bot.getRadius()-MARGIN)
				bot.setX(width-bot.getRadius()-MARGIN, arenaKey);
			
			//Y
			if(p.getY() <= MARGIN)
				bot.setY(MARGIN, arenaKey);
			else if(p.getY() >= height-bot.getRadius()-MARGIN)
				bot.setY(height-bot.getRadius()-MARGIN, arenaKey);
			
			//Speed
			if(bot.getVelocity().getMagnitude() > MAX_SPEED)
				bot.setVelocity(MAX_SPEED, arenaKey);
		
		}
		

		//---BULLET COLLISIONS
		survivingBullets = new HashSet<Bullet>();
		for(Bullet bul : bullets){

			boolean survived = true;
			
			for(Bot bot : bots){
				if(bot.getID()!=bul.getAuthor() && bot.collides(bul)){
					bot_map.get(bul.getAuthor()).setBulletHits(bot_map.get(bul.getAuthor()).getBulletHits()+1);
					bot.damageBot(arenaKey);
					if(bot.getHealth()==0){
						if(kills.containsKey(bul.getAuthor()))
							kills.put(bul.getAuthor(), kills.get(bul.getAuthor())+1);
						else 
							kills.put(bul.getAuthor(), 1);
					}
					survived=false;
					break;
				}
			}
			
			if(survived)
				survivingBullets.add(bul);
		}
		bullets = survivingBullets;
		
		//---AMMO COLLISIONS
		Set<Ammo> survivingAmmo = new HashSet<Ammo>();
		
		for(Ammo ammo : ammos){

			boolean survived = true;
			
			for(Bot bot : bots){
				if(bot.collides(ammo)){
					bot.findAmmo(arenaKey);
					bot.ammo(ammo.getID());
					survived=false;
					break;
				}
			}
			
			if(survived)
				survivingAmmo.add(ammo);
		}
		ammos = survivingAmmo;
		
		Set<Bot> survivingBots = new HashSet<Bot>();
		//---DEATHS
		for(Bot bot : bots){
			if(bot.getHealth() > 0)
				survivingBots.add(bot);
			else
				bot.battleEnd();
		}
		
		bots = survivingBots;
		
		//---END
		if(bots.size()<2 && ongoing){
			if(bots.size()==1){
				String name = null;
				for(Bot b : bots){
					name = b.getName();
					b.battleEnd();}
				if(scores.containsKey(name))
					scores.put(name, scores.get(name)+1);
				else
					scores.put(name, 1);
				ongoing=false;
			}
		} else if (bots.size()<2 && !ongoing){
			reset();
		}
	}
	
	/**
	 * Gets the view angle to either side of the bot in radians.
	 * @return
	 */
	public final double getViewAngle(){
		return VIEW_ANGLE;
	}
	
	/**
	 * Gets the width of this arena.
	 * @return
	 */
	public final double getWidth(){
		return width;
	}
	
	/**
	 * Gets the height of this arena.
	 * @return
	 */
	public final double getHeight(){
		return height;
	}
	
	/**
	 * Confirms whether or not this alleged key actually matches the randomly generated key of this Arena.
	 * Used to make sure bots aren't illegaly modifying their internal private variables.
	 * @param key
	 * @return
	 */
	public final boolean confirmKey(int key){
		return key==arenaKey;
	}
	
	/**
	 * Attemps to shoot a bullet in this Arena
	 * @param p The position of the bullet spawn.
	 * @param r The rotation of the bullet.
	 * @param v The velocity of the bullet.
	 * @param id The ID of the bot which attempts to launch this bullet.
	 */
	public final void shootFrom(Position p, Rotation r, Velocity v, int id){
		boolean exists = false;
		
		for(Bot b : bots){
			if(b.getID() == id){
				exists=true;
				break;
			}
		}
		
		if(!exists)
			return;

		bot_map.get(id).setBulletsFired(bot_map.get(id).getBulletsFired()+1);
		Bullet b = new Bullet(p, Velocity.fromAngular(BULLET_SPEED, r.getRadians()+rand()*BULLET_SPREAD).add(v), BULLET_RADIUS, globalNextBulletID++, id);
		
		bullets.add(b);
		//if(masterScribe != null) ScrollOfTraditions.spawnBullet(masterScribe, id, b.getRotation(), b.getVelocity());
	}
	
	private static final double rand(){
		return Math.random()*2-1;
	}
	
	/**
	 * Returns true if there is at most one bot remaining.
	 * @return
	 */
	public boolean finished(){
		return !ongoing;
	}
	
	/**
	 * Returns the winner (if exists) of this arena.
	 * @return
	 */
	public Bot getWinner(){
		if(finished()){
			for(Bot b : bots)
				return b;
		}
		return null;
	}
	
	/*private void spawnDust(Position pos){
		return;
		Dust d = new Dust(pos.getX(), pos.getY());
		dusts.add(d);
	}*/
	
	
	private Rotation getAngle(Position p, Rotation r, Position q){
		//Position center = new Position(getWidth()/2, getHeight()/2);
		return angleFromVectors(Math.cos(r.getRadians()), 
								Math.sin(r.getRadians()), 
								q.getX()-p.getX(), 
								q.getY()-p.getY(),
								false
								/*p.atan()>q.atan()*/);
	}
	
	private static Rotation angleFromVectors(double v1x, double v1y, double v2x, double v2y, boolean invert){
		double vecprod  = v1x*v2x + v1y*v2y,
			   v1len    = Math.hypot(v1x, v1y),
			   v2len    = Math.hypot(v2x, v2y),
			   cosAngle = vecprod/(v1len*v2len),
			   angle    = Math.acos(cosAngle);
		return new Rotation(invert?angle:-angle);
	}
	
	/**
	 * Returns the list of things that are viewable from some position and rotation in the Arena.
	 * @param p The position.
	 * @param r The rotation.
	 * @param id The ID of the bot trying to view.
	 * @return
	 */
	public List<Sighting> viewFrom(Position p, Rotation r, int id){
		List<Sighting> s = new ArrayList<Sighting>();
		
		for(Bot bot : bots){
			if(bot.getID()==id)continue;
			Position q = bot.getCenter();
			
			Rotation angle = getAngle(p,r,q);
			
			
			if(Math.abs(angle.getDegrees())%360 <= VIEW_ANGLE)
				s.add(new Sighting(ViewType.ENEMY, p.distanceTo(q), angle, q, bot.getVelocity(), bot.getID()));
			
		}


		for(Ammo ammo : ammos){
			Position q = ammo.getPosition();
			Rotation angle = getAngle(p,r,q);
			
			if(Math.abs(angle.getDegrees())%360 <= VIEW_ANGLE)
				s.add(new Sighting(ViewType.AMMO, p.distanceTo(q), angle, q, new Velocity(0,0), ammo.getID()));
			
		}
		
		for(Bullet bul : bullets){
			if(bul.getAuthor() == id)continue;
			Position q = bul.getPosition();
			Rotation angle = getAngle(p,r,q);
			
			if(Math.abs(angle.getDegrees())%360 <= VIEW_ANGLE)
				s.add(new Sighting(ViewType.BULLET, p.distanceTo(q), angle, q, bul.getVelocity(), bul.getID()));
			
		}
		
		return s;
	}
	
	/**
	 * Returns a list of all dust objects in this Arena (not currently in use).
	 * @return
	 */
	public List<Dust> getDust(){
		return new ArrayList<Dust>(dusts);
	}
}

