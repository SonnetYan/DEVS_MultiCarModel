package pulseModels;

import simView.*;

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

import java.util.*;

public class realDevs extends  ViewableAtomic{
protected double realVar;

public realDevs(String nm){
super(nm);
addInport("in");
addInport("stop");
addOutport("out");
addRealTestInput("in",10);
addRealTestInput("in",10,5);
    addRealTestInput("in",-10);
    addRealTestInput("in",0,5);
}

public realDevs(){
this("realDevs");
}

public static int signOf(double x){
    if (x == 0) return 0;
    else if (x > 0) return 1;
    else return -1;
}
public static double inv(double x){
    if (x == 0) return Double.POSITIVE_INFINITY;
    else if (x >= Double.POSITIVE_INFINITY ) return 0;
    else return 1/x;
}




public void initialize(){
realVar = 0;
super.initialize();
passivate();
}


public void deltext(double e,message x){
Continue(e);
if (somethingOnPort(x,"stop")) passivate();
else
if (phaseIs("passive")&& somethingOnPort(x,"in")){
realVar = getRealValueOnPort(x,"in");
if (signOf(realVar) ==1)
realVar = inv(realVar);
holdIn("output",0);
}
}

public void deltint(){
if (phaseIs("output"))
passivate();
}

public void deltcon(double e,message x){
   deltint();
   deltext(0,x);
}

public message out(){
if (phaseIs("output"))
return outputRealOnPort(realVar,"out");
else return new message();
}


public String getTooltipText(){
   return
  super.getTooltipText()
  +"\n"+"realVar :"+ realVar;
  }

public static void main(String args[]){
realDevs re = new  realDevs("real");
re.initialize();
atomicSimulator s = new atomicSimulator(re);
s.simInject(0,"in",new doubleEnt(10));
s.simulate(2);
//should print
//Time: 0.0 ,input injected:
//port: in value: 10.0
//port: out value: 10.0
//Terminated Normally at ITERATION 2 ,time: Infinity

}

}





