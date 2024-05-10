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

import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

public class siso extends classic{

public void AddTestPortValue(double input){
    addTestInput("in",new doubleEnt((double)input));
}

public siso(){
addInport("in");
addOutport("out");
AddTestPortValue(0);
}

public siso(String name){
   super(name);
 AddTestPortValue(0);
}


public void  Deltext(double e,double input){
}  //expects single real value

public void  Deltext(double e,content con){
          doubleEnt fe = (doubleEnt)con.getValue();
            Deltext(e,fe.getv());
}



public double sisoOut(){
    return 0; //produces single real value
}

public message  out( )
{

   message  m = new message();

   content con = makeContent("out",new doubleEnt(sisoOut()));

   m.add(con);

  return m;
}


}

