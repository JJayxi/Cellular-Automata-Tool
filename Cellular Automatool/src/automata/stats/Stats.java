package automata.stats;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import util.MiscUtil;

/**
 * Class that collects the activity and the distribution of cells
 */
public class Stats {
    
    /**
     * Array that contains the activity for each time step the simulation went through
     */
    private ArrayList<Integer> activity = new ArrayList<>();
    /**
     * Array that contains the activity for each time step the simulation went through
     */
    private ArrayList<int[]> cellCounting = new ArrayList<>();
    
    
    
    //////////The following values are used to display only
    /**
     * The dimensions of the simulation
     */
    private final int gridWidth, gridHeight;
    /**
     * the number of states that the simulation has.
     */
    private final int numberOfStates;
    
    /**
     * create a new object to track the activity and cell distribution
     * @param gridWidth
     * @param gridHeight
     * @param numberOfStates 
     */
    public Stats(int gridWidth, int gridHeight, int numberOfStates) {
	this.gridWidth = gridWidth;
	this.gridHeight = gridHeight;
	this.numberOfStates = numberOfStates;
    }
    
    /**
     * add the values collected during one step
     * @param cellChange
     * @param cellCount 
     */
    public void addStepValues(int cellChange, int[] cellCount) {
	if(cellChange > maxActivity)maxActivity = cellChange;
	activity.add(cellChange);
	cellCounting.add(cellCount);
    }
    
    public void clear() {
	activity.clear();
	cellCounting.clear();
	maxActivity = 0;
    }
    
    
    public int getStepsCount() {
	return activity.size();
    }
    
    public static void saveToCSVFile(String filename, Stats stats){
	try(PrintWriter out = new PrintWriter(new FileWriter(filename))){
	    out.println("NUMBER_OF_CELLS");
	    out.println(stats.gridHeight * stats.gridWidth);
	    out.println("NUMBER_OF_STATES");
	    out.println(stats.numberOfStates);
	    out.println("ACTIVITY, STATESCOUNT");
	    for (int i = 0; i < stats.activity.size(); i++) {
		out.print(stats.activity.get(i));
		for (int j = 0; j < stats.numberOfStates; j++) {
		    out.print("," + stats.cellCounting.get(i)[j]);
		}
		out.println();
	    }	    
	    
	} catch(IOException e) {
	    System.err.println("Unable to save the stats");
	}
    }
    
    //value that tracks the maximum activity that happened. Necessary
    //to know the hight of the activity graph
    private int maxActivity = 0;
    /**
     * displays the activity graph
     * @param g the graphisc2D object of the JPanel
     * @param width the width in pixels
     * @param height the height in pixels
     */
    public void showActivity(Graphics2D g, int width, int height) {
	g.setColor(Color.white);
	g.fillRect(0, 0, width, height);
	g.setColor(Color.black);
	float dw = (float) width / activity.size();
	for(int i = 0; i < activity.size() - 1; i++) {
	    int h1 = height - (int)((float)activity.get(i) / maxActivity * height);
	    int h2 = height - (int)((float)activity.get(i + 1) / maxActivity * height);
	    g.drawLine((int)(dw * i), h1,(int)(dw * (i + 1)), h2);
	    
	}
    }
    
    /**
     * displays the distribution of cell graph
     * @param g the graphics2D object of the Jpanel
     * @param width the width in pixels
     * @param height the height in pixels
     */
    public void showCellCount(Graphics2D g, int width, int height) {
	    int[] polygonsX = new int[activity.size() * 2];
	    int[][] polygonsY = new int[numberOfStates][activity.size() * 2];
	    float dw = (float) width / activity.size();
	    
	    g.setColor(Color.white);
	    g.fillRect(0, 0, width, height);
	    
	    for(int i = 0; i < cellCounting.size(); i++) {
		polygonsX[i] = polygonsX[polygonsX.length - i - 1] = (int)(i * dw);
		
		float h = 0;
		for(int state = 0; state < polygonsY.length; state++) {
		    polygonsY[state][i] = (int)(h * height);
		    h += cellCounting.get(i)[state] / ((float)gridWidth * gridHeight);
		    polygonsY[state][polygonsX.length - 1 - i] = (int)(h * height);
		}
	    }
	    
	    for(int state = 0; state < polygonsY.length; state++) {
		g.setColor(MiscUtil.colorFromState(state, numberOfStates));
		g.fillPolygon(polygonsX, polygonsY[state], activity.size() * 2);
	    }
	    
    }
}