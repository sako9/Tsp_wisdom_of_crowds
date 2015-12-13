package wisdom_of_crowds;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TSP {
	public ArrayList<Point> points;	
	public double dist;
	
	public TSP(){
		points = new ArrayList<>();
		dist = 0;
	}
	
	public TSP(ArrayList<Point> p){
		points = new ArrayList<>();
		points.addAll(p);
		dist = totalDistance(points);
	}
	
	public static void printList(List<Point> p){
		for(int i = 0; i < p.size(); i++){
			System.out.print("("+p.get(i).x + "," + p.get(i).y + ")");
		}
		System.out.println("");
	}
	
	public void readPointsFromFile(String fileName){
		int count = 0;
		try{
			for(Scanner sc = new Scanner(new File(fileName)); sc.hasNext();){
				String line = sc.nextLine();
				if(Character.isDigit(line.charAt(0))){
					String[] filePoints = line.split(" ");
					points.add(new Point(Double.parseDouble(filePoints[1]),
							Double.parseDouble(filePoints[2]),count));
					count++;
				}
			}
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}
	
	public TSP generatePermutation(){
		ArrayList<Point> shuffled = new ArrayList<>();
		shuffled.addAll(points);
		java.util.Collections.shuffle(shuffled, new SecureRandom());
		return new TSP(shuffled);
	}
	
	
	public double distance(Point a, Point b){
		return Math.sqrt(Math.pow((b.x - a.x),2) + Math.pow((b.y - a.y),2));
	}
	
	public double totalDistance(List<Point> p){
		double dist = 0.0;
		for(int i = 0 ; i < p.size() -1; i++){
			dist += distance(p.get(i),p.get(i+1)); 
		}
		return dist;
	}
	
	public void readPointsFromFile(File tspFile){
		points.clear();
		int count = 0;
		try{
			for(Scanner sc = new Scanner(tspFile); sc.hasNext();){
				String line = sc.nextLine();
				if(Character.isDigit(line.charAt(0))){
					String[] filePoints = line.split(" ");
					points.add(new Point(Double.parseDouble(filePoints[1]),
							Double.parseDouble(filePoints[2]),count));
					count++;
				}
			}
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}
	
}
