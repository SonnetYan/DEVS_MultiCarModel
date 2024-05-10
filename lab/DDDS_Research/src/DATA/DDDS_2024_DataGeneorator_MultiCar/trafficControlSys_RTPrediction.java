/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 08-15-02
 */


package DATA.DDDS_2024_DataGeneorator_MultiCar;

import simView.*;

import java.util.Random;

import genDevs.modeling.*;


public class trafficControlSys_RTPrediction extends ViewableDigraph{

	transducer_RTPrediction trand;

public trafficControlSys_RTPrediction(){
    this("trafficControlSys","eastMoving_passive", DevsInterface.INFINITY,0, 0, 0,
    		model_configData.carPassingTime_mean, new Random(model_configData.randSeed_IntersectionM));
}

public trafficControlSys_RTPrediction(String nm, String tfcState, double sgm, double remainGreenT, 
		int westSideQSize, int eastSideQSize, double carPassingTime, Random RTPrediction_rand){
    super(nm);

    ViewableAtomic generator_eastMoving = new generator_eastMovingTraffic("generator_eastMoving",model_configData.generator_eastMoving_lambda, RTPrediction_rand);
    ViewableAtomic generator_westMoving = new generator_westMovingTraffic("generator_westMoving",model_configData.generator_westMoving_lambda, RTPrediction_rand);
    trafficControlModel_RTPrediction IntersectionModel = new trafficControlModel_RTPrediction("trafficControlModel", tfcState, sgm, remainGreenT, 
    		westSideQSize, eastSideQSize, carPassingTime, RTPrediction_rand);

    trand = new transducer_RTPrediction();

    add(generator_eastMoving);
    add(generator_westMoving);
     add(IntersectionModel);
     add(trand);

     addCoupling(generator_eastMoving,"out",IntersectionModel,"eastMoving_in");
     addCoupling(generator_westMoving,"out",IntersectionModel,"westMoving_in");
     addCoupling(IntersectionModel,"eastMoving_out",trand,"eastMoving_out");
     addCoupling(IntersectionModel,"westMoving_out",trand,"westMoving_out");
     addCoupling(trand, "out", generator_eastMoving,"in"); // stop the vehicle generation 
     addCoupling(trand, "out", generator_westMoving,"in"); // stop the vehicle generation

}

}
