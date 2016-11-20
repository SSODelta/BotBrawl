package com.brawlbots.bots;

import java.io.FileNotFoundException;

import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;
import com.brawlbots.botscript.script.Script;
import com.brawlbots.botscript.script.ScriptEngine;
import com.brawlbots.botscript.script.ScriptRuntimeException;
import com.brawlbots.botscript.tokentrees.TreeParseException;
import com.brawlbots.util.Position;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.Velocity;
import com.brawlbots.util.ViewType;

public final class ScriptBot extends Bot {

	private ScriptEngine engine;
	private int hashId;
	
	public ScriptBot(String name, String script) throws TreeParseException, ScriptRuntimeException {
		super(name);
		engine = new ScriptEngine();
		
		
		if(script.toLowerCase().startsWith("#name ")){
			script = script.substring("#name ".length());
			
			int del = script.indexOf(";");
			String n = script.substring(0,del);
			this.setName(n);
			script = script.substring(del+1);
		}

		this.hashId = this.getName().hashCode();
		while(script.startsWith("\n"))
			script=script.substring(1);
		
		engine.registerMethods(script, hashId);
		
		if(!engine.hasMethod("start"))
			engine.setMethod("start", Script.fromString("", hashId));
		if(!engine.hasMethod("update"))
			engine.setMethod("update", Script.fromString("", hashId));
		if(!engine.hasMethod("sight"))
			engine.setMethod("sight", Script.fromString("", hashId));
		if(!engine.hasMethod("ammo"))
			engine.setMethod("ammo", Script.fromString("", hashId));
		if(!engine.hasMethod("hurt"))
			engine.setMethod("hurt", Script.fromString("", hashId));
		if(!engine.hasMethod("end"))
			engine.setMethod("end", Script.fromString("", hashId));
		
		engine.setVar(hashId, "type_ammo",   ViewType.AMMO.ordinal());
		engine.setVar(hashId, "type_bullet", ViewType.BULLET.ordinal());
		engine.setVar(hashId, "type_enemy",  ViewType.ENEMY.ordinal());
		engine.setVar(hashId, "type_friend", ViewType.FRIEND.ordinal());
		engine.setVar(hashId, "type_wall",   ViewType.WALL.ordinal());

		engine.setVar(hashId, "arena_width", 960);
		engine.setVar(hashId, "arena_height", 640);
		engine.setVar(hashId, "arena_margin", 50);
		
		engine.setVar(hashId, "pi", Math.PI);
		engine.setVar(hashId, "e", Math.E);
		
		engine.execute(engine.getMethod("start"));
		
		
	}
	
	public ScriptBot(String script) throws TreeParseException, ScriptRuntimeException{
		this("Script Bot",script);
	}

	@Override
	public BotAction updateBot() {
		try {
			//---UPDATE VARIABLES
			engine.setVar(hashId, "bot_ammo", this.getAmmo());
			engine.setVar(hashId, "bot_health", this.getHealth());
			engine.setVar(hashId, "bot_id", this.getID());
			
			Position p = this.getCenter();
			engine.setVar(hashId, "bot_x", p.getX());
			engine.setVar(hashId, "bot_y", p.getY());
			engine.setVar(hashId, "arena_width", this.getArenaWidth());
			engine.setVar(hashId, "arena_height", this.getArenaHeight());
			engine.setVar(hashId, "arena_margin", this.getArenaMargin());
			engine.setVar(hashId, "bot_radius", this.getRadius());
			engine.setVar(hashId, "bot_angle_deg", this.getRotation().getDegrees());
			engine.setVar(hashId, "bot_angle_rad", this.getRotation().getRadians());
			engine.setVar(hashId, "bot_reload_time", this.getTotalReloadTime());
			engine.setVar(hashId, "bot_viewsize_rad", this.getViewAngle());
			engine.setVar(hashId, "bot_viewsize_deg", Math.toDegrees(this.getViewAngle()));
			Velocity v = this.getVelocity();
			engine.setVar(hashId, "bot_dx", v.getDx());
			engine.setVar(hashId, "bot_dy", v.getDy());
			engine.setVar(hashId, "bot_speed_dx", v.getDx());
			engine.setVar(hashId, "bot_speed_dy", v.getDy());
			engine.setVar(hashId, "bot_speed_mag", v.getMagnitude());
			engine.setVar(hashId, "bot_speed_angle_rad", v.getAngle());
			engine.setVar(hashId, "bot_speed_angle_deg", Math.toDegrees(v.getAngle()));		
			
			//---UPDATE
			engine.execute(engine.getMethod("update"));
			
			//---SIGHTINGS---
			for(Sighting s : view()){
				Position ps = s.getAbsolutePosition();
				Velocity vs = s.getVelocity();
				engine.setVar(hashId, "sight_x", ps.getX());
				engine.setVar(hashId, "sight_y", ps.getY());
				
				engine.setVar(hashId, "sight_dx", vs.getDx());
				engine.setVar(hashId, "sight_dy", vs.getDy());
				
				engine.setVar(hashId, "sight_speed_angle_deg", vs.getAngle());
				engine.setVar(hashId, "sight_speed_angle_rad", Math.toDegrees(vs.getAngle()));
				
				engine.setVar(hashId, "sight_angle_deg", -s.getAngle().getDegrees());
				engine.setVar(hashId, "sight_angle_rad", -s.getAngle().getRadians());

				engine.setVar(hashId, "sight_dist", s.getDistance());
				engine.setVar(hashId, "sight_id", s.getID());
				
				engine.setVar(hashId, "sight_type", s.getType().ordinal());
				
				engine.execute(engine.getMethod("sight"));
			}
			return engine.getAction();
		} catch (ScriptRuntimeException e) {
			System.out.println("Unable to run script bot:\n\t"+e);
		}
		return new BotAction(0,0,false);
	}

	@Override
	public void hurt() {
		// TODO Auto-generated method stub
		try {
			engine.execute(engine.getMethod("hurt"));
		} catch (ScriptRuntimeException e) {
			System.out.println("Unable to run script bot:\n\t"+e);
		}
	}

	@Override
	public void ammo(int id) {
		// TODO Auto-generated method stub
		try {
			engine.setVar(hashId, "ammo_id", id);
			engine.execute(engine.getMethod("ammo"));
		} catch (ScriptRuntimeException e) {
			System.out.println("Unable to run script bot:\n\t"+e);
		}
	}

	@Override
	public void battleEnd() {
		try {
			try {
				engine.execute(engine.getMethod("end"));
			} catch (ScriptRuntimeException e) {
				e.printStackTrace();
			}
			engine.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
