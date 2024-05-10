/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 3.0 Beta
 *  Date       : 02-22-03
 */
package simView;

import java.awt.*;
import java.util.*;
import GenCol.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import genDevs.simulation.realTime.*;
import util.*;


public class LevelView {

  ViewableDigraph digraph;
  SimView sv;

  public LevelView(ViewableDigraph digraph_) {
    this.digraph = digraph_;
    digraph.setBlackBox(false);
    String packName = digraph_.getClass().getPackage().getName();
    String className = digraph_.getClass().getName();
    System.out.println("New window  0pened. Iminside the levelView class");
    sv = new SimView(digraph, packName,className, false,0);//false indiacates the explosion of the digraph
  }
}








//}//end LevelView