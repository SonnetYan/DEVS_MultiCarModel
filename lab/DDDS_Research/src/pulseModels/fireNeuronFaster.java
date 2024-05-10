
package pulseModels;

import simView.*;

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;



public class fireNeuronFaster extends fireOnceNeuron{

public fireNeuronFaster(){
this("fireNeuronFaster",10);
}

public fireNeuronFaster(String nm,double fireDelay){
super(nm,fireDelay);
}

public void deltext(double e,message x){
Continue(e);
if (phaseIs("receptive")&& somethingOnPort(x,"in"))
holdIn("fire",fireDelay);
else if (phaseIs("fire")&& somethingOnPort(x,"in"))
holdIn("fire",sigma/2); //note this is the sigma at time of second pulse
}

}