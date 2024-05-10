package CheckoutPlace;
import simView.*;import java.lang.*;
import java.util.LinkedList;

import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import util.*;
import statistics.*;


public class Receptionist5 extends ViewableAtomic {
	protected int activeTime = 10;
	protected int count = 0;
	protected LinkedList<String> list = new LinkedList<>();
	protected DEVSQueue q1, q2;
	entity message, customer, currentJob = null;
	protected boolean docOne = true, docTwo = true, docThree = true, docFour = true, docFive = true;
	String outOne = "1OUT", outTwo = "2OUT", outThree = "3OUT", outFour = "4OUT" , outFive = "5OUT";
	public Receptionist5()
	{
		this("Receptionist");
	}

	public Receptionist5(String name){
		   super(name);
		   addInport("IN");
		   addOutport("1OUT");
		   addOutport("2OUT");
		   addOutport("3OUT");
		   addOutport("4OUT");
		   addOutport("5OUT");
		}
	
	public void initialize(){
		q1 = new DEVSQueue(); //Normal Queue
		q2 = new DEVSQueue(); //VIP Queue
		   holdIn("active", INFINITY);
		}
	
	public boolean isDoctorFree()
	{
		if(docOne || docTwo || docThree || docFour || docFive)
			return true;
		return false;
	}
	
	public void  deltext(double e,message x)
	{
	   Continue(e);
	   for (int i=0; i< x.getLength();i++){
	     if (messageOnPort(x, "IN", i)) {
	    	 message = x.getValOnPort("IN", i);
	    	 String temp = message.getName();
			 //q.add(message);
			 //currentJob = message;
			 boolean _process = ((customerEntity) message).isProcessed();
			 int _priority = ((customerEntity) message).getPriority();
			 if (!_process){
				 if(_priority == 1) {
				 	count++;
					 System.out.println("Customer Add");
					 //list.addLast(temp);
					 q1.add(message);
					 if(phaseIs("active") && isDoctorFree())
						 holdIn("docFree",0);
					 else
						 holdIn("active",INFINITY);
				 }

				 else
				 {
				 	count++;
					 System.out.println("VIP Client Added");
					 //list.addFirst(temp);
					 q2.add(message);
					 if(phaseIs("active") && isDoctorFree())
						 holdIn("docFree",0);
					 else
						 holdIn("active",INFINITY);
				 }
			 }
			 else {
				 if(temp.indexOf("CasherOne")==0){
				 	System.out.println("doc1reset");
				 	docOne=true;
				 }
				 else if(temp.indexOf("CasherTwo")==0)
				 {
					 System.out.println("doc2reset");
					 docTwo = true;
				 }
				 else if(temp.indexOf("CasherThree")==0)
				 {
					 System.out.println("doc3reset");
					 docThree = true;
				 }
				 else if(temp.indexOf("CasherFour")==0)
				 {
					 System.out.println("doc4reset");
					 docFour = true;
				 }
				 else if(temp.indexOf("CasherFive")==0)
				 {
					 System.out.println("doc5reset");
					 docFive = true;
				 }
			 }
	    	 //System.out.println(list);
	     }
	   }
	}
	
	public void  deltint( )
	{

	if(phaseIs("docFree")){
	   holdIn("active",INFINITY);
	}
	else passivate();
	}
	
	public message out( ) {
		message m = new message();
		content con;
		System.out.println("sending msg");
		if (docOne) {
			docOne = false;
			if (!q2.isEmpty()) {
				System.out.print("VIP Job-cash1");
				currentJob = (entity) (q2.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				//list.pop();
				con = makeContent(outOne, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			} else {
				System.out.println("Norm Job-cash1");
				currentJob = (entity) (q1.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				con = makeContent(outOne, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			}
		} else if (docTwo) {
			docTwo = false;
			if (!q2.isEmpty()) {
				System.out.print("VIP Job-cash2");
				currentJob = (entity) (q2.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				//list.pop();
				con = makeContent(outTwo, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			} else {
				System.out.print("Norm Job-cash2");
				currentJob = (entity) (q1.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				con = makeContent(outTwo, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			}
		}else if (docThree) {
			docThree = false;
			if (!q2.isEmpty()) {
				System.out.print("VIP Job-cash3");
				currentJob = (entity) (q2.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				//list.pop();
				con = makeContent(outThree, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			} else {
				System.out.print("Norm Job-cash3");
				currentJob = (entity) (q1.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				con = makeContent(outThree, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			}
		}else if (docFour) {
			docFour = false;
			if (!q2.isEmpty()) {
				System.out.print("VIP Job-cash4");
				currentJob = (entity) (q2.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				//list.pop();
				con = makeContent(outFour, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			} else {
				System.out.print("Norm Job-cash4");
				currentJob = (entity) (q1.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				con = makeContent(outFour, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			}
		}
		else {
			docFive = false;
			if (!q2.isEmpty()) {
				System.out.print("VIP Job-cash5");
				currentJob = (entity) (q2.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				//list.pop();
				con = makeContent(outFive, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			} else {
				System.out.print("Norm Job-cash5");
				currentJob = (entity) (q1.pop());
				//int priority = ((customerEntity) currentJob).getPriority();
				con = makeContent(outFive, new customerEntity(currentJob.getName(), ((customerEntity) currentJob).getItems(), ((customerEntity) currentJob).getPriority(), ((customerEntity) currentJob).isProcessed()));
			}
		}
			m.add(con);
			return m;
	}
	public String getTooltipText() {
		if (currentJob != null)
			return super.getTooltipText() + "\n Normal queue:" + q1.size() +
					"\n VIP Queue:" + q2.size() +
					"\n my current customer is:" + currentJob.toString() +
					"\n Customers Serviced: " + count;

		else return "initial value";
	}
}