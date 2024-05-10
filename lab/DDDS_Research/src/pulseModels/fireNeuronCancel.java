
package pulseModels;
import simView.*;

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;



public class fireNeuronCancel extends fireOnceNeuron{

public fireNeuronCancel(){
this("fireNeuronCancel",10);
}

public fireNeuronCancel(String nm,double fireDelay){
super(nm,fireDelay);
}

public void deltext(double e,message x){
Continue(e);
if (phaseIs("receptive")&& somethingOnPort(x,"in"))
holdIn("fire",fireDelay);
else if (phaseIs("fire")&& somethingOnPort(x,"in"))
passivateIn("refract");
}


}