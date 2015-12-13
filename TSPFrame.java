package wisdom_of_crowds;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This program demonstrates how to work with JFrame in Swing.
 * @author www.codejava.net
 *
 */
public class TSPFrame extends JFrame implements ActionListener {
	private File tspFile = null;
	private Map map = null;
	private JTextField PopulationSize = null;
	private JTextField GroupSize =null;
	private JTextField iterations = null;
	private JButton start = null;
	JRadioButton SingleCross = null;
	JRadioButton SubPaths = null;
	private JTextArea status;
	int popSize = 0;
	int groupSize = 0;	
	TSP t = new TSP();
	public double minPath = 0.0;
	public ArrayList<Point> minPathList;
	public ArrayList<TSP> population = null;

	
	public void actionPerformed(ActionEvent e) {
	    System.out.println("Selected: " + e.getActionCommand());
	    switch(e.getActionCommand()){
	    	case "Exit":
	    		int reply = JOptionPane.showConfirmDialog(TSPFrame.this,
						"Are you sure you want to quit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					dispose();
				} else {
					return;
				}
	    		break;
	    	case "Open TSP":
	    		JFileChooser fileChooser = new JFileChooser();
	    		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    		FileFilter filter = new FileNameExtensionFilter("TSP File","tsp");
	    		fileChooser.setFileFilter(filter);
	    		int result = fileChooser.showOpenDialog(this);
	    		if(result == JFileChooser.APPROVE_OPTION){
	    			File fileSelected = fileChooser.getSelectedFile();
	    			tspFile = fileSelected;
	    			status.setText("Press Start");
	    			status.update(getGraphics());
	    			t.readPointsFromFile(tspFile);
	    			

	    		}
	    		break;
	    	case "Start":
	    		if(t.points.size() > 0){
	    			population = new ArrayList<>();
	    			getPopulationSize();
	    			getGroupSize();
	    			generatePopulation(popSize);
					minPathList = new ArrayList<>();
					minPath = 0.0;
					Random rand = new Random();
					status.setText("Processing...");
					status.update(status.getGraphics());
					aggregation();
					findShortestPathGenetic();
					int iter = getIterations();
	    			for(int j = 0; j < iter; j++){
	    				if(SingleCross.isSelected()){
	    					mutationFuction(groupSize);
	    				}else{
	    					mutationFuction2(groupSize);
	    				}
	    				if(rand.nextInt(50) == 1)aggregation();
	    				findShortestPathGenetic();	    				
	    			}
    				
    				minPath = totalDistance(minPathList);
	    			String path = "";
					for (int i = 0; i < minPathList.size(); i++) {
						path += "(" + minPathList.get(i).x + ","
								+ minPathList.get(i).y + ")";
					}
					status.setText("The min path is:" + path
							+ "with a distance of " + minPath);
					status.update(status.getGraphics());
	    		}
    			break;
	    		
	    	default :
	    		System.out.println("Invalid option");
	    		break;
	    }

	  }
	
	public void aggregation(){
		TSP current = null;
		double min = 0.0;
		double dist = 0.0;
		ArrayList<TSP> mins = new ArrayList<>();
		ArrayList<Point> child = new ArrayList<>();
		int[][] matrix = new int[population.get(0).points.size()][population.get(0).points.size()];
		for(int i = 0; i < 10; i ++){
			for(TSP p : population){
				if(!mins.contains(p)){
					dist = p.dist ;
					if(dist < min || min == 0){
						current = p;
						min = dist;
					}
				}
			}
			mins.add(current);
			min = 0.0;
		}
		
		
		for(int i = 0; i < 10; i++){
			current = mins.get(i);
			for(int j = 0; j < current.points.size()-1; j++){
				matrix[current.points.get(j).id][current.points.get(j+1).id]++;
			}
		}
		child.add(t.points.get(0));
		for(int i = 0; i < population.get(0).points.size()-1; i++){
			double max = 0;
			int index = 0;
			for(int j =0; j < population.get(0).points.size();j++){
				if(matrix[i][j] > max || max == 0 && !child.contains(t.points.get(j))){
					if(matrix[i][j] == max){
						Random rand = new Random();
						if(rand.nextInt(2) == 1){
							max = matrix[i][j];
							index = j;
						}
					}else{
						max = matrix[i][j];
						index = j;
					}
				}
			}
			child.add(t.points.get(index));
		}
		for(int i = 0; i < child.size(); i++){ 
			int duplicateIndex = child.lastIndexOf(child.get(i));
			if(duplicateIndex != i){
				for(int j = 0; j < current.points.size(); j++){
					if(!child.contains(current.points.get(j))){
						child.set(duplicateIndex, current.points.get(j));
						i--;
						break;
					}
				}
			}
		}
		TSP newChild = new TSP(child);
		// remove larget two paths from population, and add in children
		dist = 0;
		int largestIndex = 0;
		for(int i = 0; i < population.size(); i++){
			if(population.get(i).dist > dist || dist == 0){
				largestIndex =population.indexOf(population.get(i));
				dist = population.get(i).dist;
			}
		}
		map.setAggregateRoute(child);
		//map.update(map.getGraphics());
		population.set(largestIndex,newChild);
	}
	
	public void mutationFuction(int groupSize){
		TSP minList = null;
		TSP minList2 = null;
		Random rand = new Random();
		ArrayList<Point> child = new ArrayList<>();
		ArrayList<Point> child2 = new ArrayList<>();
		TSP current = null;
		double min = 0;
		double min2 = 0;
		double dist = 0;
		
		ArrayList<Integer> randomIndex = new ArrayList<>(); // genereate list of numbers from 0 to population size, doing this insures that we can get random paths without getting duplicates
		for(int i = 0; i < population.size(); i++){
			randomIndex.add(i);
		}
		java.util.Collections.shuffle(randomIndex,new SecureRandom());

		for(int i = 0; i < groupSize; i++){ //find two shortest paths
			current = population.get(randomIndex.get(i));
			dist = current.dist;
			if(dist < min || min == 0){
				minList2 = minList;
				min2 = min;
				minList = current;
				min = dist;
			}else if (dist < min2 || min2 == 0){
				minList2 = current;
				min2 = dist;
			}
		}
		//grab groupSize amount of paths
		//find the shortest 2 of the group
		//generate child
		//fix child if needed
		//random mutation
		int index = rand.nextInt(minList.points.size()+1); // cross over mutation 
		for(int i = 0; i <index; i++){
			child.add(minList.points.get(i));
			child2.add(minList2.points.get(i));
		}
		for(int i = 0; i < minList.points.size() - index; i++){
			child.add(minList2.points.get(i));
			child2.add(minList.points.get(i));
		}
		// fix duplicates in children
		for(int i = 0; i < child.size(); i++){ 
			int duplicateIndex = child.lastIndexOf(child.get(i));
			if(duplicateIndex != i){
				for(int j = 0; j < minList.points.size(); j++){
					if(!child.contains(minList.points.get(j))){
						child.set(duplicateIndex, minList.points.get(j));
						java.util.Collections.swap(child, duplicateIndex, rand.nextInt(child.size()));
						break;
					}
				}
			}
		}
		for(int i = 0; i < child2.size(); i++){
			int duplicateIndex = child2.lastIndexOf(child2.get(i));
			if(duplicateIndex != i){
				for(int j = 0; j < minList.points.size(); j++){
					if(!child2.contains(minList.points.get(j))){
						child2.set(duplicateIndex, minList.points.get(j));
						java.util.Collections.swap(child2, duplicateIndex, rand.nextInt(child2.size()));
						break;
					}
				}
			}
		}
		//random mutation, to insure populaiton doesn't get too similar
		int indexA = rand.nextInt(child.size());
		int indexB = rand.nextInt(child.size());
		List sub = null;
		if(indexA > indexB){
			sub = child.subList(indexB, indexA);
		}else{
			sub = child.subList(indexA, indexB);
		}
	    java.util.Collections.shuffle(sub, new SecureRandom());
		java.util.Collections.swap( child, rand.nextInt(child.size()),rand.nextInt(child.size()) );
		
		indexA = rand.nextInt(child2.size());
	    indexB = rand.nextInt(child2.size());
	    if(indexA > indexB){
			sub = child.subList(indexB, indexA);
		}else{
			sub = child.subList(indexA, indexB);
		}
	    java.util.Collections.shuffle(sub,new SecureRandom());
		java.util.Collections.swap( child2, rand.nextInt(child2.size()),rand.nextInt(child2.size()) );
		TSP newChild = new TSP(child);
		TSP newChild2 = new TSP(child2);
		
		// remove largetst two paths from population, and add in children
		dist = 0;
		int largestIndex = 0;
		for(int i = 0; i < groupSize; i++){
			if(population.get(randomIndex.get(i)).dist > dist || dist == 0){
				largestIndex =population.indexOf(population.get(randomIndex.get(i)));
				dist = population.get(randomIndex.get(i)).dist;
			}
		}
		population.set(largestIndex,newChild);
		dist = 0;
		largestIndex = 0;
		for(int i = 0; i < groupSize; i++){
			if(population.get(randomIndex.get(i)).dist > dist || dist == 0){
				largestIndex =population.indexOf(population.get(randomIndex.get(i)));
				dist = population.get(randomIndex.get(i)).dist;
			}
		}
		population.set(largestIndex,newChild2);
		
	}
	
	
	public void mutationFuction2(int groupSize){
		TSP minList = null;
		TSP minList2 = null;
		Random rand = new Random();
		ArrayList<Point> child = new ArrayList<>();
		ArrayList<Point> child2 = new ArrayList<>();
		TSP current = null;
		double min = 0;
		double min2 = 0;
		double dist = 0;
		
		ArrayList<Integer> randomIndex = new ArrayList<>();
		for(int i = 0; i < population.size(); i++){
			randomIndex.add(i);
		}
		java.util.Collections.shuffle(randomIndex,new SecureRandom());
		ArrayList<Integer> ranInd = new ArrayList<>();
		for(int i = 0; i < population.get(0).points.size(); i++){
			ranInd.add(i);
		}
		java.util.Collections.shuffle(ranInd,new SecureRandom());
		for(int i = 0; i < groupSize; i++){
			current = population.get(randomIndex.get(i));
			dist = current.dist;
			if(dist < min || min == 0){
				minList2 = minList;
				min2 = min;
				minList = current;
				min = dist;
			}else if (dist < min2 || min2 == 0){
				minList2 = current;
				min2 = dist;
			}
		//grab groupSize amount of paths
		//find the shortest 2 of the group
		//generate child
		//fix child if needed
		//random mutation
		}
		for(int i = 0; i < minList.points.size()-1; i+=2){ //sub list algorithm
			if(rand.nextInt(2) == 0){
				child.add(minList.points.get(i));  //adding sub lists
				child.add(minList.points.get(i+1));
				child2.add(minList2.points.get(i));
				child2.add(minList2.points.get(i+1));
			}else{
				child.add(minList2.points.get(i));
				child.add(minList2.points.get(i+1));
				child2.add(minList.points.get(i));
				child2.add(minList.points.get(i+1));
			}
		}
		
		if(child.size() < minList.points.size()){ //insure children have right number of elements
			child.add(minList.points.get(minList.points.size() -1));
		}
		if(child2.size() < minList.points.size()){
			child2.add(minList2.points.get(minList2.points.size() -1));
		}
		//fix children
		for(int i = 0; i < child.size(); i++){
			int duplicateIndex = child.lastIndexOf(child.get(i));
			if(duplicateIndex != i){
				for(int j = 0; j < minList.points.size(); j++){
					if(!child.contains(minList.points.get(j))){
						child.set(duplicateIndex, minList.points.get(j));
						java.util.Collections.swap(child, duplicateIndex, rand.nextInt(child.size()));
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < child2.size(); i++){
			int duplicateIndex = child2.lastIndexOf(child2.get(i));
			if(duplicateIndex != i){
				for(int j = 0; j < minList.points.size(); j++){
					if(!child2.contains(minList.points.get(j))){
						child2.set(duplicateIndex, minList.points.get(j));
						java.util.Collections.swap(child2, duplicateIndex, rand.nextInt(child2.size()));
						break;
					}
				}
			}
		}
		
		//mutations!!!
		int indexA = rand.nextInt(child.size());
		int indexB = rand.nextInt(child.size());
		List sub = null;
		if(indexA > indexB){
			sub = child.subList(indexB, indexA);
		}else{
			sub = child.subList(indexA, indexB);
		}
	    java.util.Collections.shuffle(sub, new SecureRandom());
		java.util.Collections.swap( child, rand.nextInt(child.size()),rand.nextInt(child.size()) );
		
		indexA = rand.nextInt(child2.size());
	    indexB = rand.nextInt(child2.size());
	    if(indexA > indexB){
			sub = child.subList(indexB, indexA);
		}else{
			sub = child.subList(indexA, indexB);
		}
	    java.util.Collections.shuffle(sub,new SecureRandom());
		java.util.Collections.swap( child2, rand.nextInt(child2.size()),rand.nextInt(child2.size()) );
		TSP newChild = new TSP(child);
		TSP newChild2 = new TSP(child2);
		dist = 0;
		int largestIndex = 0;
		for(int i = 0; i < groupSize; i++){
			if(population.get(randomIndex.get(i)).dist > dist || dist == 0){
				largestIndex =population.indexOf(population.get(randomIndex.get(i)));
				dist = population.get(randomIndex.get(i)).dist;
			}
		}
		population.set(largestIndex,newChild);
		dist = 0;
		largestIndex = 0;
		for(int i = 0; i < groupSize; i++){
			if(population.get(randomIndex.get(i)).dist > dist || dist == 0){
				largestIndex =population.indexOf(population.get(randomIndex.get(i)));
				dist = population.get(randomIndex.get(i)).dist;
			}
		}
		population.set(largestIndex,newChild2);
		
	}
	
	
	//find shortest path in population
	public void findShortestPathGenetic(){
		double dist = 0;
		boolean didUpdate = false;
		ArrayList<Point>temp = new ArrayList<>();
		for(TSP p : population){
			temp.clear();
			temp.addAll(p.points);
			temp.add(temp.get(0));
			dist = p.dist + distance(p.points.get(p.points.size()-1) , p.points.get(0));
			if(dist < minPath || minPath == 0){
				minPath = dist;
				minPathList.clear();
				minPathList.addAll(temp);
				didUpdate = true;
			}
		}
		if(didUpdate){
			// we only need to update when we find a new minPath
			map.setRoute(minPathList);
			map.update(map.getGraphics());
		}
	}
	
	
	public void generatePopulation(int size){
		for(int i = 0; i < size; i++){
			population.add(t.generatePermutation());
		}
	}
	
	public void getPopulationSize(){
		try{
			popSize = Integer.parseInt(PopulationSize.getText());
		}catch(NumberFormatException e){
			PopulationSize.setText("100");
			popSize = 100;
		}
	}
	
	public int getIterations(){
		try{
			return Integer.parseInt(iterations.getText());
		}catch(NumberFormatException e){
			iterations.setText("100");
			return 100;
		}
	}
	
	public void getGroupSize(){
		try{
			int temp = Integer.parseInt(GroupSize.getText());
			if(temp > popSize){
				GroupSize.setText("" + (popSize));
				groupSize = popSize;
			}else{
				groupSize = temp;
			}
			
		}catch(NumberFormatException e){
			GroupSize.setText("" + (popSize));
			groupSize = popSize;
		}
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
	

	public TSPFrame() {
		super("TSP GUI");
		map = new Map();
		population = new ArrayList<>();
		minPathList = new ArrayList<>();
		status = new JTextArea("Select a tsp file");
		
		status.setLineWrap(true);
		status.setEditable(false);
		status.setWrapStyleWord(true);
		setLayout(new BorderLayout());
		add(map,"Center");
		add(status,"South");
		
		JPanel panel = new JPanel();
		start = new JButton("Start");
		start.addActionListener(this);
		panel.add(start);
		JLabel popsizelabel = new JLabel("Population Size");
		PopulationSize = new JTextField("100",3);
		panel.add(popsizelabel);
		panel.add(PopulationSize);
		JLabel groupsizelabel = new JLabel("Group Size");
		panel.add(groupsizelabel);
		GroupSize = new JTextField("10",3);
		panel.add(GroupSize);
		JLabel interationsLabel = new JLabel("Iterations");
		panel.add(interationsLabel);
		iterations = new JTextField("100",3);
		panel.add(iterations);
		SingleCross = new JRadioButton("SingleCross");
		SubPaths = new JRadioButton("SubPaths");
		ButtonGroup bG = new ButtonGroup();
		bG.add(SingleCross);
		bG.add(SubPaths);
        panel.add(SingleCross);
        panel.add(SubPaths);
        SingleCross.setSelected(true);
		add(panel,"North");
	
		// adds menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);
		menuFile.add(menuItemExit);
		JMenuItem menuItemOpen = new JMenuItem("Open TSP");
		menuItemOpen.addActionListener(this);
		menuFile.add(menuItemOpen);

		menuBar.add(menuFile);
		
		// adds menu bar to the frame
		setJMenuBar(menuBar);

		// adds window event listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				int reply = JOptionPane.showConfirmDialog(TSPFrame.this,
						"Are you sure you want to quit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					dispose();
				} else {
					return;
				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TSPFrame();
			}
		});
	}
}
