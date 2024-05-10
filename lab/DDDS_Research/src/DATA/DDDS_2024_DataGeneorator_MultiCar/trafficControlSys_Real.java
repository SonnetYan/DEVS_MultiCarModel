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

import java.awt.*;


public class trafficControlSys_Real extends ViewableDigraph{


public trafficControlSys_Real(){
    this("trafficControlSys");
}

public trafficControlSys_Real(String nm){
    super(nm);
    intersectionSysConstruct();
}

public void intersectionSysConstruct(){

    this.addOutport("out");
    
    ViewableAtomic generator_eastMoving = new generator_eastMovingTraffic("generator_eastMoving");
    ViewableAtomic generator_westMoving = new generator_westMovingTraffic("generator_westMoving");
    //trafficControlModel IntersectionModel = new trafficControlModel("oneWayTrafficControl");
    trafficControlModel IntersectionModel = new trafficControlModel("oneWayTrafficControl", 0, 0);
    ViewableAtomic trand = new transducer_real_dataSaving();

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
     addCoupling(IntersectionModel,"signalChangeOut",trand,"signalChange_in");

     addCoupling(trand,"out",IntersectionModel,"report");
     addCoupling(IntersectionModel,"report_out",trand,"report_in");
}


    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(721, 436);
        if((ViewableComponent)withName("generator_eastMoving")!=null)
             ((ViewableComponent)withName("generator_eastMoving")).setPreferredLocation(new Point(-9, 58));
        if((ViewableComponent)withName("generator_westMoving")!=null)
             ((ViewableComponent)withName("generator_westMoving")).setPreferredLocation(new Point(8, 261));
        if((ViewableComponent)withName("transducer_dataSaving")!=null)
             ((ViewableComponent)withName("transducer_dataSaving")).setPreferredLocation(new Point(390, 267));
        if((ViewableComponent)withName("oneWayTrafficControl")!=null)
             ((ViewableComponent)withName("oneWayTrafficControl")).setPreferredLocation(new Point(171, 135));
    }
}
