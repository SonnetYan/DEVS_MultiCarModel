package quantization;



import java.lang.*;
import java.awt.*;
import java.util.*;


import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import simView.*;
import util.*;
import genDevs.plots.*;
import pulseModels.*;



public class oneDint extends realDevs{

protected double state, input,quantum,initialState;//q,x,D
protected double clock;
protected int level,lastLevel; //n,ln
public double eps = 0;//10./100; //choice could be max input / 100;

public oneDint(String  name, double quantum, double initialState){
super(name);
this.quantum = quantum;
this.initialState = initialState;
addOutport("outNeg");
}

public oneDint(){
this("oneDint",1,0);
}

public static int positive(double x){
     if (x > 0) return 1;
    else return  0;
}

public  double equivInputZero(double x){
if (Math.abs(x) <= eps)
 return 0;
else return x;
}

public void initialize(){
super.initialize();
state = initialState;
level = (int)Math.floor(state/quantum);
holdIn(""+state,0);
input = 0;
clock = 0;
}


public double timeAdvance(){
if ((level+1)*quantum - state > 0 && input > 0)
return ((level+1)*quantum - state)/input;
if (state - level*quantum > 0 && input < 0)
return (state - level*quantum)/-input;
else if (input != 0)
return quantum/Math.abs(input);
else return  INFINITY;
}

public double nextState(){
return (level + positive(input))*quantum;
}

public void deltint(){
clock+=sigma;
lastLevel = level;
state = nextState();
level = level + signOf(input);
holdIn(""+state,timeAdvance());
}

public void  deltext(double e,message   x)
{
clock+=e;
state += input*e;
if (state <level*quantum) //gone below current level
    level = level - 1;
input = equivInputZero(sumValuesOnPort(x,"in"));
holdIn(""+state,timeAdvance());
}

public void  deltcon(double e,message   x)
{
deltint();
input = equivInputZero(sumValuesOnPort(x,"in"));
holdIn(""+state,timeAdvance());
}


public message    out( )
{
message   m = new message();
if (clock <= 0){
m.add(makeContent("out",  new doubleEnt(state)));
m.add(makeContent("outNeg",  new doubleEnt(-state)));
}
else if (level != lastLevel){
m.add(makeContent("out",  new doubleEnt(nextState())));
m.add(makeContent("outNeg",  new doubleEnt(-nextState())));
}
return m;
}


public String getTooltipText(){
   return
  super.getTooltipText()
  +"\n"+"quantum :"+ quantum
  +"\n"+"state :"+ state
   +"\n"+"input :"+ input
    +"\n"+"level :"+ level
      +"\n"+"lastLevel :"+ lastLevel;
}
}
