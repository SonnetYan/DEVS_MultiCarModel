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
import genDevs.plots.*;
import genDevs.simulation.realTime.*;
import simView.*;

public class OrderedDigraphTest extends ViewableDigraph
{
    public OrderedDigraphTest()
    {
        super("stats");

        // add a1
        atomic a1 = new JobQueue("a1", 1);
        add(a1);

        // add d1 and its children
        digraph d1 = new ViewableDigraph("d1");
        atomic a2 = new JobQueue("a2", 1);
        atomic a3 = new JobQueue("a3", 1);
        d1.add(a2);
        d1.add(a3);
        add(d1);

        // add d2 and its children to d1
        digraph d2 = new digraph("d2");
        atomic a4 = new JobQueue("a4", 1);
        atomic a5 = new JobQueue("a5", 1);
        d2.add(a4);
        d2.add(a5);
        d1.add(d2);
    }

    public static void main(String args[])
    {
        digraph digraph = new OrderedDigraphTest();
        TunableCoordinator coord = new TunableCoordinator(digraph);
        coord.initialize();
        coord.simulate(100);
    }
}



