package DDDS_ML_Comparision2;


import GenCol.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import genDevs.simulation.realTime.*;


public class test{

protected static digraph testDig;

  public test(){}

  public static void main(String[ ] args)
  {
      testDig = new trafficControlSys_Real();
//      testDig = new trafficControlSys_RTPrediction();
      genDevs.simulation.coordinator cs = new genDevs.simulation.coordinator(testDig);

//      TunableCoordinator cs = new TunableCoordinator(testDig);
//      cs.setTimeScale(0.1);

      cs.initialize();
      //cs.simulate(5000);
      // Add extra 2000s  to ensure the Car-waiting time can be computed so that the prediction results can be compared
      cs.simulate_TN(PF_configData.numberofsteps*PF_configData.stepInterval+2000); 
      System.out.println("simulation finsihed");
  }
}
