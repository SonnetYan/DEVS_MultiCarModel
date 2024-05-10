/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package simView;

import java.awt.*;

public interface ComponentView
{
    Point getPreferredLocation();
    Dimension getPreferredSize();
    Point getPortLocation(String portName);
    void injectAll();
    ViewableComponent getViewableComponent();
}