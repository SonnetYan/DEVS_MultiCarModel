/*      Copyright 2002 Arizona Board of regents on behalf of 
 *                  The University of Arizona 
 *                     All Rights Reserved 
 *         (USE & RESTRICTION - Please read COPYRIGHT file) 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 


package SimpArc;

import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import simView.*;


public class procName extends proc {//ViewableAtomic{//

public procName(){
super("procName",20);
addInport("none");
addInport("inName");
addOutport("outName");
addTestInput("inName",new Pair(new procName("procName",20),
                 new entity("job1")));
addTestInput("inName",new Pair(new procName("other",20),
                 new entity("job2")));
addTestInput("none",new entity("job"));
}


public procName(String  name,double  Processing_time){
super(name, Processing_time);
addInport("none");
addInport("inName");
addOutport("outName");
processing_time = Processing_time;
}

public void initialize(){
job = new job("nullJob");
super.initialize();
 }



public void  deltext(double e,message   x)
{

    Continue(e);
if (phaseIs("passive"))
{
  for (int i=0; i< x.size();i++)
    if (messageOnPort(x,"inName",i))
     {
    entity   ent =
     x.getValOnPort("inName",i);
    Pair   pr = (Pair)ent;
    entity en = (entity)pr.getKey();
    procName   pn = (procName  )en;

   if (this.equals(pn))
      {
      job = (entity)pr.getValue();
      holdIn("busy",processing_time);
      }
    }
}
}

public message    out( )
{
message   m = new message();
if (phaseIs("busy")){
content con = makeContent("outName",new Pair(this,job));
m.add(con);
}
return m;
}

}

