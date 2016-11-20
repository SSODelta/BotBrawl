package com.brawlbots.bots;

import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;

public final class PlayerControlledBot extends Bot {

	private boolean LEFT, RIGHT, UP, DOWN, SHOOT;
	
	public PlayerControlledBot(String name) {
		super(name);
		this.setLEFT(false);
		this.setRIGHT(false);
		this.setUP(false);
		this.setDOWN(false);
		this.setSHOOT(false);
	}
	
	public PlayerControlledBot(){
		super("Player");
	}

	@Override
	public BotAction updateBot() {
		return new BotAction(
								UP   ? 1  : DOWN  ? -1 : 0,
								LEFT ? -1 : RIGHT ?  1 : 0,
								SHOOT
							 );
	}

	@Override
	public void hurt() {
	}

	@Override
	public void ammo(int id) {
	}

	public boolean isLEFT() {
		return LEFT;
	}

	public void setLEFT(boolean lEFT) {
		LEFT = lEFT;
	}

	public boolean isRIGHT() {
		return RIGHT;
	}

	public void setRIGHT(boolean rIGHT) {
		RIGHT = rIGHT;
	}

	public boolean isUP() {
		return UP;
	}

	public void setUP(boolean uP) {
		UP = uP;
	}

	public boolean isDOWN() {
		return DOWN;
	}

	public void setDOWN(boolean dOWN) {
		DOWN = dOWN;
	}

	public boolean isSHOOT() {
		return SHOOT;
	}

	public void setSHOOT(boolean sHOOT) {
		SHOOT = sHOOT;
	}

	@Override
	public void battleEnd() {
		// TODO Auto-generated method stub
		
	}

}
