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

public class TestHierarchy extends testGeneral
{
    protected coordinator cs;
    protected static digraph testDig;
    public TestHierarchy(coordinator cs)
    {
        super("GenDevsTest.testHierarchy");
        this.cs = cs;
    }

    public static void main(String[ ] args)
    {
        testDig = new HierarModel();
        coordinator cs = new coordinator(testDig);
        cs.initialize();
        message m = new message();
        //
        m.add(new content("in", new entity()));
        //
        cs.simInject(4.0, m);
        cs.simulate(150);
    }
}
