package wisdom_of_crowds;

import java.util.ArrayList;

public class Point {
	public double x;
	public double y;
	public int id;
	
	public Point(double x, double y, int id){
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public String toString(){
		return "(" + x +"," + y +")";
	}

}
