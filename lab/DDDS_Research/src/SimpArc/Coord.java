/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 


package SimpArc;

import simView.*;

import genDevs.modeling.*;
import genDevs.simulation.*;


public class Coord extends proc{

public Coord(String name)
{
super(name,100);
addInport("setup");
addInport("x");
addOutport("y");
}

public Coord(){this("Coord");}

public void initialize(){
    super.initialize();
}

//protected void add_procs(proc p){
    protected void add_procs(devs p){
System.out.println("Default in Coord is being used");
}
}