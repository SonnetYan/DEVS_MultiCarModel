/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 


package SimpArc;

import simView.*;

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

public class generator extends siso{
protected double period;

  public generator(){
    super("generator");
    period = 30;
}

public generator(String name,double Period){
   super(name);
   period = Period;
}

public void initialize(){
     phase = "active";
     sigma = period;
     super.initialize();
 }


public void  deltint( ){
    holdIn("active",period);
    showState();
}


public double sisoOut(){
    return 1;
}

 public void showState(){
  super.showState();
  System.out.println("period: " + period);
 }
}

