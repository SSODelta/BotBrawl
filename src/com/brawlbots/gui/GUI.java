package com.brawlbots.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JFrame;

import com.brawlbots.Bot;

public final class GUI extends JFrame {

	private static AIArenaPanel panel;
	private static final long serialVersionUID = 4520276562649253094L;

	private static final int WIDTH  = 1280,
							 HEIGHT = 720;
	
	private List<Bot> bots;
	
	public GUI(){
		super("AIArena - Let the games begin");
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setResizable(false);
		this.addKeyListener(KeyBoardListener);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
	}

	public void start(){
		panel = new AIArenaPanel(WIDTH, HEIGHT);
		add(panel);
		pack();
		
		setVisible(true);

		panel.init(bots);
		panel.run(140);
	}

	public void addBot(Bot b){
		bots.add(b);
	}
	
	public void clearBots(){
		bots.clear();
	}
	
	public KeyListener KeyBoardListener = new KeyListener()
	{
	    @Override
	    public void keyPressed(KeyEvent args0) {
	    	int key = args0.getKeyCode();
	    	if(key == KeyEvent.VK_ENTER) 
	    		panel.reset();
	    	if(key == KeyEvent.VK_LEFT)
	    		panel.setLeft(true);
	    	if(key == KeyEvent.VK_RIGHT)
	    		panel.setRight(true);
	    	if(key == KeyEvent.VK_UP)
	    		panel.setUp(true);
	    	if(key == KeyEvent.VK_DOWN)
	    		panel.setDown(true);
	    	if(key == KeyEvent.VK_SPACE)
	    		panel.setShoot(true);
	    }

		@Override
		public void keyReleased(KeyEvent arg0) {
	    	int key = arg0.getKeyCode();
	    	if(key == KeyEvent.VK_LEFT)
	    		panel.setLeft(false);
	    	if(key == KeyEvent.VK_RIGHT)
	    		panel.setRight(false);
	    	if(key == KeyEvent.VK_UP)
	    		panel.setUp(false);
	    	if(key == KeyEvent.VK_DOWN)
	    		panel.setDown(false);
	    	if(key == KeyEvent.VK_SPACE)
	    		panel.setShoot(false);
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}   
	};
}
