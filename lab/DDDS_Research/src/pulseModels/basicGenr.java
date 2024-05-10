package pulseModels;

import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;


public class basicGenr extends ViewableAtomic{
protected double firstDuration, secondDuration, thirdDuration;
protected double firstOutput, secondOutput, thirdOutput;

public basicGenr(){
this("basicGenr",1,2,3,3,2,1);
}


public basicGenr(String nm,
    double firstDuration, double secondDuration,double thirdDuration,
    double firstOutput, double secondOutput,double thirdOutput){
super(nm);
this.firstDuration = firstDuration;
this.secondDuration = secondDuration;
this.thirdDuration = thirdDuration;
this.firstOutput = firstOutput;
this.secondOutput = secondOutput;
this.thirdOutput = thirdOutput;

addInport("start");
addInport("stop");
addOutport("out");
addPortTestInput("start");
addPortTestInput("stop");
}


public void initialize(){
super.initialize();
holdIn("active",0);
}


public void deltext(double e,message x){
Continue(e);

if (somethingOnPort(x,"start"))
  holdIn("first",firstDuration);
else if (somethingOnPort(x,"stop"))
  passivate();
}

public void   deltint(){
if (phaseIs("active"))
holdIn("first",firstDuration);
else if (phaseIs("first"))
holdIn("second",secondDuration);
else if (phaseIs("second"))
holdIn("third",thirdDuration);
else //if (phaseIs("third"))
passivate();
}

public message out(){
if (phaseIs("active"))
return outputRealOnPort(firstOutput,"out");
else if (phaseIs("first"))
return outputRealOnPort(secondOutput,"out");
else if (phaseIs("second"))
return outputRealOnPort(thirdOutput,"out");
else //if (phaseIs("third"))
return outputRealOnPort(0,"out");
}

}
