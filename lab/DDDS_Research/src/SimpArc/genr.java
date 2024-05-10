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

public class genr extends ViewableAtomic{


  protected double int_arr_time;
  protected int count;

  public genr() {this("genr", 30);}

public genr(String name,double Int_arr_time){
   super(name);
   //addInport("in");
   addOutport("out");
   addInport("stop");
   addInport("start");
   int_arr_time = Int_arr_time ;

    addTestInput("start",new entity(""));
    addTestInput("stop",new entity(""));
}

public void initialize(){
 //  holdIn("active", int_arr_time);
   passivate();

    //  phase = "passive";
   //  sigma = INFINITY;
     count = 0;
     super.initialize();
 }


public void  deltext(double e,message x)
{
Continue(e);
if(phaseIs("passive")){
   for (int i=0; i< x.getLength();i++)
      if (messageOnPort(x,"start",i))
         holdIn("active",int_arr_time);
}
 if(phaseIs("active"))
   for (int i=0; i< x.getLength();i++)
      if (messageOnPort(x,"stop",i))
         phase = "finishing";
}


public void  deltint( )
{
/*
System.out.println(name+" deltint count "+count);
System.out.println(name+" deltint int_arr_time "+int_arr_time);
System.out.println(name+" deltint tL "+tL);
System.out.println(name+" deltint tN "+tN);
*/


if(phaseIs("active")){
   count = count +1;
   holdIn("active",int_arr_time);
}
else passivate();
}

public message  out( )
{

//System.out.println(name+" out count "+count);

   message  m = new message();
   content con = makeContent("out",
            new entity("job" + count));
   m.add(con);

  return m;
}

public void showState(){
    super.showState();
    System.out.println("int_arr_t: " + int_arr_time);
}


}

