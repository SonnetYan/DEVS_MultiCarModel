package CheckoutPlace;

import simView.*;
import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import util.*;
import statistics.*;

public class Casher extends ViewableAtomic {
	protected int processTime;
	protected int count = 0;
	protected String currCustomer;
	entity message, currentJob;

	public Casher() {
		this("Casher");
	}

	public Casher(String name) {
		super(name);
		addInport("IN");
		addOutport("OUT");
	}

	public void initialize() {
		holdIn("passive", INFINITY);
		count++;
	}

	public void deltext(double e, message x) {
		double rand = (Math.random() * 10) + 10;
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			message = x.getValOnPort("IN", i);
			currentJob = message;
			if (messageOnPort(x, "IN", i)) {
				currCustomer = message.getName();
				//System.out.print(currCustomer);
				processTime = (int) ((customerEntity) message).getItems()*10;
				holdIn("Active",processTime);
			}
		}
	}

	public void deltint() {

		if (phaseIs("active")) {
			count++;
			holdIn("passive", INFINITY);
		} else
			passivate();
	}

	public message out() {
		//System.out.println(" OUT count " + count);
		message m = new message();
		content con;
		if (name.equals("CasherOne"))
			//con = makeContent("OUT", new entity("CasherOne" + currCustomer));
			con = makeContent("OUT", new customerEntity("CasherOne" + currCustomer,((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),((customerEntity) currentJob).isProcessed()));
		else if (name.equals("CasherTwo"))
			//con = makeContent("OUT", new entity("CasherTwo" + currCustomer));
			con = makeContent("OUT", new customerEntity("CasherTwo" + currCustomer,((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),((customerEntity) currentJob).isProcessed()));

		else if (name.equals("CasherThree"))
			//con = makeContent("OUT", new entity("CasherThree" + currCustomer));
			con = makeContent("OUT", new customerEntity("CasherThree" + currCustomer,((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),((customerEntity) currentJob).isProcessed()));

		else if (name.equals("CasherFour"))
			//con = makeContent("OUT", new entity("CasherThree" + currCustomer));
			con = makeContent("OUT", new customerEntity("CasherFour" + currCustomer,((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),((customerEntity) currentJob).isProcessed()));
		else
			//con = makeContent("OUT", new entity("CasherThree" + currCustomer));
			con = makeContent("OUT", new customerEntity("CasherFive" + currCustomer,((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),((customerEntity) currentJob).isProcessed()));

		m.add(con);
		return m;
	}

}