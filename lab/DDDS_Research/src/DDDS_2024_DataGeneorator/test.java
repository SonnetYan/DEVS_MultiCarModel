package DDDS_2024_DataGeneorator;


import java.io.PrintWriter;
import java.util.Random;

import genDevs.modeling.*;


public class test{

protected static digraph testDig;

  public test(){}
  

  public static void main(String[ ] args)
  {


    	String workingDir = System.getProperty("user.dir");
    	System.out.println("current workspace : " + workingDir);

	    int run = model_configData.total_run; // Default run is 1
	    if(args.length > 0) {
        try {
            // Parse the first argument to integer and assign it to run
            run = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Argument must be an integer");
            System.exit(1);
        }
	  }

	    PrintWriter Real_TotalInfo = null;

	  
	  long seed = 42;  
	  Random rand = new Random(seed);
	  
	   for(int i = 0; i < run; i++) {
		  
	 	  model_configData.randSeed_eastMoving = rand.nextInt();
	 	  model_configData.randSeed_westMoving = rand.nextInt();
	 	  model_configData.randSeed_IntersectionM = rand.nextInt();
		  
		  testDig = new trafficControlSys_Real();
		  genDevs.simulation.coordinator cs = new genDevs.simulation.coordinator(testDig);
		  cs.initialize();
		  cs.simulate_TN(PF_configData.numberofsteps*PF_configData.stepInterval+2000); 
		  System.out.println(" simulation finished with carPassingTime = " + model_configData.carPassingTime_mean);
		  System.out.println("Max Elapse Time When Busy E2W = " + model_configData.maxElapseTimeWhenBusy_E2W);
		  System.out.println("Max Elapse Time When Busy W2E = " + model_configData.maxElapseTimeWhenBusy_W2E);
		  System.out.println("Generator East Moving Lambda = " +"1/" + 1/model_configData.generator_eastMoving_lambda);
		  System.out.println("Generator West Moving Lambda = " +"1/" +1/model_configData.generator_westMoving_lambda);

		  model_configData.run++;
	   }
    }
}
 

