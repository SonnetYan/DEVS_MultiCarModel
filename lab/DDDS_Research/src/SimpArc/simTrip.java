/*      Copyright 1999 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA2.6
 *  Date       : 04-15-00
 */

package SimpArc;

import java.awt.*;
import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

public class simTrip extends ViewableDigraph {


public simTrip(){
    super("simTrip");

    atomic g = new simulator("g",new genr("g",2000));
  atomic p = new simulator("p",new proc("p",1000));//2000
//     atomic p = new simulator("p",new pipeSimple("p",300));
    atomic t = new simulator("t",new transd("t",20000));
    atomic c = new sCoordinator("c",g,p,t);


     add(c);
     add(g);
     add(p);
     add(t);

     addCoupling(g,"outTN",c,"getTN");
     addCoupling(p,"outTN",c,"getTN");
     addCoupling(t,"outTN",c,"getTN");

     addCoupling(g,"sendOut",c,"getOutFromG");
     addCoupling(p,"sendOut",c,"getOutFromP");
     addCoupling(t,"sendOut",c,"getOutFromT");

     addCoupling(c,"nextTN",g,"nextTN");
     addCoupling(c,"getOut",g,"getOut");

     addCoupling(c,"nextTN",p,"nextTN");
     addCoupling(c,"getOut",p,"getOut");

     addCoupling(c,"nextTN",t,"nextTN");
     addCoupling(c,"getOut",t,"getOut");



     addCoupling(c,"applyDeltG",g,"applyDelt");
     addCoupling(c,"applyDeltP",p,"applyDelt");
     addCoupling(c,"applyDeltT",t,"applyDelt");




     showState();



}


    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(611, 324);
        if((ViewableComponent)withName("t")!=null)
             ((ViewableComponent)withName("t")).setPreferredLocation(new Point(402, 193));
        if((ViewableComponent)withName("p")!=null)
             ((ViewableComponent)withName("p")).setPreferredLocation(new Point(195, 251));
        if((ViewableComponent)withName("c")!=null)
             ((ViewableComponent)withName("c")).setPreferredLocation(new Point(153, 32));
        if((ViewableComponent)withName("g")!=null)
             ((ViewableComponent)withName("g")).setPreferredLocation(new Point(-17, 177));
    }
}
