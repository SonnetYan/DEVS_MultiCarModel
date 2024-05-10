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
import GenCol.*;

public class divideCoord extends Coord{

int num_procs, num_results;

public divideCoord(String   name){
super(name);
num_procs = 0;
num_results = 0;
}

public divideCoord(){
super("divideCoord");

//CAUTION: start with port "setup" to test

addTestInput("setup",new entity(""));
addTestInput("in",new entity("val"));
//addTestInput("x",new entity("val"));
addTestInput("xAll",new entity("val"));


}

public void initialize(){
     phase = "passive";
     sigma = INFINITY;
     job = null;
     super.initialize();;
 }

public void showState(){
    super.showState();
    System.out.println("num_procs: " + num_procs);
    System.out.println("num_results: " + num_results);
}

//protected void add_procs (proc   p){
protected void add_procs (devs  p){
num_procs++;
num_results++;
}

public void  deltext(double e,message   x)
{

Continue(e);

if (phaseIs("passive"))
{
 for (int i=0; i< x.size();i++)
 if (messageOnPort(x,"setup",i))
     add_procs(new proc("p",1000));
}

if (phaseIs("passive"))
{
 for (int i=0; i< x.size();i++)
 if (messageOnPort(x,"in",i))
   {
   job = x.getValOnPort("in",i);
       num_results = num_procs;
       holdIn("send_y",0);
   }

}
// job Pairs returned on port x


else if (phaseIs("busy")){
 for (int i=0; i< x.size();i++)
 if (messageOnPort(x,"x",i) ||
       messageOnPort(x,"xAll",i))
    {
      num_results--;
      if (num_results == 0)
           holdIn("send_out",0);
    }
}

//showState();
}

public void  deltint( )
{
if (phaseIs("send_y"))
     passivateIn("busy");
else passivate();
//showState();
}

public message    out( )
{
message   m = new message();
if (phaseIs("send_out"))
  m.add( makeContent("out",job));
else if (phaseIs("send_y")){
   m.add(makeContent("y",job));  //use in divideAndConquer
   m.add(makeContent("yAll",job));//use with multEnts
}
return m;
}



}


