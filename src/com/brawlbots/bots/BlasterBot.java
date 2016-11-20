package com.brawlbots.bots;

import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.ViewType;

public class BlasterBot extends Bot{

	//private static int 
	
	private int direction, shootStep;
	private boolean shootMode, bullet;
	
	public BlasterBot() {
		super("Blaster Bot");
		direction = Math.random()<0.5?1:-1;
		shootMode = true;
		shootStep=20;
		bullet=false;
	}

	@Override
	public BotAction updateBot() {
		boolean shoot = false, turn=true;
		
		//Turn a bit randomly
		if(Math.random()<0.00025 || (bullet && Math.random()<0.001))
			direction*=-1;
		
		bullet=false;
		
		if(getAmmo()>=8 && !shootMode){
			shootMode=true;
		} else if(getAmmo()==0){
			shootStep=0;
			shoot=false;
			shootMode=false;
		}
		
		if(shootStep>0){
			shoot=true;
			shootStep--;
		}
		
		for(Sighting s : view()){
			if((shootMode && s.getType()==ViewType.ENEMY))
				shootStep=20;
			if((shootMode && s.getType()==ViewType.ENEMY)|| (!shootMode && s.getType()==ViewType.AMMO)){
				if(s.getDistance()>=100 || shootMode)
					if(s.getAngle().getDegrees()>=1)
						direction=-1;
					else if(s.getAngle().getDegrees()<=-1)
						direction=1;
				
				if(Math.abs(s.getAngle().getRadians()) < 10 &&((s.getDistance()<500 && s.getType()==ViewType.AMMO) || (s.getType()==ViewType.ENEMY)))
						turn=false;
				
			}
			
			if(s.getType()==ViewType.BULLET){
				turn=true;
				bullet=true;
			}

		}
		return new BotAction(1, turn?direction:0, shoot);
	}

	@Override
	public void hurt() {
		// TODO Auto-generated method stub
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
