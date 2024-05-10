/*
 */

package pulseModels;

import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;


public class shooter extends ViewableAtomic{
  protected double timeToShoot, timeToDie,hitEffect;
  protected String healthState; //healthy,dying, dead

  public shooter(){
    this("shooter",.4,.2); //timeToShoot includes travel time
  }


  public shooter(String nm,double timeToShoot,double hitEffect)
  {
    super(nm);
    this.timeToShoot = timeToShoot;
    this.hitEffect = hitEffect;

    addInport("orderToShoot");
    addInport("getHit");
    addInport("getDead");
    addOutport("outShot");
    addPortTestInput("orderToShoot");
    addPortTestInput("orderToShoot",.1);
    addPortTestInput("getHit");
    addPortTestInput("getHit",.1);
    addPortTestInput("getDead");
    addPortTestInput("getDead",.1);
    addPortTestInput("getDead",1);
   }

  public void initialize(){
    super.initialize();
    healthState = "alive";
    passivate();
  }


  public void deltext(double e,message x){
    Continue(e);

  if (somethingOnPort(x,"getHit") && healthState.equals("alive")){
      healthState = "dying";
       if (phaseIs("shooting"))
         sigma = sigma+hitEffect;
     }
else  if (somethingOnPort(x,"getDead")){
          healthState = "dead";
          if (phaseIs("shooting"))
           passivate();
          }
  if (phaseIs("passive") && !healthState.equals("dead")
          && somethingOnPort(x,"orderToShoot"))
      holdIn("shooting",timeToShoot+(
            !healthState.equals("alive")?hitEffect:0));
}

  public void   deltint(){
    if (phaseIs("shooting"))
       passivateIn("hasShot");
      }


  public message out(){
    if (phaseIs("shooting"))
      return  outputNameOnPort("shot","outShot");
   else return new message();
  }

  public String getTooltipText(){
  return
    super.getTooltipText()
     + "\n"+"healthState: "+healthState;
}


}
