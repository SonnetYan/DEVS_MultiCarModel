package devs.traffic;

import GenCol.entity;
import genDevs.modeling.atomic;
import genDevs.modeling.content;
import genDevs.modeling.message;
import simView.ViewableAtomic;

public class ObservationCollect extends ViewableAtomic{
	private double timeInterval;
	private double observation[];
	
	public ObservationCollect() {
		super("ObservationCollect");
	}
	
	public ObservationCollect(String name) {
		super(name);
		 addInport("observationIn");
		 addOutport("collectOut");
	}
	
	public ObservationCollect(double timeInterval, int size) {
		this();
		addInport("observationIn");
		addOutport("collectOut");
		this.timeInterval = timeInterval;
		observation = new double[size];
	}
	
	public ObservationCollect(ObservationCollect other) {
		this.timeInterval = other.timeInterval;	
		observation = new double[other.observation.length];
	}
	
	@Override
	public void initialize() {
		holdIn(("active"), timeInterval);
	}
	
	@Override
	public void deltint() {
		if (phaseIs("active")) {
			holdIn(("active"), timeInterval);
		}
	}
	
	@Override
	public void deltext(double e, message x) {
		Continue(e);
   		for (int i=0; i< x.getLength();i++){
		   	if (messageOnPort(x,"observationIn",i)) {
		   		entity ent =  (entity) x.getValOnPort("observationIn",i);
		  	}
		}
		
	}
	
	@Override
	public void deltcon(double e, message x) {
		// TODO Auto-generated method stub
		deltext(e,x);
		deltint();
	}
	
	@Override
	public message out() {
		if (phaseIs("active")) {	
			message  m = new message();
			content con;
			//send message to collect observation
			con = makeContent("collectOut", new entity("collect") );
			m.add(con);
			return m;
		}
		return null;
	}
	
}
