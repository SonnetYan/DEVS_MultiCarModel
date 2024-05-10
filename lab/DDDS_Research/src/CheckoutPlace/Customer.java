package CheckoutPlace;
import simView.*;


import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import util.*;
import statistics.*;

public class Customer extends ViewableAtomic {
	protected int itemMax = 15;
	protected int activeTime;
	protected int CustomerCount =0,emerCount=0;
	protected rand r;
	public Customer()
	{
		this("Customer");
	}
	
	public Customer(String name){
		   super(name);
		   if(name.equals("Customer"))
			   activeTime = 10;
		   else
			   activeTime=40;
		   addInport("IN");
		   addOutport("OUT");
		}
	
	public void initialize(){
		   holdIn("active", activeTime);
		   r = new rand(123987979);
		   if(name.equals("Customer"))
		   CustomerCount=1;
		   else
		   emerCount=1;
		}
	
	public void  deltext(double e,message x)
	{
	   Continue(e);
	   for (int i=0; i< x.getLength();i++){
	     if (messageOnPort(x, "IN", i)) {
	       passivate();
	     }
	   }
	}
	
	public void  deltint( )
	{
		
	if(phaseIs("active")){
	   if(name.equals("Customer"))
		   		CustomerCount++; 
		   else
			  emerCount++;
	   holdIn("active", Math.ceil(activeTime+r.uniform(10)));
	}
	else passivate();
	}
	
	public message  out( )
	{
	   message  m = new message();
	   content con;
	   //System.out.println(name);
	   if(name.equals("Customer"))
		   con = makeContent("OUT", new customerEntity("Customer" + CustomerCount, 1 + r.uniform(itemMax), 1, false));
	   else
		   con = makeContent("OUT", new customerEntity("VIPCustomer" + emerCount, 1 + r.uniform(itemMax), 2 , false));;
	   m.add(con);
	  return m;
	}
	
}
