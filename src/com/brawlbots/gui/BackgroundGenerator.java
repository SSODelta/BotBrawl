package com.brawlbots.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.delta2.colours.BlendMode;
import com.delta2.colours.DImage;
import com.delta2.colours.filters.image.BlurFilter;
import com.delta2.colours.filters.image.MedianFilter;

public final class BackgroundGenerator {
	
	private ImageIcon BEACH,
					  DEEP,
					  GRASS_0,
					  GRASS_1,
					  GRASS_2,
					  GRASS_3,
					  GRASS_4,
					  GRASS_5,
					  SHALLOW;
	
	private static final int IMG_SIZE = 16;
	private static final double __IMG_SIZE = IMG_SIZE+0.0;
	
	private static final String tilePath = "spr/tiles/";
	
	public BackgroundGenerator(){
		
		BEACH   = new ImageIcon(tilePath+"beach.png");
		DEEP    = new ImageIcon(tilePath+"deep.png");
		GRASS_0 = new ImageIcon(tilePath+"grass0.png");
		GRASS_1 = new ImageIcon(tilePath+"grass1.png");
		GRASS_2 = new ImageIcon(tilePath+"grass2.png");
		GRASS_3 = new ImageIcon(tilePath+"grass3.png");
		GRASS_4 = new ImageIcon(tilePath+"grass4.png");
		GRASS_5 = new ImageIcon(tilePath+"grass5.png");
		SHALLOW = new ImageIcon(tilePath+"shallow.png");
		
	}
	
	public BufferedImage generateBackground(int width, int height){
		return generateBackground_aux((int)Math.ceil(width/__IMG_SIZE), (int)Math.ceil(height/__IMG_SIZE));
	}
	
	private BufferedImage generateBackground_aux(int tilesX, int tilesY){
		BufferedImage img = new BufferedImage(IMG_SIZE*tilesX, IMG_SIZE*tilesY, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		
		HeightMap hm = new HeightMap(tilesX, tilesY);
		Tile[][] tiles = hm.generate();
		
		Map<Tile, Point> tileToPoint = new HashMap<Tile, Point>();
		
		for(int x=0; x<tilesX; x++)
		for(int y=0; y<tilesY; y++){
			
			Tile t = tiles[x][y];
			tileToPoint.put(t, new Point(x,y));
		}
		
		List<Tile> tileList = new ArrayList<Tile>(tileToPoint.keySet());
		tileList.sort(new Comparator<Tile>(){

			@Override
			public int compare(Tile arg0, Tile arg1) {
				return (int)Math.signum(arg0.getVal() - arg1.getVal());
			}
			
		});
		
		int sz = (int)Math.ceil(IMG_SIZE*(Math.sqrt(2)-1)/2);
		
		for(Tile t : tileList){
			Point p = tileToPoint.get(t);
			int x = p.x, y = p.y;
			double rot = Math.toRadians(t.getRotation());
			g2d.rotate(rot, (x+0.5)*IMG_SIZE, (y+0.5)*IMG_SIZE);
			g2d.drawImage(getImageFromTileType(t.getType()).getImage(), x*IMG_SIZE-sz, y*IMG_SIZE-sz, IMG_SIZE+2*sz, IMG_SIZE+2*sz, null);
			g2d.rotate(-rot, (x+0.5)*IMG_SIZE, (y+0.5)*IMG_SIZE);
		}
		
		return new DImage(img).applyFilter(new MedianFilter(5)).toBufferedImage();
	}
	
	private ImageIcon getImageFromTileType(TileType t){
		//Core types
		if(t == TileType.DEEP)
			return DEEP;

		if(t == TileType.SHALLOW)
			return SHALLOW;

		if(t == TileType.SAND)
			return BEACH;
		
		if(t == TileType.GRASS)
			switch((int)(Math.random()*6)){
				default:
					return GRASS_0;
				case 1:
					return GRASS_1;
				case 2:
					return GRASS_2;
				case 3:
					return GRASS_3;
				case 4:
					return GRASS_4;
				case 5:
					return GRASS_5;
			}

		return null;
	}
	
}
