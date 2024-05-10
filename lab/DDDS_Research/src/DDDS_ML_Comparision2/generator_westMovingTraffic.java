/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 08-15-02
 */


package DDDS_ML_Comparision2;

import simView.*;


import java.lang.*;
import java.util.Random;

import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import util.*;
import statistics.*;

public class generator_westMovingTraffic extends ViewableAtomic{


  protected double int_gen_time;
  protected int count;
  protected Random r;
  double nextCarArriveTime_Lambda = 1.0/6; // 1 car per 6 seconds 
  
  boolean lambdaChanged=false;
  
  public generator_westMovingTraffic() {this("generator_westMoving");}

public generator_westMovingTraffic(String name){
   this(name, model_configData.generator_westMoving_lambda, new Random(model_configData.randSeed_westMoving));
}

public generator_westMovingTraffic(String name,double lambda, Random rd){
	   super(name);
	   addInport("in");
	   addOutport("out");

	   nextCarArriveTime_Lambda = lambda ;
	   r = rd;
	}

public void initialize(){
	   int_gen_time = this.getNextCarTime(nextCarArriveTime_Lambda);
	   //System.out.println(this.getName()+" time="+int_gen_time);
	   holdIn("active", int_gen_time);
	   count = 0;
}


public void  deltext(double e,message x){
Continue(e);
   for (int i=0; i< x.getLength();i++){
     if (messageOnPort(x, "in", i)) { //the stop message from tranducer
       passivate();
     }
   }
}


public void  deltint( ){
	 //System.out.println(this.getName()+" start deltint");

//	if(this.getSimulationTime()>(500) && this.getSimulationTime()<(800)) {
//		nextCarArriveTime_Lambda=1.2*model_configData.generator_westMoving_lambda;
//		lambdaChanged=true;
//	}
//	else {
//		nextCarArriveTime_Lambda=model_configData.generator_westMoving_lambda;		
//	}

if(phaseIs("active")){
   count = count +1;
   int_gen_time = this.getNextCarTime(nextCarArriveTime_Lambda);
   holdIn("active", int_gen_time);
}
else passivate();
}

public message  out(){
//System.out.println(name+" out count "+count);
   message  m = new message();
   content con = makeContent("out", new entity("westMovingCar_" + count));
   m.add(con);

  return m;
}

double getNextCarTime(double Lambda) {
	return getPoissonNextTime(Lambda);
}

//The following is taken from 
//https://preshing.com/20111007/how-to-generate-random-timings-for-a-poisson-process/
private double getPoissonNextTime(double lambda) {
	double u = r.nextDouble();
	double nextTime = -Math.log(u) / lambda;
	
	return nextTime;
}


}

