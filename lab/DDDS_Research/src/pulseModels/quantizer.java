/*      Copyright 1999 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA2.6
 *  Date       : 04-15-00
 */
package pulseModels;

import simView.*;

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

public class quantizer extends realDevs{
protected double store,nextVal,duration,quantum;
protected boolean start = true;

  public quantizer(){
    this("quantizer",1);
}

public quantizer(String name,double quantum){
   super(name);
   this.quantum = quantum;
   addInport("in");
   addOutport("out");
   addOutport("change");
   addRealTestInput("in",5.6,0);
   addRealTestInput("in",5.7,10);
   addRealTestInput("in",6.7,20);
}

public void initialize(){
     passivate();
     store = 0;
     duration = 0;
     start=true;
     super.initialize();
 }

 public int change(double d, double f){
 if (Math.abs(d - f) > quantum)
 System.out.println("CONTINUITY VIOLATION: "+ d + " "+ f);
 return (int)(Math.floor(d/quantum) - Math.floor(f/quantum));
 }




public void  deltext(double e,message x){
    Continue(e);
    duration = duration + e;
 if (phaseIs("passive"))
      if (somethingOnPort(x,"in")){
      double d = getRealValueOnPort(x,"in");
      nextVal = quantum*Math.floor(d/quantum);
      if (change(nextVal,store)!= 0)
      holdIn("output", 0);
      else passivate();
      if (start){ //only for initial input
      store = nextVal;
      passivate();
      start = false;
      }
    }
}

public void  deltint( ){
    store = nextVal;
    duration = 0;
    passivate();
}


public message    out( )
{
return outputRealOnPort(change(nextVal,store),"out");
}


 public void showState(){
  super.showState();
  System.out.println("store: " + store);
 }

public String getTooltipText(){
   return
  super.getTooltipText()
  +"\n"+"store :"+ store
   +"\n"+"duration :"+ duration;
  }


}

