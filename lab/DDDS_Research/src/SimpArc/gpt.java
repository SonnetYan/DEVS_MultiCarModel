/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 08-15-02
 */


package SimpArc;

import java.awt.*;
import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

public class gpt extends ViewableDigraph{


public gpt(){
    super("gpt");

    ViewableAtomic g = new genr("g",10);
    ViewableAtomic p = new proc("p",5);
    ViewableAtomic t = new transd("t",70);

     add(g);
     add(p);
     add(t);

    addInport("in");
    addInport("start");
    addInport("stop");
    addOutport("out");
    addOutport("result");

     addTestInput("start",new entity());
     addTestInput("stop",new entity());

     addCoupling(this,"in",g,"in");

     addCoupling(this,"start",g,"start");
     addCoupling(this,"stop",g,"stop");

     addCoupling(g,"out",p,"in");

     addCoupling(g,"out",t,"ariv");
     addCoupling(p,"out",t,"solved");
     addCoupling(t,"out",g,"stop");


     addCoupling(p,"out",this,"out");
     addCoupling(t,"out",this,"result");

     initialize();
     showState();

    preferredSize = new Dimension(484, 145);
    g.setPreferredLocation(new Point(13, 18));
    p.setPreferredLocation(new Point(195, 18));
    t.setPreferredLocation(new Point(193, 80));
}


    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(582, 281);
        if((ViewableComponent)withName("p")!=null)
             ((ViewableComponent)withName("p")).setPreferredLocation(new Point(189, 55));
        if((ViewableComponent)withName("t")!=null)
             ((ViewableComponent)withName("t")).setPreferredLocation(new Point(260, 203));
        if((ViewableComponent)withName("g")!=null)
             ((ViewableComponent)withName("g")).setPreferredLocation(new Point(41, 118));
    }
}
