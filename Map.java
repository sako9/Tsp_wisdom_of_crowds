package wisdom_of_crowds;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class Map extends JPanel{
	public ArrayList<Point> route;
	public ArrayList<Point> aggregateRoute;
	public int[][] matrix;
	
	Map(List<Point> route){
		this.route = (ArrayList<Point>) route;
	}
	
	Map(){
		this.route = new ArrayList<>();
		this.aggregateRoute = new ArrayList<>();
	}
	
	
	
	//update the map
	
	public void setRoute(List<Point> route){
		this.route.clear();
		this.route.addAll(route);
	}
	
	public void setAggregateRoute(List<Point> aggregateRoute){
		this.aggregateRoute.clear();
		this.aggregateRoute.addAll(aggregateRoute);
		this.aggregateRoute.add(aggregateRoute.get(0));
	}
	
	public void paint(Graphics g){
		super.paint(g);
		update(g);
	}
	
	public void update(Graphics g){
		int width = getBounds().width;
		int height = getBounds().height;
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		
		if(route == null || route.size() == 0) return;
		
		int prevx = (int)route.get(0).x;
		int prevy = (int)route.get(0).y;
		for( int i=0; i<route.size(); i++){
			if(i == route.size()-1){
				g2d.setColor(Color.red);
			}else{
				g2d.setColor(Color.green);
			}
			int xpos =(int) route.get(i).x *9 +100;
			int ypos = (int) route.get(i).y *3 +200;
			g2d.fillOval(xpos  -5 ,ypos -5, 10 , 10);
			if(i !=0 ){
				g2d.setColor(Color.white);
				g2d.drawLine(prevx , prevy, xpos , ypos );
			}
			prevx = xpos;
			prevy = ypos;
		}
		
		prevx = (int)aggregateRoute.get(0).x;
		prevy = (int)aggregateRoute.get(0).y;
		for( int i=0; i<aggregateRoute.size(); i++){
//			if(i == route.size()-1){
//				g2d.setColor(Color.red);
//			}else{
//				g2d.setColor(Color.green);
//			}
			int xpos =(int) aggregateRoute.get(i).x *9 +100;
			int ypos = (int) aggregateRoute.get(i).y *3 +200;
			//g2d.fillOval(xpos  -5 ,ypos -5, 10 , 10);
			if(i !=0 ){
				g2d.setColor(Color.red);
				g2d.drawLine(prevx , prevy, xpos , ypos );
			}
			prevx = xpos;
			prevy = ypos;
		}
		
	}

}
