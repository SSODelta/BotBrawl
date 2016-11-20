package com.brawlbots.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.brawlbots.Ammo;
import com.brawlbots.Arena;
import com.brawlbots.Bot;
import com.brawlbots.Bullet;
import com.brawlbots.Dust;
//import com.brawlbots.lorekeeper.Scribe;
import com.brawlbots.util.Position;
import com.brawlbots.util.Rotation;

public class AIArenaPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3719407242144968938L;
	private static final double RAY_LENGTH = 160;
			
	private Arena arena;
	private ImageIcon ammo;
	private ImageIcon[] planes, bullets;
	private BufferedImage generatedBackground;
	private Timer t;
	private final static int NUM_BULLETS = 1;
	
	private int width, height, step;
	
	private boolean running = false;
	
	public AIArenaPanel(int width, int height){
		this.width = width;
		this.height = height;
		
		planes = new ImageIcon[26];
		bullets = new ImageIcon[NUM_BULLETS];
		for(int i=0; i<26; i++)
			planes[i] = new ImageIcon("spr/planes/plane"+i+".png");

		for(int i=0; i<NUM_BULLETS; i++)
			bullets[i] = new ImageIcon("spr/bullets/bullet"+i+".png");
	
		ammo = new ImageIcon("ammo.png");
		
		arena = new Arena(64, width, height);
		
		t = new Timer();
		
		this.setPreferredSize(new Dimension(width,height));
	
		
		BackgroundGenerator gen = new BackgroundGenerator();
		generatedBackground = gen.generateBackground(width, height);
	}
	
	public void run(int FPS){
		t.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				
				arena.update();
				repaint();

				if(step++ >= 1 || arena.finished()){
					step=0;
					Bot winner = arena.getWinner();
					if(winner!=null)
						increment(winner.getName());
					//arena.masterScribe.masterScroll.printAsBinary(1000);
					
					//arena = new Arena(Math.min(spaceship.getIconWidth()/2, spaceship.getIconHeight()/2), width, height);
					running = false;
				} else running = true;
				
			}
			
		}, 1000/FPS, 1000/FPS);
	}
	
	public boolean running(){
		return running;
	}
	
	public void reset(){
		arena.reset();
	}
	
	public void init(Collection<? extends Bot> bots){
		//arena.masterScribe = new Scribe(bots.size(), bots.size());
		for(Bot b : bots)
			arena.addBot(b);
	}
	
	public void init(Bot[] bots){
		//arena.masterScribe = new Scribe(bots.length, bots.length);
		for(Bot b : bots)
			arena.addBot(b);
	}
	
	public void paintBG(Graphics g){
		g.drawImage(generatedBackground, 0, 0, width, height, null);
	}
	
	public void setLeft(boolean val){
		arena.setLeft(val);
	}
	public void setRight(boolean val){
		arena.setRight(val);
	}
	public void setUp(boolean val){
		arena.setUp(val);
	}
	public void setDown(boolean val){
		arena.setDown(val);
	}
	public void setShoot(boolean val){
		arena.setShoot(val);
	}
	
	private void drawRays(Graphics2D g2d, Position p, Rotation r){
		double ang1 = Math.toRadians(r.getDegrees()-arena.getViewAngle()),
			   ang2 = Math.toRadians(r.getDegrees()+arena.getViewAngle());
		
		double r1x = p.getX() + RAY_LENGTH*Math.cos(ang1),
			   r1y = p.getY() + RAY_LENGTH*Math.sin(ang1),
			   r2x = p.getX() + RAY_LENGTH*Math.cos(ang2),
			   r2y = p.getY() + RAY_LENGTH*Math.sin(ang2);
				   
		g2d.drawLine((int)p.getX(), (int)p.getY(), (int)r1x, (int)r1y);
		g2d.drawLine((int)p.getX(), (int)p.getY(), (int)r2x, (int)r2y);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		paintBG(g);
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform at = new AffineTransform(); 
		g2d.setTransform(at);
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14f));
        
		for(Dust d : arena.getDust()){
			float alpha = (float)Math.pow(d.t / Dust.DUST_LIFE,2);
			AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			g2d.setComposite(composite);
			g2d.setColor(Color.WHITE);
			g2d.fillOval((int)d.x, (int)d.y, (int)(d.r*alpha), (int)(d.r*alpha));
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		for(Bullet b : arena.getBullets()){
			Position p = b.getPosition();
			drawImage(g2d, getBulletFromBullet(b), p, b.getRotation());
		}

		g2d.setTransform(at);
		for(Ammo a : arena.getAmmo()){
			g2d.drawImage(ammo.getImage(), (int)a.getPosition().getX()-16,(int)a.getPosition().getY()-16,null);
		}
		
		for(Bot b : arena.getBots()){
			//Reset transform
			g2d.setTransform(at);
			
			//Draw name
			g2d.setColor(Color.BLACK);
			g2d.drawString(b.getName(), (int)b.getPosition().getX()+16, (int)b.getPosition().getY()+111);

			g2d.setColor(Color.BLACK);
			//Draw rays
			drawRays(g2d, b.getCenter(), b.getRotation());
			
			//And then draw the bot
			drawImage(g2d, getPlaneFromBot(b), b.getPosition(), b.getRotation());
		}

		g2d.setTransform(at);
		int y=0;
		for(String line : getInfo())
			g2d.drawString(line, 24, 24+24*y++);
		
	}
	
	private ImageIcon getBulletFromBullet(Bullet b){
		int seed = b.getAuthor() % NUM_BULLETS;
		if(seed<0) seed = NUM_BULLETS+seed;
		return bullets[seed];
	}
	
	private ImageIcon getPlaneFromBot(Bot b){
		int seed = b.getName().hashCode() % 26;
		if(seed<0) seed = 26+seed;
		return planes[seed];
	}
	
	private int score(String name){
		if(!arena.scores.containsKey(name))return 0;
		return arena.scores.get(name);
	}
	
	private int kills(int name){
		if(!arena.kills.containsKey(name))return 0;
		return arena.kills.get(name);
	}
	
	private void increment(String name){
		if(!arena.scores.containsKey(name))
			arena.scores.put(name, 1);
		else
			arena.scores.put(name, arena.scores.get(name)+1);
	}
	
	public String[] getInfo(){
		StringBuilder sb = new StringBuilder();
		
		for(Bot b : arena.getBots()){
			sb.append(b.getName());
			sb.append(" (");
			sb.append(b.getAmmo());
			sb.append(" shots)\nScore: ");
			sb.append(score(b.getName()));
			sb.append("\nKills: ");
			sb.append(kills(b.getID()));
			sb.append("\n\n");
		}
		
		return sb.toString().split("\n");
	}
	
	private void drawImage(Graphics2D g2d, ImageIcon icn, Position p, Rotation r){
		Image img = icn.getImage();
		AffineTransform at = new AffineTransform();
        at.setToRotation(r.getRadians()+Math.PI/2, p.getX() + (icn.getIconWidth() / 2), p.getY() + (icn.getIconHeight() / 2));
        at.translate(p.getX(), p.getY());
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, this);
	}



}
