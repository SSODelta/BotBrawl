package com.brawlbots.bots;


import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.ViewType;

public class SimpleBot extends Bot {

	private int direction;
	
	public SimpleBot() {
		super("Simple Bot");
		direction = Math.random()<0.5?1:-1;
	}

	@Override
	public BotAction updateBot() {
		boolean shoot = false, turn=true;
		
		if(Math.random()<0.0025)
			direction*=-1;
		
		for(Sighting s : view()){
			if(s.getType() == ViewType.AMMO){
				turn=false;
				break;
			}
			if(s.getType() == ViewType.ENEMY){
				turn=false;
				shoot=true;
				break;
			}
		}
		return new BotAction(1, turn?direction:0, shoot);
	}

	@Override
	public void hurt() {
		// Do nothing
	}
	
	@Override
	public void ammo(int id){
		//Do nothing
	}

	@Override
	public void battleEnd() {
		// TODO Auto-generated method stub
		
	}


}
