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

public class binaryCounter extends siso{
  double count;

  public binaryCounter(){
    super("binaryCounter");
    AddTestPortValue(1);
}

public binaryCounter(String name){
   super(name);
}

public void initialize(){
     count  = 0;
     super.initialize();
 }


public void  Deltext(double e,double input){
 Continue(e);
    count = count + (int)input;
    if (count >= 2){
    count = 0;
    holdIn("active",10);
}

}



public void  deltint( ){
passivate();
}


public double sisoOut(){
    if (phaseIs("active"))
       return 1;
     else return 0;
}

 public void showState(){
  super.showState();
System.out.println("count: " + count);
 }



}

