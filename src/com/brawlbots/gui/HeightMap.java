package com.brawlbots.gui;

import java.util.Random;

public final class HeightMap {

	private final int w, h;
	private final OpenSimplexNoise osn;
	
	private final static double THRESHOLD_DEEP    = 0.2, 
								THRESHOLD_SHALLOW = 0.5, 
								THRESHOLD_SAND    = 0.8;
								//the rest is grass
	
	public HeightMap(int w, int h){
		this.h=h;
		this.w=w;
		osn = new OpenSimplexNoise(new Random().nextLong());
	}
	
	private final static double SIZE = 6;
	
	/**
	 * HEIGHT VALUES:
	 * 
	 *  [-1   to -0.5]  = DEEP
	 *  [-0.5 to    0]  = SHALLOW
	 *  [0    to  0.5]  = SAND
	 *  [0.5  to    1]  = GRASS
	 */
	public Tile[][] generate(){
		Tile[][] tiles = new Tile[w][h];
		for(int x=0; x<w; x++)
		for(int y=0; y<h; y++)
			tiles[x][y] = new Tile(getTileType(osn.eval(x/SIZE, y/SIZE)/0.87), //norm constants not right according to @tyronx (https://gist.github.com/KdotJPG/b1270127455a94ac5d19)
								   (int)(Math.random()*360));
		return tiles;
	}
	
	private static final TileType getTileType(double d){
		if(d<THRESHOLD_DEEP)
			return TileType.DEEP;
		
		if(d<THRESHOLD_SHALLOW)
			return TileType.SHALLOW;
		
		if(d<THRESHOLD_SAND)
			return TileType.SAND;
		
		return TileType.GRASS;
	}
	
}
