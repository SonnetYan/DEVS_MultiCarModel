
package Homework2022.PredatorPrey.MYan2;

import genDevs.modeling.*;
import genDevs.plots.newCellGridPlot;

import java.io.*;
import java.util.Random;

import GenCol.doubleEnt;
import GenCol.intEnt;


/**
 * This class defines an GlobalRef that makes it easy to find a cell and its
 * information, such as current state of the cell, and the cell's reference
 *
 * @author  Xiaolin Hu
 * @Date: Sept. 2007
 */
public class GlobalRef {
  protected static int xDim;
  protected static int yDim;
  protected static GlobalRef _instance=null;
  protected static Random r = null;

  public String[][] state;
  public IODevs[][] cell_ref;
  
  public double grassReproduceT = 1.2 ; // change to the appropriate value for your model 
  public double sheepMoveT = 1.5; // change to the appropriate value for your model 
  public double sheepLifeT  = 6; // change to the appropriate value for your model 
  public double sheepReproduceT = 7.5; // change to the appropriate value for your model 
  
  
  public int totalSheep = 0;
  public double[] sheepBornTime = new double[20000];
  public double[] sheepDeadTime = new double[20000];
  public double TotalSheepLifeTimeSum = 0;
  public int totalDeadSheep = 0;
  
  
  private GlobalRef(){}  // construction function

  public static GlobalRef getInstance(){
    if(_instance!=null) return _instance;
    else {
      _instance = new GlobalRef();
      return _instance;
    }
  }

  public void setDim(int x, int y){
    xDim = x;
    yDim = y;
    state = new String[xDim][yDim];
    cell_ref = new IODevs[xDim][yDim];
  }
  
  
  public static Random getRand() {
	  if(r!=null) {
		  return r;
	  }else {
		  r = new Random(77777);
		  return r;
	  }
  }

}
