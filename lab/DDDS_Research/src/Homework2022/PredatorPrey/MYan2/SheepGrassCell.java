package Homework2022.PredatorPrey.MYan2;

import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import genDevs.plots.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.text.*;
import java.time.Clock;
import java.io.*;

import statistics.*;
import quantization.*;
import twoDCellSpace.TwoDimCell;

public class SheepGrassCell extends TwoDimCell {

    String initStatus = "";
    final double grassReproduceT = GlobalRef.getInstance().grassReproduceT;
    final double sheepReproduceT = GlobalRef.getInstance().sheepReproduceT;
    final double sheepLifeT = GlobalRef.getInstance().sheepLifeT;
    final double sheepMoveT = GlobalRef.getInstance().sheepMoveT;
    double leftGrassReproduceT = sheepReproduceT;
    public static final String grass="grass", empty = "empty", sheep = "sheep";
    private List<String> outDirect = Arrays.asList("outN","outNE","outE","outSE","outS","outSW", "outW","outNW");
    double leftSheepLifeT = sheepLifeT;
    double leftSheepMoveT = sheepMoveT;
    private double leftSheepReproduceT;
    private double minNextStepTime;
    
    
    private sheepGreassEntity rawItem = null;
    private sheepGreassEntity item = null;
    private Pair itemProp = null;
    private String moveDir;
    private Double inLeftLT = null; //when pass a sheep here the left life time
    private Double inLeftRT = null; //when pass a sheep here the left reproduce time
    
	private int curSheepNum = 0;
    
	private String  die_born_move = "die";
	
    
    final String[] INPortArray = {"inN", "inNE", "inE", "inSE", "inS", "inSW", "inW", "inNW"};
   

    public SheepGrassCell() {
        this(0, 0);
    }

    public SheepGrassCell(int xcoord, int ycoord) {
        super(new Pair(xcoord, ycoord));
        this.initStatus = GlobalRef.getInstance().state[xcoord][ycoord];
    }

    public void initialize() {
        super.initialize();
        if (initStatus == "empty") {
            passivateIn("empty");
        }
        else if (initStatus == "grass") {
            holdIn("toGrass", 0);
            leftGrassReproduceT = grassReproduceT;
            System.out.println("set a grass in "+ xcoord+","+ycoord);
        }
        else if (initStatus == "sheep") {
        	leftSheepMoveT = sheepMoveT;
        	leftSheepLifeT = sheepLifeT;
        	leftSheepReproduceT = sheepReproduceT;
        	minNextStepTime = getMin(leftSheepMoveT, leftSheepLifeT, leftSheepReproduceT);
            holdIn("toSheep", 0);
            
            //int newsheepNum = updateAndGetSheepTotalNum();
            //System.out.println("set a sheep-"+ newsheepNum+"in (" + xcoord + "," + ycoord+") at clock " + this.getSimulationTime() );
            
        }
    }

    
    //   !!!Change the global REF AT FIRST!!!!!
    public void deltint() {
    	
  	  leftGrassReproduceT -= sigma;
  	  leftSheepMoveT -= sigma;
  	  leftSheepLifeT -= sigma;
  	  leftSheepReproduceT -= sigma;
  	  
  	  minNextStepTime = getMin(leftSheepMoveT, leftSheepLifeT, leftSheepReproduceT);
        if (phaseIs(empty)) {
            passivateIn(empty);
        } 
        else if (phaseIs("toGrass")) {
        	updateCurrentGlobalRef(grass);
            holdIn(grass, grassReproduceT);
        } 
        else if (phaseIs(grass)) {
        	leftGrassReproduceT = grassReproduceT;
            holdIn(grass, grassReproduceT);
        } 
        else if (phaseIs("toSheep")) {
        	updateCurrentGlobalRef(sheep);
        	// System.out.println("tosheep a sheep in "+ xcoord+","+ycoord);
        	holdIn(sheep, minNextStepTime);
        }
        else if(phaseIs(sheep)){
        	if(minNextStepTime<=0){
        		holdIn("sheepStateChange",0);
        	}
        }
        else if(phaseIs("sheepStateChange"))
        {
        	if(die_born_move == "born")
        	{
        		 holdIn("sheep",minNextStepTime);
        	}else {
        		//die or move, cell all be empty
        		updateCurrentGlobalRef(empty);
        		passivateIn(empty);
        	}
        }
        
    }

    public void deltext(double e, message x) {

  	  Continue(e);

  	  leftGrassReproduceT -= e;
  	  leftSheepMoveT -= e;
  	  leftSheepLifeT -= e;
  	  leftSheepReproduceT -= e;
  	  
  	 
  	  if(phaseIs("grass")) {
  		  for (int i=0; i< x.getLength();i++){  // 1
  			for(int j = 0; j < 8; j++) {
  			   if (messageOnPort(x, INPortArray[j], i)) {
  				   item = (sheepGreassEntity)x.getValOnPort(INPortArray[j], i);
  				   if(item.getName().equals("grass")) {
  					   System.out.println("Wrong EXT! GRASS GOT GRASS");
  				   }
  				   if(item.getName().equals("sheep")) {
  					   // sheep comes to eat grass
//  					   inLeftRT = (Double) itemProp.getValue();
  					   inLeftRT = item.getLeftReproduceT();
  					   curSheepNum = item.getsheepNum();
  					   
  					   
  					   leftGrassReproduceT = -1;
  					   leftSheepMoveT = sheepMoveT;   //arrive new place,reset move time
  					   leftSheepLifeT = sheepLifeT;
  					   leftSheepReproduceT = inLeftRT;
  					   
  					   holdIn("toSheep", 0);
  				   }
  			   }
  		  }
  		   }
  	  }
  	  
  	  if (phaseIs("empty")) {
  		  for (int i=0; i< x.getLength();i++){ 
  			  for(int j = 0; j < 8; j++) {
  			   if (messageOnPort(x,  INPortArray[j], i)) {
  				 item = (sheepGreassEntity)x.getValOnPort(INPortArray[j], i);
  				   
  				   if(item.getName().equals("sheep")) {
//  					   inLeftLT = (Double) itemProp.getKey();
//  					   inLeftRT = (Double) itemProp.getValue();
  					  inLeftLT = item.getLeftLifeTime();
  					  inLeftRT = item.getLeftReproduceT();
  					  curSheepNum = item.getsheepNum();
  					   
  					   leftGrassReproduceT = -1;
  					   leftSheepMoveT = sheepMoveT;
  					   leftSheepLifeT = inLeftLT;
  					   leftSheepReproduceT = inLeftRT;

  					   holdIn("toSheep", 0);
  				   }
  				   if(item.getName().equals("grass")) {
  					   leftGrassReproduceT = grassReproduceT;
  					   holdIn("toGrass", 0);
  				   }
  				   
  			   }
  		  	}
  		   }
  	  }

  	  
      
    }

    public message out() {
        message m = super.out();
        if (phaseIs("toGrass")) {
            m.add(makeContent("outDraw", new DrawCellEntity("drawCellToScale",
                    x_pos, y_pos, Color.green, Color.green)));    
        } else if (phaseIs("grass")) {
          int tmpDir = findEmpty();
  		  if(tmpDir != -1) {
  			  moveDir = outDirect.get(tmpDir);
  			  int[] distXY = getNeighborXYCoord(this, tmpDir);
  			  GlobalRef.getInstance().state[distXY[0]][distXY[1]] = "grass";
  			//  m.add(makeContent(moveDir, new Pair(new entity("grass"), new Pair(0, 0))));
  			  m.add(makeContent(moveDir, new sheepGreassEntity(grass,0,true,0,0,0)));   //NEW SHEEP BORN ,NUMBER = total +1
  		    	
  		  }
        } else if (phaseIs("toSheep")) {
            m.add(makeContent("outDraw", new DrawCellEntity("drawCellToScale",
                    x_pos, y_pos, Color.red, Color.red)));
        }
        else if(phaseIs("sheepStateChange")){

        	minNextStepTime = getMin(leftSheepMoveT, leftSheepLifeT, leftSheepReproduceT);
    		  if(minNextStepTime == leftSheepLifeT) {
    			  //sheep dead
    			  //System.out.println("sheep Dies");
    			  leftSheepMoveT=0; leftSheepLifeT = 0; leftSheepReproduceT = 0;
    			  die_born_move = "die";
    	    	  m.add(makeContent("outDraw", new DrawCellEntity("drawCellToScale",
    	    	              x_pos, y_pos, Color.white, Color.white)));
    	    	  double curSheepLifeTime = updateDeadTimeAndGetLifeTime(curSheepNum, this.getSimulationTime());
    	           System.out.println("sheep-"+curSheepNum + " dead in (" + xcoord +","+ ycoord + ") at clock " + this.getSimulationTime() + " and total life time " + curSheepLifeTime);
    	    	  //System.out.println("sheep-"+curSheepNum + " dead in (" + xcoord +","+ ycoord + ") at clock " + this.getSimulationTime() );
    	    	  
    	           //CALCULATE THE DEAD SHEEP AVG LIFE TIME
    	           GlobalRef.getInstance().totalDeadSheep++;
    	           
    	          System.out.println("TOTAL SHEEP LIFE TIME is "+ GlobalRef.getInstance().TotalSheepLifeTimeSum + " AVG SHEEP LIFE TIME IS " + GlobalRef.getInstance().TotalSheepLifeTimeSum / GlobalRef.getInstance().totalDeadSheep);
    		  }else{
    			  if(leftSheepReproduceT == minNextStepTime) {
    				  leftSheepReproduceT = sheepReproduceT;
    				  die_born_move = "born";

    				  int tmpDir = findGrass();
    				  if(tmpDir == -1) {   //wihout grass

    					  tmpDir = findEmpty();
    					  if(tmpDir == -1) {  //wihout empty

    						  System.out.println("Unable to birth");
    					  }else {
    						  moveDir = outDirect.get(tmpDir);
    						  int[] distXY = getNeighborXYCoord(this, tmpDir);
    						  GlobalRef.getInstance().state[distXY[0]][distXY[1]] = "sheep";  
    						  
//    			    	      m.add(makeContent(moveDir, new Pair(new entity("sheep"), new Pair(new Double(sheepLifeT), new Double(sheepReproduceT)))));					  
    						  

    						  int newSheepNum = updateAndGetSheepTotalNum();
    						  System.out.println("sheep-" + newSheepNum+" born in empty (" +distXY[0] +","+distXY[1] + ") at clock " + this.getSimulationTime());
    						  //update cur sheep born time 
    						  updateBornTime(newSheepNum, this.getSimulationTime());
    						  m.add(makeContent(moveDir, new sheepGreassEntity(sheep,newSheepNum,true,sheepLifeT,sheepMoveT,sheepReproduceT)));   //NEW SHEEP BORN ,NUMBER = total +1, SEND NEW SHEEP NUM
    						  
    					  }
    				  }else {
    					  //if some grass nearby
    					  
    					  moveDir = outDirect.get(tmpDir);
    					  int[] distXY = getNeighborXYCoord(this, tmpDir);
    					  GlobalRef.getInstance().state[distXY[0]][distXY[1]] = "sheep";
    					  
    					  
//    					  System.out.println("giving birth to grass  in" +distXY[0] +" "+distXY[1]);
    					  int newSheepNum = updateAndGetSheepTotalNum();
    					  //System.out.println("sheep-" + newSheepNum+" born in grass " +distXY[0] +","+distXY[1]);
    					  System.out.println("sheep-" + newSheepNum+" born in grass (" +distXY[0] +","+distXY[1] + ") at clock " + this.getSimulationTime());
    					  //update cur sheep born time
    					  updateBornTime(newSheepNum, this.getSimulationTime());
						  m.add(makeContent(moveDir, new sheepGreassEntity(sheep,newSheepNum,true,sheepLifeT,sheepMoveT,sheepReproduceT)));   //NEW SHEEP BORN ,NUMBER = total +1, SEND NEW SHEEP NUM
						  //do not update cursheepnum, 

    				  }
    			  }
    			  if (minNextStepTime == leftSheepMoveT ) {
    				  //time to move
    				  leftSheepMoveT = sheepMoveT;
    				  
    				  int tmpDir = findGrass();
    				  //first find grass
    				  if(tmpDir == -1) {
    					  // if no grass cells nearby find empty
    					  tmpDir = findEmpty();
    					  if(tmpDir == -1) {
    						  //neither grass &empty
    						  //System.out.println("Unable to move");
    						  die_born_move = "born";
	
    					  }else {
    						  // move and left an empty cell
    						  die_born_move = "move";
    						  
    						  moveDir = outDirect.get(tmpDir);
    						  int[] distXY = getNeighborXYCoord(this, tmpDir);
    						  GlobalRef.getInstance().state[distXY[0]][distXY[1]] = "sheep";
    				    	  m.add(makeContent("outDraw", new DrawCellEntity("drawCellToScale",
    				    			  x_pos, y_pos, Color.white, Color.white)));
    				    	//  m.add(makeContent(moveDir, new Pair(new entity("sheep"), new Pair(new Double(leftSheepLifeT), new Double(leftSheepReproduceT)))));
    				    	  m.add(makeContent(moveDir, new sheepGreassEntity(sheep  ,curSheepNum,true,leftSheepLifeT,sheepMoveT,leftSheepReproduceT)));   //SEND CUR SHEEP NUM

    						  
    					  }
    				  }else {
    					  //with grass
    					  die_born_move = "move"; 
    					  
    					  moveDir = outDirect.get(tmpDir);
    					  int[] distXY = getNeighborXYCoord(this, tmpDir);
    					  GlobalRef.getInstance().state[distXY[0]][distXY[1]] = "sheep";
    					  m.add(makeContent("outDraw", new DrawCellEntity("drawCellToScale",
    			    			  x_pos, y_pos, Color.white, Color.white)));
    			    	 // m.add(makeContent(moveDir, new Pair(new entity("sheep"), new Pair(new Double(sheepLifeT), new Double(leftSheepReproduceT)))));
    					  m.add(makeContent(moveDir, new sheepGreassEntity(sheep  ,curSheepNum,true,leftSheepLifeT,sheepMoveT,leftSheepReproduceT)));     //SEND CUR SHEEP NUM
    				  }
    			  }
    			  
    		    }
    		  }

        return m;
    }
    
    ///tool functions 

    private void updateCurrentGlobalRef(String state){
    	GlobalRef.getInstance().state[xcoord][ycoord] = state;
    }

    private int updateAndGetSheepTotalNum(){
    	GlobalRef.getInstance().totalSheep++;
    	return GlobalRef.getInstance().totalSheep;
    }
    public void deltcon(double e, message x) {
        deltint();
    }
    public void updateBornTime(int sheepNum,double bornClock){
    	GlobalRef.getInstance().sheepBornTime[sheepNum] = bornClock;
    }
    public double updateDeadTimeAndGetLifeTime(int sheepNum,double deadClock)
    {
    	GlobalRef.getInstance().sheepDeadTime[sheepNum] = deadClock;
    	double lifeTime = deadClock - GlobalRef.getInstance().sheepBornTime[sheepNum] ;
    	GlobalRef.getInstance().TotalSheepLifeTimeSum += lifeTime;
    	return lifeTime;
    }

    public double getMin(double a,double b, double c)
    {
    	return Math.min(a,Math.min(b, c));
    }
    public int[] getNeighborXYCoord(TwoDimCell myCell, int direction )
    {
        int[] myneighbor = new int[2];
        int tempXplus1 = myCell.getXcoord()+1;
        int tempXminus1 = myCell.getXcoord()-1;
        int tempYplus1 = myCell.getYcoord()+1;
        int tempYminus1 = myCell.getYcoord()-1;

        if( tempXplus1 >= xDimCellspace)
            tempXplus1 = 0;

        if( tempXminus1 < 0 )
            tempXminus1 = xDimCellspace-1;

        if( tempYplus1 >= yDimCellspace)
            tempYplus1 = 0;

        if( tempYminus1 < 0 )
            tempYminus1 = yDimCellspace-1;

        // N
         
        if( (direction == 0) )
        {
            myneighbor[0] = myCell.getXcoord();
            myneighbor[1] = tempYplus1;
        }
        // NE
        else if( (direction == 1) )
        {
            myneighbor[0] = tempXplus1;
            myneighbor[1] = tempYplus1;
        }
        // E
        else if( (direction == 2) )
        {
            myneighbor[0] = tempXplus1;
            myneighbor[1] = myCell.getYcoord();
        }
        // SE
        else if( (direction == 3) )
        {
            myneighbor[0] = tempXplus1;
            myneighbor[1] = tempYminus1;
        }
        // S
        else if( (direction == 4) )
        {
            myneighbor[0] = myCell.getXcoord();
            myneighbor[1] = tempYminus1;
        }
        // SW
        else if( (direction == 5) )
        {
            myneighbor[0] = tempXminus1;
            myneighbor[1] = tempYminus1;
        }
        // W
        else if( (direction == 6) )
        {
            myneighbor[0] = tempXminus1;
            myneighbor[1] = myCell.getYcoord();
        }
        // NW
        //( (direction == 7) )
        else
        {
            myneighbor[0] = tempXminus1;
            myneighbor[1] = tempYplus1;
        }
        return myneighbor;
    }
    
    private int findGrass() {
  	  List<Integer> candidate = new ArrayList<Integer>();
  	  for(int i = 0; i < 8; i ++) {
  		  int[] tmp = getNeighborXYCoord(this, i);
  		  if(GlobalRef.getInstance().state[tmp[0]][tmp[1]].equals("grass")) {
  			  candidate.add(i);
  		  }
  	  }
  	  
  	  if(candidate.isEmpty()) {
  		  //if no grass near this cell
  		  return -1;
  	  }else {
  		  Random r = GlobalRef.getRand();
  		  return candidate.get(r.nextInt(candidate.size())).intValue();
  	  }
  	  
    }
    
    private int findEmpty() {
  	  List<Integer> candidate = new ArrayList<Integer>();
  	  for(int i = 0; i < 8; i ++) {
  		  int[] tmp = getNeighborXYCoord(this, i);
  		  if(GlobalRef.getInstance().state[tmp[0]][tmp[1]].equals("empty")) {
  			  candidate.add(new Integer(i));
  		  }
  	  }
  	  
  	  if(candidate.isEmpty()) {
  		  //if no empty part near this cell
  		  return -1;
  	  }else {
  		  Random r = GlobalRef.getRand();
  		  return candidate.get(r.nextInt(candidate.size())).intValue();
  	  }
    }
}