/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 3.0 Beta
 *  Date       : 02-22-03
 */

package simView;
import GenCol.*;
import genDevs.modeling.*;
/**
 * the interface needed for displaying variable structure DEVS
 *
 * @author  Xiaolin Hu
 */

public interface variableStructureViewer{
   void couplingAdded(IODevs src, String p1, IODevs dest, String p2);
   void couplingRemoved(IODevs src, String p1, IODevs dest, String p2);
   void modelAdded(ViewableComponent iod, ViewableDigraph parent);
   void modelRemoved(ViewableComponent iod, ViewableDigraph parent);
}