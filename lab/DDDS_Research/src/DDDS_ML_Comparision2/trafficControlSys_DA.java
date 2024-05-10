/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 08-15-02
 */


package DDDS_ML_Comparision2;

import simView.*;

import java.awt.*;
import java.io.*;
import java.util.Random;

import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;


public class trafficControlSys_DA extends ViewableDigraph{

public transducer_DA trand;

public trafficControlSys_DA(){
    this("trafficControlSys", model_configData.generator_eastMoving_lambda, new Random(model_configData.randSeed_eastMoving),
    		model_configData.generator_westMoving_lambda, new Random(model_configData.randSeed_westMoving),
    		"eastMoving_passive", DevsInterface.INFINITY,0, 0, 0,
    		model_configData.carPassingTime_mean, new Random(model_configData.randSeed_IntersectionM));
}

public trafficControlSys_DA(String nm, double eastMoving_lambda, Random eastMoving_rand, double westMoving_lambda, Random westMoving_rand,
		String tfcState, double sgm, double remainGreenT, int westSideQSize, int eastSideQSize, double carPassingTime, Random interSection_rand){
    super(nm);
    this.addOutport("out");

    ViewableAtomic generator_eastMoving = new generator_eastMovingTraffic("generator_eastMoving",eastMoving_lambda, eastMoving_rand);
    ViewableAtomic generator_westMoving = new generator_westMovingTraffic("generator_westMoving",westMoving_lambda, westMoving_rand);
    trafficControlModel IntersectionModel = new trafficControlModel("trafficControlModel", tfcState, sgm, remainGreenT, 
    		westSideQSize, eastSideQSize, carPassingTime, interSection_rand);
    trand = new transducer_DA();

     add(generator_eastMoving);
     add(generator_westMoving);
     add(IntersectionModel);
     add(trand);

     addCoupling(generator_eastMoving,"out",IntersectionModel,"eastMoving_in");
     addCoupling(generator_westMoving,"out",IntersectionModel,"westMoving_in");
     addCoupling(generator_eastMoving,"out",trand,"eastMoving_in");
     addCoupling(generator_westMoving,"out",trand,"westMoving_in");
     addCoupling(IntersectionModel,"eastMoving_out",trand,"eastMoving_out");
     addCoupling(IntersectionModel,"westMoving_out",trand,"westMoving_out");
     
     addCoupling(trand,"out",IntersectionModel,"report");
     addCoupling(IntersectionModel,"report_out",trand,"report_in");
}




    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(625, 341);
        if((ViewableComponent)withName("generator_streetB")!=null)
             ((ViewableComponent)withName("generator_streetB")).setPreferredLocation(new Point(24, 231));
        if((ViewableComponent)withName("transducer_dataSaving")!=null)
             ((ViewableComponent)withName("transducer_dataSaving")).setPreferredLocation(new Point(333, 191));
        if((ViewableComponent)withName("generator_streetA")!=null)
             ((ViewableComponent)withName("generator_streetA")).setPreferredLocation(new Point(51, 52));
        if((ViewableComponent)withName("IntersectionModel")!=null)
             ((ViewableComponent)withName("IntersectionModel")).setPreferredLocation(new Point(292, 49));
    }
}
