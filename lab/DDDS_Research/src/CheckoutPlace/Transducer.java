package CheckoutPlace;
import GenCol.DEVSQueue;
import GenCol.entity;
import genDevs.modeling.content;
import genDevs.modeling.message;
import simView.ViewableAtomic;
public class Transducer extends ViewableAtomic {
	String IN = "IN", OUT = "OUT";
	String ACTIVE = "ACTIVE", OFF = "OFF", ON = "ON", BUSY = "BUSY";
	int totalCustomer=0;
	entity message, currentJob = null;
	String currCustomer = "";
	int processTime;
	long start, finish;
	public Transducer() {
		this("Transducer");
	}

	public Transducer(String name) {
		super(name);
		addInport(IN);
		addOutport(OUT);
	}
	
	public void initialize() {
		long start = System.currentTimeMillis();
		holdIn("passive", INFINITY);
	}
	
	public void deltext(double e, message x) {
		Continue(e);
		for (int i = 0; i < x.size(); i++) {
			if (messageOnPort(x, IN, i)) {
				message = x.getValOnPort(IN, i);
				currentJob = message;
				holdIn("active",0);
				totalCustomer++;
				currCustomer = message.getName();

			}
		}
	}
	
	public void deltint() {
		holdIn("passive", INFINITY);
	}
	
	public message out() {
		message m = new message();
		processTime = (int) ((customerEntity) message).getItems()*10;
		long finish = System.currentTimeMillis();
		long time = finish - start;
		m.add(makeContent(OUT, new customerEntity(currentJob.getName(),((customerEntity) currentJob).getItems(),((customerEntity) currentJob).getPriority(),true)));
		System.out.println(currentJob.getName()+" "+ processTime + " Seconds" + "completed after " + time + " seconds");
		return m;

	}
	
}
