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

public class SimActivity extends activity
{
    protected double tN;
    protected devs myModel;
    public SimActivity(String name, double pt)
    {
        super(name, pt);
    }

    public void run()
    {
        try {
            sleep((long) getProcessingTime() * 1000);
        } catch (InterruptedException e) {
            return;
        }
        returnTheResult(new entity("ActivityResult"));
    }

    public void kill()
    {
        interrupt();
    }
}
