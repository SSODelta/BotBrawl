package com.brawlbots.bots;

import com.brawlbots.Bot;
import com.brawlbots.action.BotAction;
import com.brawlbots.util.Position;
import com.brawlbots.util.Sighting;
import com.brawlbots.util.ViewType;

public class SniperBot extends Bot {

	private static final int MODE_RESOURCES = 0,
							 MODE_RETREAT   = 1,
							 MODE_SNIPER    = 2;
	
	private int mode, direction;
	
	private Position prevPosition;
	
	public SniperBot() {
		super("Sniper Bot");
		mode = MODE_RESOURCES;
		direction = Math.random()<0.5?1:-1;
		prevPosition=null;
	}
	
	@Override
	public String getName(){
		return super.getName() + " ("+mode+")";
	}

	@Override
	public BotAction updateBot() {
		boolean turn = true, shoot=false, changeable=true;
		int a=1;
		
		for(Sighting s : view()){
			if(!changeable)break;
			
			switch(mode){
				case MODE_RESOURCES:
					a=1;
					if(getAmmo() > 5){
						mode=MODE_RETREAT;
						continue;
					}
					if(s.getType() == ViewType.AMMO){
						turn=true;
						direction = -(int)s.getAngle().getDegrees();
						changeable=false;
					}
					break;
				
				case MODE_RETREAT:
					turn=false;
					if(getVelocity().getMagnitude() > 1){
						a=-1;
						changeable=false;
						break;
					}
					if(Math.abs(prevPosition.distanceTo(getPosition())) < 1){
						mode=MODE_SNIPER;
						continue;
					}
					break;
					
				case MODE_SNIPER:
					if(getAmmo() == 0){
						mode=MODE_RESOURCES;
						continue;
					}
					if(s.getType() == ViewType.ENEMY){
						if(Math.abs(s.getAngle().getDegrees()) <= 10){
							shoot=true;
							turn=false;
							changeable=false;
							break;
						}
						direction=(int)s.getAngle().getDegrees();
						changeable=false;
						break;
					} 
					direction = (int)Math.toDegrees(Math.atan2(	prevPosition.getY()-getArenaHeight()/2, 
																prevPosition.getX()-getArenaWidth()/2	) + Math.PI - getRotation().getRadians());
					turn=true;
					changeable=false;
					break;
			}
		}

		prevPosition=getPosition();
		
		return new BotAction(a, turn?direction:0, shoot);
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
