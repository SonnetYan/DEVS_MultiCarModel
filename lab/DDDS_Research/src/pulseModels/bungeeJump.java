package pulseModels;

import simView.*;
import genDevs.plots.*;
import java.awt.*;
import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import pulseExpFrames.*;

import java.util.*;

class oneWaySpringGen extends linearGen{
protected double length;

public double rateFn(){
if (input >= length)
return -coefficient*(input - length);
else return 0;
}

public oneWaySpringGen(String nm,double springConst,double quantum,double length){
super(nm,springConst,quantum);
this.length = length;
}
}

/////////////////////////////////


public class bungeeJump extends  ViewableDigraph{

public static void main (String[ ] args){
/*
TunableCoordinator c = new TunableCoordinator(new bungeeJump());
c.setTimeScale(.01);
c.initialize();
c.simulate(100000);
*/
}

public bungeeJump(){
super("bungeeJump");

addInport("in");
addOutport("out");

double quantum = .2;// .1;//1;

//different settings of quanta per generators are possible
//seems to need at least .2 overall otherwise doesn't settle into limit cycle
ViewableAtomic gravity = new varGen("gravity",1,2*quantum);//rate,quantum

ViewableAtomic airRes  = new  linearGen("airRes",-.05,.1*quantum);//coef of Res

ViewableAtomic velocity = new sum("velocity",0);//state
ViewableAtomic velocityGen = new varGen("velocityGen",0,2*quantum);
ViewableAtomic position = new sum("position",0);
ViewableAtomic oneWaySpringGen = new oneWaySpringGen("oneWaySpringGen",1,2*quantum,50);
                                               //coefficient,quantum,length

add(gravity);
add(airRes);
add(velocity);
add(velocityGen);
add(position);
add(oneWaySpringGen);


addCoupling(gravity,"out",velocity,"in");
addCoupling(airRes,"out",velocity,"in");
addCoupling(velocity,"out",airRes,"setInput");
addCoupling(velocity,"out",velocityGen,"setRate");
addCoupling(velocityGen,"out",position,"in");
addCoupling(position,"out",oneWaySpringGen,"setInput");
addCoupling(oneWaySpringGen,"out",velocity,"in");

CellGridPlot t = new CellGridPlot("Position Velocity PhasePlot",10,
                                       200,100);
add(t);
addCoupling(velocity,"out",t,"drawJ");
addCoupling(position,"out",t,"drawI");

CellGridPlot timeP = new CellGridPlot(name +" Time Plot",10,160);
timeP.setCellGridViewLocation(100,500);
timeP.setSpaceSize(100,40);
timeP.setCellSize(5);
timeP.setTimeScale(50);
add(timeP);
addCoupling(position,"out",timeP,"timePlot");
}


    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(732, 604);
        ((ViewableComponent)withName("velocity")).setPreferredLocation(new Point(273, 56));
        ((ViewableComponent)withName("position")).setPreferredLocation(new Point(279, 149));
        ((ViewableComponent)withName("airRes")).setPreferredLocation(new Point(30, 260));
        ((ViewableComponent)withName("gravity")).setPreferredLocation(new Point(37, 13));
        ((ViewableComponent)withName("oneWaySpringGen")).setPreferredLocation(new Point(20, 88));
        ((ViewableComponent)withName("velocityGen")).setPreferredLocation(new Point(18, 191));
    }
}
