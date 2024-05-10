package DDDS_2024_ParameterEstimate;

import GenCol.*;
import genDevs.modeling.DevsInterface;

public class stateEntity extends entity{
  protected String TrafficLightState; 
  protected double sigma;
  protected double elapseTimeInGreen;
  protected int westSideQueue, eastSideQueue;
  
  //For parameter estimation 
  protected double carPassingTime, lightSwitchTime_E2W,lightSwitchTime_W2E;
  
  
  public stateEntity(){
	  this(0, 0, "eastMovingGreen_passive", DevsInterface.INFINITY, 0, model_configData.pf_carPassingTime_mean,model_configData.pf_maxElapseTimeWhenBusy_E2W,model_configData.pf_maxElapseTimeWhenBusy_W2E);
  }
  
  public stateEntity(int Wq, int Eq, String GreenLtSt, double sgm, double elapseTimeGreen, double carPTime,double lSTime_E2W,double lSTime_W2E){
	  super("stateEntity");
	  westSideQueue = Wq;
	  eastSideQueue = Eq;
	  TrafficLightState = GreenLtSt;
	  sigma = sgm;
	  elapseTimeInGreen = elapseTimeGreen;
	  carPassingTime= carPTime;
    lightSwitchTime_E2W = lSTime_E2W;
    lightSwitchTime_W2E = lSTime_W2E;


  }
  
  public String toString(){
	  //return name+"_"+processingTime;
	  return westSideQueue+"_"+eastSideQueue+"_"+TrafficLightState
			  +"_"+sigma+"_"+elapseTimeInGreen+"_"+carPassingTime;
//			  ((double)((int)(elapseTimeInGreen*1000)))/1000;
  }
		
}
