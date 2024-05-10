/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 


package GenDevsTest;

import GenCol.*;
import java.util.*;
import genDevs.modeling.*;
import genDevs.simulation.*;

public class TestActivity extends testGeneral
{
    protected coupledSimulator as;
    protected static ActModel g;
    public TestActivity(coupledSimulator as)
    {
        super("GenDevsTest.testActivity");
        this.as = as;
    }

    public static void main(String[ ] args)
    {
        g = new ActModel("second", 10);
        //g = new actModel("first",10);
        coupledSimulator as = new coupledSimulator(g);
        TestActivity t = new TestActivity(as);
        t.applyTests("activitySimulation of actModel is correct");
    }

    public boolean testSimulation()
    {
        description = "simulation of activity";
        precondition = new Boolean(true);
        as.initialize();
        as.simulate(2);      //consume 1*10 seconds
        return g.toString().startsWith("phase :" + "passive");
    }
}
