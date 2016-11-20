package com.brawlbots.util;

import java.text.DecimalFormat;

public abstract class Misc {

	private static final DecimalFormat df = new DecimalFormat("#.###");
	public static String form(double k) {
		return df.format(k).replace(",", ".");
	}
	public static Position getIntersection(Velocity v1, Velocity v2, Position p1, Position p2) throws Exception{
		
		double a1, a2, b1, b2;
		a1 = v1.getDy()/v1.getDx();
		a2 = v2.getDy()/v1.getDx();
		if(a1 == a2){
			throw new Exception();
		}
		else{
			b1 = p1.getY() - p1.getX() * a1;
			b2 = p2.getY() - p2.getX() * a2; 
			double x = (b2-b1)*(a1-a2);
			double y = x*a1+b1;
			return new Position(x, y);
		}
	}
	
	public static double pointDistance(double x, double y, double x1, double y1, double x2, double y2) {

		  double A = x - x1;
		  double B = y - y1;
		  double C = x2 - x1;
		  double D = y2 - y1;

		  double dot = A * C + B * D;
		  double len_sq = C * C + D * D;
		  double param = -1;
		  if (len_sq != 0) //in case of 0 length line
		      param = dot / len_sq;

		  double xx, yy;

		  if (param < 0) {
		    xx = x1;
		    yy = y1;
		  }
		  else if (param > 1) {
		    xx = x2;
		    yy = y2;
		  }
		  else {
		    xx = x1 + param * C;
		    yy = y1 + param * D;
		  }

		  double dx = x - xx;
		  double dy = y - yy;
		  return Math.sqrt(dx * dx + dy * dy);
		}
	
}
