/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 


package GenDevsTest;

import genDevs.modeling.*;
import genDevs.simulation.*;
import java.awt.Color;
import GenCol.*;
import java.util.*;
import javax.swing.*;
import simView.*;

public class RegisterPredict extends ViewableAtomic
{
    protected double interval;
    protected int latestPrediction;
    public RegisterPredict()
    {
        this("registerPredict", 100);
        addTestInput("in", new entity("pulse"), 0);
        addTestInput("in", new entity("pulse"), 1);
        addTestInput("none", new entity("pulse"), 10);
    }

    public RegisterPredict(String name, double interval)
    {
        super(name);
        this.interval = interval;
        addInport("in");
        addOutport("out");
    }

    public void reset()
    {
        latestPrediction = 0;
        passivateIn("receptive");
    }

    public void initialize()
    {
        reset();
        super.initialize();
    }

    public void  deltext(double e, message x)
    {
        Continue(e);
        if (phaseIs("receptive")) {
            for (int i = 0; i < x.getLength(); i++)
                if (messageOnPort(x, "in", i)) {
                    entity ent = x.getValOnPort("in", i);
                    latestPrediction = Integer.parseInt(ent.getName()); //
                    int response = JOptionPane.showConfirmDialog
                        (null, "prediction is " + latestPrediction + " continue?", "We have a prediction:",
                            JOptionPane.YES_NO_OPTION);
                    if (response != 0) {
                        System.out.println("simulation terminated at your request");
                        //dispose();
                        System.exit(1);
                    }
                    passivateIn("receptive");
                }
        } else  for (int i = 0; i < x.getLength(); i++)
                if (messageOnPort(x, "reset", i))
                    initialize();
    }

    public void  deltint()
    {
        passivate();
    }

    public message    out()
    {
        message   m = new message();
        return m;
    }

    public void showState()
    {
        super.showState();
        System.out.println(stringState());
    }

    public String stringState()
    {
        return "\n" + "latestPrediction :" + latestPrediction;
    }
}
