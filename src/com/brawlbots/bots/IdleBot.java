package com.brawlbots.bots;

import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;

/**
 * This bot does nothing.
 * @author nikol
 *
 */
public final class IdleBot extends Bot {

	public IdleBot() {
		super("Idle Bot");
	}

	@Override
	public BotAction updateBot() {
		return null;
	}

	@Override
	public void hurt() {
		
	}

	@Override
	public void ammo(int id) {
		
	}

	@Override
	public void battleEnd() {
		// TODO Auto-generated method stub
		
	}

}
